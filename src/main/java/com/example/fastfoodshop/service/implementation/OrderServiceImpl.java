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
import com.example.fastfoodshop.response.CartResponse;
import com.example.fastfoodshop.dto.OrderDTO;
import com.example.fastfoodshop.response.OrderResponse;
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
import java.util.ArrayList;

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

    private void checkAllActivatedProducts(ArrayList<CartDTO> cartDTOs) {
        for (CartDTO cartDTO : cartDTOs) {
            productService.checkActivatedCategoryAndActivatedProduct(cartDTO.product().getId());
        }
    }

    private void buildOrder(Order order, CartResponse cartResponse, String phone, Long addressId) {
        User user = userService.findUserOrThrow(phone);
        order.setUser(user);

        Address address = addressService.findAddressOrThrow(addressId);
        order.setAddress(address);

        LocalDateTime now = LocalDateTime.now();

        order.setOrderStatus(OrderStatus.PENDING);
        order.setPlacedAt(now);
        order.setPaymentStatus(PaymentStatus.PENDING);

        order.setOriginalPrice(cartResponse.getOriginalPrice());
        order.setSubtotalPrice(cartResponse.getSubtotalPrice());
        order.setDeliveryFee(cartResponse.getDeliveryFee());
        order.setTotalPrice(cartResponse.getTotalPrice());
    }

    private void applyOrderPromotion(Order order, CartResponse cartResponse) {
        if (cartResponse.getApplyPromotionResult() != null && cartResponse.getApplyPromotionResult().getPromotion() != null) {
            promotionService.increasePromotionUsageCount(cartResponse.getApplyPromotionResult().getPromotion().getId());
            Promotion promotion = promotionService.findPromotionOrThrow(cartResponse.getApplyPromotionResult().getPromotion().getId());
            order.setPromotion(promotion);
        }
    }

    private void addUserNoteIfPresent(Order order, String userNote) {
        if (userNote != null && !userNote.isEmpty()) {
            orderNoteService.createOrderNoteByUser(order, NoteType.USER_NOTE, userNote);
        }
    }

    private void createOrderDetails(Order order, CartResponse cartResponse) {
        ArrayList<CartDTO> cartDTOs = cartResponse.getCarts();
        for (CartDTO cartDTO : cartDTOs) {
            orderDetailService.createOrderDetail(cartDTO, order);
            if (cartDTO.product().getPromotionId() != null) {
                promotionService.increasePromotionUsageCount(cartDTO.product().getPromotionId());
            }
        }
    }

    private void clearCartForUser(String phone) {
        cartService.deleteAllProductFromCart(phone);
    }

    @Transactional
    public OrderDTO createCashOnDeliveryOrder(String phone, OrderCreateRequest orderCreateRequest) {
        DeliveryRequest deliveryRequest = new DeliveryRequest();
        deliveryRequest.setAddressId(orderCreateRequest.getAddressId());
        CartResponse cartResponse = cartService.getCartResponse(phone, orderCreateRequest.getPromotionCode(), deliveryRequest);
        checkAllActivatedProducts(cartResponse.getCarts());

        Order order = new Order();
        checkCashOnDeliveryLimitOrThrow(cartResponse.getTotalPrice());
        checkOrderTotalAmountLimitOrThrow(cartResponse.getTotalPrice());
        buildOrder(order, cartResponse, phone, orderCreateRequest.getAddressId());
        order.setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);

        applyOrderPromotion(order, cartResponse);

        Order savedOrder = orderRepository.save(order);

        addUserNoteIfPresent(savedOrder, orderCreateRequest.getUserNote());

        createOrderDetails(savedOrder, cartResponse);

        clearCartForUser(phone);

        return OrderDTO.from(savedOrder);
    }

    @Transactional
    public OrderDTO createStripePaymentOrder(String phone, OrderCreateRequest orderCreateRequest) {
        DeliveryRequest deliveryRequest = new DeliveryRequest();
        deliveryRequest.setAddressId(orderCreateRequest.getAddressId());
        CartResponse cartResponse = cartService.getCartResponse(phone, orderCreateRequest.getPromotionCode(), deliveryRequest);
        checkAllActivatedProducts(cartResponse.getCarts());

        Order order = new Order();
        checkOrderTotalAmountLimitOrThrow(cartResponse.getTotalPrice());
        buildOrder(order, cartResponse, phone, orderCreateRequest.getAddressId());
        order.setPaymentMethod(PaymentMethod.BANK_TRANSFER);

        applyOrderPromotion(order, cartResponse);

        Order savedOrder = orderRepository.save(order);

        addUserNoteIfPresent(savedOrder, orderCreateRequest.getUserNote());

        createOrderDetails(savedOrder, cartResponse);

        clearCartForUser(phone);

        try {
            String setClientSecret = paymentService.createPaymentIntent(savedOrder.getTotalPrice(), savedOrder);
            return OrderDTO.from(order, setClientSecret);
        } catch (StripeException e) {
            throw new PaymentFailedException(e.getMessage());
        }
    }

    public OrderDTO getPaymentIntent(String phone, Long orderId) {
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
            return OrderDTO.from(order, setClientSecret);
        } catch (StripeException e) {
            throw new PaymentFailedException(e.getMessage());
        }
    }

    public OrderDTO getOrder(String phone, Long orderId) {
        User user = userService.findUserOrThrow(phone);
        Order order = findDeliveredOrderOrThrow(orderId, user);
        return OrderDTO.from(order);
    }

    public OrderResponse getAllUnfinishedOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Order.Field.placedAt).descending());
        Page<Order> orderPage = orderRepository.findByDeliveredAtIsNullAndCancelledAtIsNull(pageable);

        return new OrderResponse(orderPage);
    }

    public OrderDTO getUnfinishedOrder(Long orderId) {
        Order order = findUnfinishedOrderOrThrow(orderId);
        return OrderDTO.from(order);
    }

    public Order updatePaymentStatus(Long orderId, PaymentStatus paymentStatus) {
        Order order = findOrderForUpdate(orderId);
        order.setPaymentStatus(paymentStatus);

        return orderRepository.save(order);
    }

    public OrderDTO confirmOrder(Long orderId) {
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
        Order confirmedOrder = orderRepository.save(order);
        return OrderDTO.from(confirmedOrder);
    }

    public OrderDTO markAsDelivering(Long orderId) {
        Order order = findOrderForUpdate(orderId);
        if (order.getConfirmedAt() == null) {
            throw new InvalidOrderStatusException();
        }
        order.setOrderStatus(OrderStatus.DELIVERING);
        LocalDateTime now = LocalDateTime.now();
        order.setDeliveringAt(now);
        Order updatedOrder = orderRepository.save(order);
        return OrderDTO.from(updatedOrder);
    }

    public OrderDTO markAsDelivered(Long orderId) {
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

        Order updatedOrder = orderRepository.save(order);
        return OrderDTO.from(updatedOrder);
    }

    public OrderResponse getAllActiveOrders(String phone, int page, int size) {
        User user = userService.findUserOrThrow(phone);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Order.Field.placedAt).descending());
        Page<Order> orderPage = orderRepository.findByUserAndDeliveredAtIsNullAndCancelledAtIsNull(user, pageable);

        return new OrderResponse(orderPage);
    }

    public OrderDTO getActiveOrder(Long orderId, String phone) {
        User user = userService.findUserOrThrow(phone);
        Order order = findActiveOrderOrThrow(orderId, user);
        return OrderDTO.from(order);
    }

    public OrderResponse getAllOrderHistory(String phone, int page, int size) {
        User user = userService.findUserOrThrow(phone);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Order.Field.placedAt).descending());
        Page<Order> orderPage = orderRepository.findCompletedOrCancelledOrdersByUser(user, pageable);

        return new OrderResponse(orderPage);
    }

    public OrderDTO getOrderHistory(Long orderId, String phone) {
        User user = userService.findUserOrThrow(phone);
        Order order = findOrderHistoryOrThrow(orderId, user);
        return OrderDTO.from(order);
    }

    public OrderDTO cancelOrderByUser(Long orderId, OrderCancelRequest orderCancelRequest) {
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

        orderNoteService.createOrderNoteByUser(order, NoteType.CANCEL_REASON, orderCancelRequest.getReason());

        Order updatedOrder = orderRepository.save(order);
        return OrderDTO.from(updatedOrder);
    }

    public OrderDTO cancelOrderByStaff(Long orderId, OrderCancelRequest orderCancelRequest) {
        Order order = findOrderForUpdate(orderId);

        LocalDateTime now = LocalDateTime.now();
        order.setCancelledAt(now);
        order.setOrderStatus(OrderStatus.CANCELLED);

        orderNoteService.createOrderNoteByStaff(order, NoteType.CANCEL_REASON, orderCancelRequest.getReason());

        Order updatedOrder = orderRepository.save(order);
        return OrderDTO.from(updatedOrder);
    }

    public OrderResponse getAllOrdersByAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Order.Field.placedAt).descending());
        Page<Order> orderPage = orderRepository.findAll(pageable);

        return new OrderResponse(orderPage);
    }
}
