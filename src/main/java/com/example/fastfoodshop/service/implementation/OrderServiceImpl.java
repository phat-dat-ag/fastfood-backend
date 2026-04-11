package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.constant.OrderConstant;
import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.dto.OrderStatsDTO;
import com.example.fastfoodshop.entity.Address;
import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.NoteType;
import com.example.fastfoodshop.enums.UserRole;
import com.example.fastfoodshop.enums.OrderStatus;
import com.example.fastfoodshop.enums.PaymentMethod;
import com.example.fastfoodshop.enums.PaymentStatus;
import com.example.fastfoodshop.enums.AuthorType;
import com.example.fastfoodshop.enums.OrderQueryType;
import com.example.fastfoodshop.exception.order.CODPaymentLimitException;
import com.example.fastfoodshop.exception.order.InvalidOrderStatusException;
import com.example.fastfoodshop.exception.order.ForbiddenException;
import com.example.fastfoodshop.exception.order.OrderAlreadyCancelledException;
import com.example.fastfoodshop.exception.order.OrderAmountExceededException;
import com.example.fastfoodshop.exception.order.OrderCannotBeCancelledException;
import com.example.fastfoodshop.exception.order.PaymentFailedException;
import com.example.fastfoodshop.exception.order.PaymentNotAllowedException;
import com.example.fastfoodshop.exception.order.PaymentNotCompletedException;
import com.example.fastfoodshop.exception.order.OrderNotFoundException;
import com.example.fastfoodshop.exception.order.AccessDeniedException;
import com.example.fastfoodshop.projection.OrderStatsProjection;
import com.example.fastfoodshop.repository.OrderRepository;
import com.example.fastfoodshop.request.OrderCreateRequest;
import com.example.fastfoodshop.request.OrderStatusUpdateRequest;
import com.example.fastfoodshop.response.cart.CartDetailResponse;
import com.example.fastfoodshop.dto.OrderDTO;
import com.example.fastfoodshop.response.order.OrderPageResponse;
import com.example.fastfoodshop.response.order.OrderResponse;
import com.example.fastfoodshop.response.order.OrderStatsResponse;
import com.example.fastfoodshop.response.order.OrderUpdateResponse;
import com.example.fastfoodshop.service.CartService;
import com.example.fastfoodshop.service.OrderNoteService;
import com.example.fastfoodshop.service.OrderDetailService;
import com.example.fastfoodshop.service.ProductService;
import com.example.fastfoodshop.service.PromotionService;
import com.example.fastfoodshop.service.UserService;
import com.example.fastfoodshop.service.AddressService;
import com.example.fastfoodshop.service.PaymentService;
import com.example.fastfoodshop.service.OrderService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final CartService cartService;
    private final OrderNoteService orderNoteService;
    private final OrderDetailService orderDetailService;
    private final ProductService productService;
    private final PromotionService promotionService;
    private final UserService userService;
    private final AddressService addressService;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

    public Order findOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException(orderId)
        );
    }

    private void checkAllActivatedProducts(List<CartDTO> cartDTOs) {
        for (CartDTO cartDTO : cartDTOs) {
            productService.checkActivatedCategoryAndActivatedProduct(cartDTO.product().id());
        }
    }

    private void checkOrderTotalAmountLimitOrThrow(int orderTotalAmount) {
        if (orderTotalAmount > OrderConstant.MAX_ORDER_TOTAL_AMOUNT)
            throw new OrderAmountExceededException();
    }

    private void checkCashOnDeliveryLimitOrThrow(int orderTotalAmount) {
        if (orderTotalAmount > OrderConstant.CASH_ON_DELIVERY_MAX_AMOUNT)
            throw new CODPaymentLimitException();
    }

    private CartDetailResponse prepareCart(String phone, OrderCreateRequest orderCreateRequest) {
        CartDetailResponse cartDetailResponse = cartService.getCartDetailByUser(
                phone, orderCreateRequest.promotionCode(), orderCreateRequest.addressId()
        );

        checkAllActivatedProducts(cartDetailResponse.carts());

        checkOrderTotalAmountLimitOrThrow(cartDetailResponse.totalPrice());

        if (orderCreateRequest.paymentMethod() == PaymentMethod.CASH_ON_DELIVERY) {
            checkCashOnDeliveryLimitOrThrow(cartDetailResponse.totalPrice());
        }

        return cartDetailResponse;
    }

    private void buildOrder(Order order, CartDetailResponse cartDetailResponse, User user, Address address) {
        order.setUser(user);
        order.setAddress(address);

        order.setOrderStatus(OrderStatus.PENDING);
        order.setPlacedAt(LocalDateTime.now());
        order.setPaymentStatus(PaymentStatus.PENDING);

        order.setOriginalPrice(cartDetailResponse.originalPrice());
        order.setSubtotalPrice(cartDetailResponse.subtotalPrice());
        order.setDeliveryFee(cartDetailResponse.deliveryFee());
        order.setTotalPrice(cartDetailResponse.totalPrice());
    }

    private void applyOrderPromotion(Order order, CartDetailResponse cartDetailResponse) {
        if (cartDetailResponse.promotion() != null) {
            Promotion promotion = promotionService.findPromotionOrThrow(
                    cartDetailResponse.promotion().id()
            );

            promotionService.increasePromotionUsageCount(promotion.getId());

            log.debug(
                    "[OrderService] Applied promotion id={} to order id={}",
                    promotion.getId(), order.getId()
            );

            order.setPromotion(promotion);
        }

        log.debug("[OrderService] Did not apply promotion for order id={}", order.getId());
    }

    private Order buildOrderEntity(
            User user, Address address,
            OrderCreateRequest orderCreateRequest, CartDetailResponse cartDetailResponse
    ) {
        Order order = new Order();

        buildOrder(order, cartDetailResponse, user, address);
        order.setPaymentMethod(orderCreateRequest.paymentMethod());

        applyOrderPromotion(order, cartDetailResponse);

        return order;
    }

    private void addUserNoteIfPresent(Order order, String userNote) {
        if (userNote != null && !userNote.isBlank()) {
            log.debug("[OrderService] Added note for order id={}", order.getId());
            orderNoteService.createOrderNote(order, NoteType.USER_NOTE, userNote, AuthorType.USER);
        }

        log.debug("[OrderService] Did not write note for order id={}", order.getId());
    }

    private void createOrderDetails(Order order, CartDetailResponse cartDetailResponse) {
        List<CartDTO> cartDTOs = cartDetailResponse.carts();

        for (CartDTO cartDTO : cartDTOs) {
            orderDetailService.createOrderDetail(cartDTO, order);

            if (cartDTO.product().promotionId() != null) {
                promotionService.increasePromotionUsageCount(cartDTO.product().promotionId());
            }
        }
    }

    private void clearCartForUser(String phone) {
        cartService.deleteAllProductFromCart(phone);
    }

    private Order saveOrder(
            Order order, CartDetailResponse cartDetailResponse, OrderCreateRequest orderCreateRequest
    ) {
        Order savedOrder = orderRepository.save(order);

        addUserNoteIfPresent(savedOrder, orderCreateRequest.userNote());
        createOrderDetails(savedOrder, cartDetailResponse);
        clearCartForUser(order.getUser().getPhone());

        return savedOrder;
    }

    private OrderResponse handlePayment(Order order, OrderCreateRequest orderCreateRequest) {
        return switch (orderCreateRequest.paymentMethod()) {
            case CASH_ON_DELIVERY -> new OrderResponse(OrderDTO.from(order));

            case BANK_TRANSFER -> {
                try {
                    String clientSecret = paymentService.createPaymentIntent(
                            order.getTotalPrice(), order
                    );
                    yield new OrderResponse(OrderDTO.from(order, clientSecret));
                } catch (StripeException e) {
                    throw new PaymentFailedException(e.getMessage());
                }
            }
        };
    }

    @Transactional
    public OrderResponse createOrder(String phone, OrderCreateRequest orderCreateRequest) {
        CartDetailResponse cartDetailResponse = prepareCart(phone, orderCreateRequest);

        User user = userService.findUserOrThrow(phone);
        Address address = addressService.findAddressOrThrow(orderCreateRequest.addressId());

        Order order = buildOrderEntity(user, address, orderCreateRequest, cartDetailResponse);

        Order savedOrder = saveOrder(order, cartDetailResponse, orderCreateRequest);

        log.info("[OrderService] Successfully created order for user phone={}", phone);

        return handlePayment(savedOrder, orderCreateRequest);
    }

    private Order findActiveOrderOrThrow(Long orderId, User user) {
        return orderRepository.findByIdAndUserAndDeliveredAtIsNullAndCancelledAtIsNull(orderId, user)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    public OrderResponse getPaymentIntent(String phone, Long orderId) {
        User user = userService.findUserOrThrow(phone);
        Order order = findActiveOrderOrThrow(orderId, user);

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException();
        }

        if (order.getPaymentMethod() != PaymentMethod.BANK_TRANSFER
                || order.getPaymentStatus() == PaymentStatus.PAID
        ) {
            throw new PaymentNotAllowedException();
        }

        try {
            String setClientSecret = paymentService.createPaymentIntent(order.getTotalPrice(), order);

            return new OrderResponse(OrderDTO.from(order, setClientSecret));
        } catch (StripeException e) {
            throw new PaymentFailedException(e.getMessage());
        }
    }

    private Order findOrderForUpdate(Long orderId) {
        Order order = findOrderOrThrow(orderId);

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new OrderAlreadyCancelledException();
        }

        return order;
    }

    public void updatePaymentStatus(Long orderId, PaymentStatus paymentStatus) {
        Order order = findOrderForUpdate(orderId);

        order.setPaymentStatus(paymentStatus);

        log.debug(
                "[OrderService] Updated payment status {} for order id={}",
                paymentStatus, orderId
        );

        orderRepository.save(order);
    }

    private void validateTransition(OrderStatus currentStatus, OrderStatus nextStatus) {
        if (currentStatus == OrderStatus.PENDING && nextStatus == OrderStatus.CONFIRMED) return;
        if (currentStatus == OrderStatus.CONFIRMED && nextStatus == OrderStatus.DELIVERING) return;
        if (currentStatus == OrderStatus.DELIVERING && nextStatus == OrderStatus.DELIVERED) return;
        if (nextStatus == OrderStatus.CANCELLED) return;

        throw new InvalidOrderStatusException();
    }

    private void handleConfirm(Order order) {
        if (order.getPaymentMethod() == PaymentMethod.BANK_TRANSFER
                && order.getPaymentStatus() != PaymentStatus.PAID
        ) {
            throw new PaymentNotCompletedException();
        }

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException();
        }

        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setConfirmedAt(LocalDateTime.now());

        log.debug("[OrderService] Confirmed order id={}", order.getId());
    }

    private void handleDelivering(Order order) {
        if (order.getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new InvalidOrderStatusException();
        }

        order.setOrderStatus(OrderStatus.DELIVERING);
        order.setDeliveringAt(LocalDateTime.now());

        log.debug("[OrderService] Marked order id={} as delivering", order.getId());
    }

    private void handleDelivered(Order order) {
        if (order.getOrderStatus() != OrderStatus.DELIVERING) {
            throw new InvalidOrderStatusException();
        }

        order.setOrderStatus(OrderStatus.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());

        log.debug("[OrderService] Marked order id={} as delivered", order.getId());

        if (order.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }
    }

    private void validateUserCancelPermission(Order order) {
        if (order.getOrderStatus() == OrderStatus.DELIVERING) {
            throw new InvalidOrderStatusException();
        }

        if (order.getPaymentStatus() == PaymentStatus.PAID
                && order.getPaymentMethod() == PaymentMethod.BANK_TRANSFER) {
            throw new OrderCannotBeCancelledException();
        }
    }

    private void handleCancel(Order order, User user, String reason) {
        if (user.getRole() == UserRole.USER) {
            validateUserCancelPermission(order);
            orderNoteService.createOrderNote(order, NoteType.CANCEL_REASON, reason, AuthorType.USER);
        } else if (user.getRole() == UserRole.STAFF) {
            orderNoteService.createOrderNote(order, NoteType.CANCEL_REASON, reason, AuthorType.STAFF);
        } else {
            throw new ForbiddenException();
        }

        order.setCancelledAt(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.CANCELLED);

        log.debug(
                "[OrderService] Cancelled order id={} by role={}",
                order.getId(), user.getRole()
        );
    }

    private void validatePermission(User user, OrderStatus newStatus) {
        if (user.getRole() == UserRole.USER && newStatus != OrderStatus.CANCELLED) {
            throw new ForbiddenException();
        }
    }

    @Transactional
    public OrderUpdateResponse updateOrder(Long orderId, String phone, OrderStatusUpdateRequest request) {
        Order order = findOrderForUpdate(orderId);
        User user = userService.findUserOrThrow(phone);

        OrderStatus newStatus = request.status();
        validatePermission(user, newStatus);
        validateTransition(order.getOrderStatus(), newStatus);

        if (order.getOrderStatus() == newStatus) {
            return new OrderUpdateResponse("Trạng thái đơn hàng không đổi");
        }

        switch (newStatus) {
            case CONFIRMED -> handleConfirm(order);
            case DELIVERING -> handleDelivering(order);
            case DELIVERED -> handleDelivered(order);
            case CANCELLED -> handleCancel(order, user, request.reason());
            default -> throw new InvalidOrderStatusException();
        }

        orderRepository.save(order);

        log.info("[OrderService] Successfully updated order id={}", orderId);

        return new OrderUpdateResponse("Đã cập nhật trạng thái cho đơn hàng: " + order.getId());
    }

    private void validateOrderAccess(User user, Order order) {
        UserRole userRole = user.getRole();

        boolean isOwner = order.getUser().getId().equals(user.getId());
        boolean isCompleted = order.getDeliveredAt() != null || order.getCancelledAt() != null;

        switch (userRole) {
            case ADMIN -> {
                return;
            }

            case USER -> {
                if (!isOwner) {
                    throw new AccessDeniedException("Không có quyền truy cập đơn hàng này");
                }
            }

            case STAFF -> {
                if (isOwner) {
                    return;
                }

                if (isCompleted) {
                    throw new AccessDeniedException("Bạn chỉ được xem đơn hàng chưa hoàn thành của người khác");
                }
            }
        }
    }

    public OrderResponse getOrder(String phone, Long orderId) {
        User user = userService.findUserOrThrow(phone);
        Order order = findOrderOrThrow(orderId);

        validateOrderAccess(user, order);

        log.info("[OrderService] Successfully retrieved order id={}", orderId);

        return new OrderResponse(OrderDTO.from(order));
    }

    private OrderPageResponse getAllActiveOrders(String phone, int page, int size) {
        User user = userService.findUserOrThrow(phone);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Order.Field.placedAt).descending());

        Page<Order> orderPage =
                orderRepository.findByUserAndDeliveredAtIsNullAndCancelledAtIsNull(user, pageable);

        log.info("[OrderService] Successfully retrieved all active order page");

        return OrderPageResponse.from(orderPage);
    }

    private OrderPageResponse getAllOrderHistory(String phone, int page, int size) {
        User user = userService.findUserOrThrow(phone);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Order.Field.placedAt).descending());

        Page<Order> orderPage = orderRepository.findCompletedOrCancelledOrdersByUser(user, pageable);

        log.info("[OrderService] Successfully retrieved all order history");

        return OrderPageResponse.from(orderPage);
    }

    private OrderPageResponse getAllUnfinishedOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Order.Field.placedAt).descending());

        Page<Order> orderPage = orderRepository.findByDeliveredAtIsNullAndCancelledAtIsNull(pageable);

        log.info("[OrderService] Successfully retrieved all unfinished orders");

        return OrderPageResponse.from(orderPage);
    }

    private OrderPageResponse getAllOrdersByAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Order.Field.placedAt).descending());

        Page<Order> orderPage = orderRepository.findAll(pageable);

        log.info("[OrderService] Successfully retrieved all order for admin");

        return OrderPageResponse.from(orderPage);
    }

    private void validateOrderListAccess(User user, OrderQueryType orderQueryType) {
        switch (orderQueryType) {
            case UNFINISHED -> {
                if (user.getRole() != UserRole.STAFF) {
                    throw new AccessDeniedException("Bạn không có quyền xem các đơn hàng chưa hoàn thành");
                }
            }
            case ALL -> {
                if (user.getRole() != UserRole.ADMIN) {
                    throw new AccessDeniedException("Chỉ quản trị viên mới được xem tất cả đơn hàng");
                }
            }
            default -> {
                return;
            }
        }
    }

    public OrderPageResponse getOrders(String phone, OrderQueryType orderQueryType, int page, int size) {
        User user = userService.findUserOrThrow(phone);

        validateOrderListAccess(user, orderQueryType);

        return switch (orderQueryType) {
            case ACTIVE -> getAllActiveOrders(phone, page, size);
            case HISTORY -> getAllOrderHistory(phone, page, size);
            case UNFINISHED -> getAllUnfinishedOrders(page, size);
            case ALL -> getAllOrdersByAdmin(page, size);
        };
    }

    public OrderStatsResponse getOrderStats() {
        OrderStatsProjection statsProjection = orderRepository.getStats();

        OrderStatsDTO orderStatsDTO = new OrderStatsDTO(
                statsProjection.getPendingOrderAmount(),
                statsProjection.getConfirmedOrderAmount(),
                statsProjection.getDeliveringOrderAmount(),
                statsProjection.getDeliveredOrderAmount(),
                statsProjection.getCancelledOrderAmount(),
                statsProjection.getCashOnDeliveryOrderAmount(),
                statsProjection.getBankTransferOrderAmount(),
                statsProjection.getDiscountedOrderAmount(),
                statsProjection.getCashOnDeliveryRevenue(),
                statsProjection.getBankTransferRevenue(),
                statsProjection.getTotalRevenue()
        );

        log.info("[OrderService] Successfully retrieved order stats");

        return new OrderStatsResponse(orderStatsDTO);
    }
}
