package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.constant.OrderConstant;
import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.entity.Address;
import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.NoteType;
import com.example.fastfoodshop.enums.OrderStatus;
import com.example.fastfoodshop.enums.PaymentMethod;
import com.example.fastfoodshop.enums.PaymentStatus;
import com.example.fastfoodshop.enums.AuthorType;
import com.example.fastfoodshop.exception.order.CODPaymentLimitException;
import com.example.fastfoodshop.exception.order.InvalidOrderStatusException;
import com.example.fastfoodshop.exception.order.OrderAlreadyCancelledException;
import com.example.fastfoodshop.exception.order.OrderAmountExceededException;
import com.example.fastfoodshop.exception.order.OrderCannotBeCancelledException;
import com.example.fastfoodshop.exception.order.PaymentFailedException;
import com.example.fastfoodshop.exception.order.PaymentNotAllowedException;
import com.example.fastfoodshop.exception.order.PaymentNotCompletedException;
import com.example.fastfoodshop.exception.order.OrderNotFoundException;
import com.example.fastfoodshop.repository.OrderRepository;
import com.example.fastfoodshop.request.DeliveryRequest;
import com.example.fastfoodshop.request.OrderCancelRequest;
import com.example.fastfoodshop.request.OrderCreateRequest;
import com.example.fastfoodshop.response.cart.CartDetailResponse;
import com.example.fastfoodshop.dto.OrderDTO;
import com.example.fastfoodshop.response.order.OrderPageResponse;
import com.example.fastfoodshop.response.order.OrderResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private Order findUnfinishedOrderOrThrow(Long orderId) {
        return orderRepository.findByIdAndDeliveredAtIsNullAndCancelledAtIsNull(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private Order findActiveOrderOrThrow(Long orderId, User user) {
        return orderRepository.findByIdAndUserAndDeliveredAtIsNullAndCancelledAtIsNull(orderId, user)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private Order findOrderHistoryOrThrow(Long orderId, User user) {
        return orderRepository.findCompletedOrCancelledOrderByIdAndUser(orderId, user)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private Order findDeliveredOrderOrThrow(Long orderId, User user) {
        return orderRepository.findByIdAndUserAndDeliveredAtIsNotNull(orderId, user).orElseThrow(
                () -> new OrderNotFoundException(orderId)
        );
    }

    private Order findOrderForUpdate(Long orderId) {
        Order order = findOrderOrThrow(orderId);
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new OrderAlreadyCancelledException();
        }
        return order;
    }

    private void checkCashOnDeliveryLimitOrThrow(int orderTotalAmount) {
        if (orderTotalAmount > OrderConstant.CASH_ON_DELIVERY_MAX_AMOUNT)
            throw new CODPaymentLimitException();
    }

    private void checkOrderTotalAmountLimitOrThrow(int orderTotalAmount) {
        if (orderTotalAmount > OrderConstant.MAX_ORDER_TOTAL_AMOUNT)
            throw new OrderAmountExceededException();
    }

    private void checkAllActivatedProducts(List<CartDTO> cartDTOs) {
        for (CartDTO cartDTO : cartDTOs) {
            productService.checkActivatedCategoryAndActivatedProduct(cartDTO.product().id());
        }
    }

    private void buildOrder(Order order, CartDetailResponse cartDetailResponse, String phone, Long addressId) {
        User user = userService.findUserOrThrow(phone);
        order.setUser(user);

        Address address = addressService.findAddressOrThrow(addressId);
        order.setAddress(address);

        LocalDateTime now = LocalDateTime.now();

        order.setOrderStatus(OrderStatus.PENDING);
        order.setPlacedAt(now);
        order.setPaymentStatus(PaymentStatus.PENDING);

        order.setOriginalPrice(cartDetailResponse.originalPrice());
        order.setSubtotalPrice(cartDetailResponse.subtotalPrice());
        order.setDeliveryFee(cartDetailResponse.deliveryFee());
        order.setTotalPrice(cartDetailResponse.totalPrice());
    }

    private void applyOrderPromotion(Order order, CartDetailResponse cartDetailResponse) {
        if (cartDetailResponse.applyPromotionResult() != null && cartDetailResponse.applyPromotionResult().promotion() != null) {
            promotionService.increasePromotionUsageCount(cartDetailResponse.applyPromotionResult().promotion().id());
            Promotion promotion = promotionService.findPromotionOrThrow(cartDetailResponse.applyPromotionResult().promotion().id());
            order.setPromotion(promotion);
        }
    }

    private void addUserNoteIfPresent(Order order, String userNote) {
        if (userNote != null && !userNote.isBlank()) {
            orderNoteService.createOrderNote(order, NoteType.USER_NOTE, userNote, AuthorType.USER);
        }
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

    @Transactional
    public OrderResponse createCashOnDeliveryOrder(String phone, OrderCreateRequest orderCreateRequest) {
        DeliveryRequest deliveryRequest = new DeliveryRequest();
        deliveryRequest.setAddressId(orderCreateRequest.getAddressId());
        CartDetailResponse cartDetailResponse = cartService.getCartResponse(phone, orderCreateRequest.getPromotionCode(), deliveryRequest);
        checkAllActivatedProducts(cartDetailResponse.carts());

        Order order = new Order();
        checkCashOnDeliveryLimitOrThrow(cartDetailResponse.totalPrice());
        checkOrderTotalAmountLimitOrThrow(cartDetailResponse.totalPrice());
        buildOrder(order, cartDetailResponse, phone, orderCreateRequest.getAddressId());
        order.setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);

        applyOrderPromotion(order, cartDetailResponse);

        Order savedOrder = orderRepository.save(order);

        addUserNoteIfPresent(savedOrder, orderCreateRequest.getUserNote());

        createOrderDetails(savedOrder, cartDetailResponse);

        clearCartForUser(phone);

        return new OrderResponse(OrderDTO.from(savedOrder));
    }

    @Transactional
    public OrderResponse createStripePaymentOrder(String phone, OrderCreateRequest orderCreateRequest) {
        DeliveryRequest deliveryRequest = new DeliveryRequest();
        deliveryRequest.setAddressId(orderCreateRequest.getAddressId());
        CartDetailResponse cartDetailResponse = cartService.getCartResponse(phone, orderCreateRequest.getPromotionCode(), deliveryRequest);
        checkAllActivatedProducts(cartDetailResponse.carts());

        Order order = new Order();
        checkOrderTotalAmountLimitOrThrow(cartDetailResponse.totalPrice());
        buildOrder(order, cartDetailResponse, phone, orderCreateRequest.getAddressId());
        order.setPaymentMethod(PaymentMethod.BANK_TRANSFER);

        applyOrderPromotion(order, cartDetailResponse);

        Order savedOrder = orderRepository.save(order);

        addUserNoteIfPresent(savedOrder, orderCreateRequest.getUserNote());

        createOrderDetails(savedOrder, cartDetailResponse);

        clearCartForUser(phone);

        try {
            String setClientSecret = paymentService.createPaymentIntent(savedOrder.getTotalPrice(), savedOrder);
            return new OrderResponse(OrderDTO.from(order, setClientSecret));
        } catch (StripeException e) {
            throw new PaymentFailedException(e.getMessage());
        }
    }

    public OrderResponse getPaymentIntent(String phone, Long orderId) {
        User user = userService.findUserOrThrow(phone);
        Order order = findActiveOrderOrThrow(orderId, user);
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException();
        }
        if (order.getPaymentMethod() != PaymentMethod.BANK_TRANSFER || order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new PaymentNotAllowedException();
        }

        try {
            String setClientSecret = paymentService.createPaymentIntent(order.getTotalPrice(), order);
            return new OrderResponse(OrderDTO.from(order, setClientSecret));
        } catch (StripeException e) {
            throw new PaymentFailedException(e.getMessage());
        }
    }

    public OrderResponse getOrder(String phone, Long orderId) {
        User user = userService.findUserOrThrow(phone);
        Order order = findDeliveredOrderOrThrow(orderId, user);
        return new OrderResponse(OrderDTO.from(order));
    }

    public OrderPageResponse getAllUnfinishedOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Order.Field.placedAt).descending());
        Page<Order> orderPage = orderRepository.findByDeliveredAtIsNullAndCancelledAtIsNull(pageable);

        return OrderPageResponse.from(orderPage);
    }

    public OrderResponse getUnfinishedOrder(Long orderId) {
        Order order = findUnfinishedOrderOrThrow(orderId);
        return new OrderResponse(OrderDTO.from(order));
    }

    public void updatePaymentStatus(Long orderId, PaymentStatus paymentStatus) {
        Order order = findOrderForUpdate(orderId);
        order.setPaymentStatus(paymentStatus);

        orderRepository.save(order);
    }

    public OrderUpdateResponse confirmOrder(Long orderId) {
        Order order = findOrderForUpdate(orderId);
        if (order.getPaymentMethod() == PaymentMethod.BANK_TRANSFER && order.getPaymentStatus() != PaymentStatus.PAID) {
            throw new PaymentNotCompletedException();
        }
        if (order.getPlacedAt() == null) {
            throw new InvalidOrderStatusException();
        }
        order.setOrderStatus(OrderStatus.CONFIRMED);
        LocalDateTime now = LocalDateTime.now();
        order.setConfirmedAt(now);
        orderRepository.save(order);
        return new OrderUpdateResponse("Đã đánh dấu đơn hàng là đã giao: " + orderId);
    }

    public OrderUpdateResponse markAsDelivering(Long orderId) {
        Order order = findOrderForUpdate(orderId);
        if (order.getConfirmedAt() == null) {
            throw new InvalidOrderStatusException();
        }
        order.setOrderStatus(OrderStatus.DELIVERING);
        LocalDateTime now = LocalDateTime.now();
        order.setDeliveringAt(now);
        orderRepository.save(order);
        return new OrderUpdateResponse("Đã đánh dấu đơn hàng là đang giao: " + orderId);
    }

    public OrderUpdateResponse markAsDelivered(Long orderId) {
        Order order = findOrderForUpdate(orderId);
        if (order.getDeliveringAt() == null) {
            throw new InvalidOrderStatusException();
        }
        order.setOrderStatus(OrderStatus.DELIVERED);
        LocalDateTime now = LocalDateTime.now();
        order.setDeliveredAt(now);

        if (order.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }

        orderRepository.save(order);
        return new OrderUpdateResponse("Đã đánh dấu đơn hàng là đã giao: " + orderId);
    }

    public OrderPageResponse getAllActiveOrders(String phone, int page, int size) {
        User user = userService.findUserOrThrow(phone);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Order.Field.placedAt).descending());
        Page<Order> orderPage = orderRepository.findByUserAndDeliveredAtIsNullAndCancelledAtIsNull(user, pageable);

        return OrderPageResponse.from(orderPage);
    }

    public OrderResponse getActiveOrder(Long orderId, String phone) {
        User user = userService.findUserOrThrow(phone);
        Order order = findActiveOrderOrThrow(orderId, user);
        return new OrderResponse(OrderDTO.from(order));
    }

    public OrderPageResponse getAllOrderHistory(String phone, int page, int size) {
        User user = userService.findUserOrThrow(phone);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Order.Field.placedAt).descending());
        Page<Order> orderPage = orderRepository.findCompletedOrCancelledOrdersByUser(user, pageable);

        return OrderPageResponse.from(orderPage);
    }

    public OrderResponse getOrderHistory(Long orderId, String phone) {
        User user = userService.findUserOrThrow(phone);
        Order order = findOrderHistoryOrThrow(orderId, user);
        return new OrderResponse(OrderDTO.from(order));
    }

    public OrderUpdateResponse cancelOrderByUser(Long orderId, OrderCancelRequest orderCancelRequest) {
        Order order = findOrderForUpdate(orderId);
        if (order.getOrderStatus() == OrderStatus.DELIVERING) {
            throw new InvalidOrderStatusException();
        }
        if (order.getPaymentStatus() == PaymentStatus.PAID && order.getPaymentMethod() == PaymentMethod.BANK_TRANSFER) {
            throw new OrderCannotBeCancelledException();
        }
        LocalDateTime now = LocalDateTime.now();
        order.setCancelledAt(now);
        order.setOrderStatus(OrderStatus.CANCELLED);

        orderNoteService.createOrderNote(
                order, NoteType.CANCEL_REASON, orderCancelRequest.getReason(), AuthorType.USER
        );

        orderRepository.save(order);
        return new OrderUpdateResponse("Đã hủy đơn hàng: " + orderId);
    }

    public OrderUpdateResponse cancelOrderByStaff(Long orderId, OrderCancelRequest orderCancelRequest) {
        Order order = findOrderForUpdate(orderId);

        LocalDateTime now = LocalDateTime.now();
        order.setCancelledAt(now);
        order.setOrderStatus(OrderStatus.CANCELLED);

        orderNoteService.createOrderNote(
                order, NoteType.CANCEL_REASON, orderCancelRequest.getReason(), AuthorType.STAFF
        );

        orderRepository.save(order);
        return new OrderUpdateResponse("Đã hủy đơn hàng: " + orderId);
    }

    public OrderPageResponse getAllOrdersByAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Order.Field.placedAt).descending());
        Page<Order> orderPage = orderRepository.findAll(pageable);

        return OrderPageResponse.from(orderPage);
    }
}
