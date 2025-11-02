package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.entity.Address;
import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.NoteType;
import com.example.fastfoodshop.enums.OrderStatus;
import com.example.fastfoodshop.enums.PaymentMethod;
import com.example.fastfoodshop.enums.PaymentStatus;
import com.example.fastfoodshop.repository.OrderRepository;
import com.example.fastfoodshop.request.DeliveryRequest;
import com.example.fastfoodshop.response.CartResponse;
import com.example.fastfoodshop.dto.OrderDTO;
import com.example.fastfoodshop.response.OrderResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartService cartService;
    private final OrderNoteService orderNoteService;
    private final OrderDetailService orderDetailService;
    private final PromotionService promotionService;
    private final UserService userService;
    private final AddressService addressService;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

    public Order findOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
    }

    private Order findUnfinishedOrderOrThrow(Long orderId) {
        return orderRepository.findByIdAndDeliveredAtIsNullAndCancelledAtIsNull(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đang xử lý nào hợp lệ"));
    }

    private Order findActiveOrderOrThrow(Long orderId, User user) {
        return orderRepository.findByIdAndUserAndDeliveredAtIsNullAndCancelledAtIsNull(orderId, user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đang xử lý hợp lệ của người dùng này"));
    }

    private Order findOrderHistoryOrThrow(Long orderId, User user) {
        return orderRepository.findCompletedOrCancelledOrderByIdAndUser(orderId, user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng hợp lệ của người dùng này"));
    }

    private Order findOrderForUpdate(Long orderId) {
        Order order = findOrderOrThrow(orderId);
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Đơn hàng đã bị hủy, không thể cập nhật trạng thái");
        }
        return order;
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
            if (cartDTO.getProduct().getPromotionId() != null) {
                promotionService.increasePromotionUsageCount(cartDTO.getProduct().getPromotionId());
            }
        }
    }

    private void clearCartForUser(String phone) {
        cartService.deleteAllProductFromCart(phone);
    }

    @Transactional
    public ResponseEntity<ResponseWrapper<OrderDTO>> createCashOnDeliveryOrder(String phone, String promotionCode, String userNote, Long addressId) {
        try {
            DeliveryRequest deliveryRequest = new DeliveryRequest();
            deliveryRequest.setAddressId(addressId);
            CartResponse cartResponse = cartService.getCartResponse(phone, promotionCode, deliveryRequest);

            Order order = new Order();
            buildOrder(order, cartResponse, phone, addressId);
            order.setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);

            applyOrderPromotion(order, cartResponse);

            Order savedOrder = orderRepository.save(order);

            addUserNoteIfPresent(savedOrder, userNote);

            createOrderDetails(savedOrder, cartResponse);

            clearCartForUser(phone);

            return ResponseEntity.ok(ResponseWrapper.success(new OrderDTO(savedOrder)));
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "CREATE_COD_ORDER_FAILED",
                    "Lỗi tạo đơn hàng với phương thức vận chuyển COD: " + e.getMessage()
            ));
        }
    }

    @Transactional
    public ResponseEntity<ResponseWrapper<OrderDTO>> createStripePaymentOrder(String phone, String promotionCode, String userNote, Long addressId) {
        try {
            DeliveryRequest deliveryRequest = new DeliveryRequest();
            deliveryRequest.setAddressId(addressId);
            CartResponse cartResponse = cartService.getCartResponse(phone, promotionCode, deliveryRequest);

            Order order = new Order();
            buildOrder(order, cartResponse, phone, addressId);
            order.setPaymentMethod(PaymentMethod.BANK_TRANSFER);

            applyOrderPromotion(order, cartResponse);

            Order savedOrder = orderRepository.save(order);

            addUserNoteIfPresent(savedOrder, userNote);

            createOrderDetails(savedOrder, cartResponse);

            clearCartForUser(phone);

            OrderDTO orderResponse = new OrderDTO(savedOrder);
            orderResponse.setClientSecret(paymentService.createPaymentIntent(savedOrder.getTotalPrice(), savedOrder));

            return ResponseEntity.ok(ResponseWrapper.success(orderResponse));
        } catch (StripeException stripeException) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "CREATE_STRIPE_PAYMENT_FAILED",
                    "Lỗi khi tạo thanh toán Stripe: " + stripeException.getMessage()
            ));
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "CREATE_STRIPE_PAYMENT_FAILED",
                    "Lỗi tạo đơn hàng với phương thức thanh toán Stripe: " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<OrderDTO>> getOrder(Long orderId) {
        try {
            Order order = findOrderOrThrow(orderId);
            return ResponseEntity.ok(ResponseWrapper.success(new OrderDTO(order)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_ORDER_FAILED",
                    "Lỗi lấy đơn hàng " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<OrderResponse>> getAllUnfinishedOrders() {
        try {
            List<Order> orders = orderRepository.findByDeliveredAtIsNullAndCancelledAtIsNull();
            return ResponseEntity.ok(ResponseWrapper.success(new OrderResponse(orders)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_UNFINISHED_ORDER_FAILED",
                    "Lỗi khi lấy các đơn hàng chưa hoàn tất" + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<OrderDTO>> getUnfinishedOrder(Long orderId) {
        try {
            Order order = findUnfinishedOrderOrThrow(orderId);
            return ResponseEntity.ok(ResponseWrapper.success(new OrderDTO(order)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_UNFINISHED_ORDER_FAILED",
                    "Lỗi khi lấy chi tiết đơn hàng chưa hoàn tất" + e.getMessage()
            ));
        }
    }

    public Order updatePaymentStatus(Long orderId, PaymentStatus paymentStatus) {
        try {
            Order order = findOrderForUpdate(orderId);
            order.setPaymentStatus(paymentStatus);

            return orderRepository.save(order);
        } catch (RuntimeException e) {
            throw new RuntimeException("Lỗi khi cập nhật trạng thái thanh toán " + e.getMessage());
        }
    }

    public ResponseEntity<ResponseWrapper<OrderDTO>> confirmOrder(Long orderId) {
        try {
            Order order = findOrderForUpdate(orderId);
            if (order.getPaymentMethod() == PaymentMethod.BANK_TRANSFER && order.getPaymentStatus() != PaymentStatus.PAID) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error(
                        "CONFIRM_ORDER_FAILED",
                        "Không thể duyệt đơn chưa thanh toán Stripe hoặc thanh toán thất bại"
                ));
            }
            if (order.getPlacedAt() == null) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error(
                        "CONFIRM_ORDER_FAILED",
                        "Không thể duyệt đơn hàng chưa được đặt"
                ));
            }
            order.setOrderStatus(OrderStatus.CONFIRMED);
            LocalDateTime now = LocalDateTime.now();
            order.setConfirmedAt(now);
            Order confirmedOrder = orderRepository.save(order);
            return ResponseEntity.ok(ResponseWrapper.success(new OrderDTO(confirmedOrder)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "CONFIRM_ORDER_FAILED",
                    "Lỗi duyệt đơn hàng: " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<OrderDTO>> markAsDelivering(Long orderId) {
        try {
            Order order = findOrderForUpdate(orderId);
            if (order.getConfirmedAt() == null) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error(
                        "MARK_DELIVERING_ORDER_FAILED",
                        "Không thể giao cho đơn hàng chưa duyệt"
                ));
            }
            order.setOrderStatus(OrderStatus.DELIVERING);
            LocalDateTime now = LocalDateTime.now();
            order.setDeliveringAt(now);
            Order updatedOrder = orderRepository.save(order);
            return ResponseEntity.ok(ResponseWrapper.success(new OrderDTO(updatedOrder)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "MARK_DELIVERING_ORDER_FAILED",
                    "Lỗi đánh dấu giao đang hàng đơn hàng: " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<OrderDTO>> markAsDelivered(Long orderId) {
        try {
            Order order = findOrderForUpdate(orderId);
            if (order.getDeliveringAt() == null) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error(
                        "MARK_DELIVERED_ORDER_FAILED",
                        "Không thể xác nhận giao thành công cho đơn hàng chưa được giao"
                ));
            }
            order.setOrderStatus(OrderStatus.DELIVERED);
            LocalDateTime now = LocalDateTime.now();
            order.setDeliveredAt(now);

            if (order.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY) {
                order.setPaymentStatus(PaymentStatus.PAID);
            }

            Order updatedOrder = orderRepository.save(order);
            return ResponseEntity.ok(ResponseWrapper.success(new OrderDTO(updatedOrder)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "MARK_DELIVERED_ORDER_FAILED",
                    "Lỗi đánh dấu đã giao cho đơn hàng: " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<OrderResponse>> getAllActiveOrders(String phone) {
        try {
            User user = userService.findUserOrThrow(phone);
            List<Order> orders = orderRepository.findByUserAndDeliveredAtIsNullAndCancelledAtIsNull(user);
            return ResponseEntity.ok(ResponseWrapper.success(new OrderResponse(orders)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_ACTIVE_ORDER_FAILED",
                    "Lỗi khi lấy các đơn hàng đang xử lý cho khách hàng " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<OrderDTO>> getActiveOrder(Long orderId, String phone) {
        try {
            User user = userService.findUserOrThrow(phone);
            Order order = findActiveOrderOrThrow(orderId, user);
            return ResponseEntity.ok(ResponseWrapper.success(new OrderDTO(order)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_ACTIVE_ORDER_FAILED",
                    "Lỗi khi lấy đơn hàng đang xử lý cho khách hàng " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<OrderResponse>> getAllOrderHistory(String phone) {
        try {
            User user = userService.findUserOrThrow(phone);
            List<Order> orders = orderRepository.findCompletedOrCancelledOrdersByUser(user);
            return ResponseEntity.ok(ResponseWrapper.success(new OrderResponse(orders)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_ORDER_FAILED",
                    "Lỗi khi lấy các đơn hàng cho khách hàng " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<OrderDTO>> getOrderHistory(Long orderId, String phone) {
        try {
            User user = userService.findUserOrThrow(phone);
            Order order = findOrderHistoryOrThrow(orderId, user);
            return ResponseEntity.ok(ResponseWrapper.success(new OrderDTO(order)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "GET_ORDER_HISTORY_FAILED",
                    "Lỗi khi lấy chi tiết lịch sử đơn hàng " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<OrderDTO>> cancelOrderByUser(Long orderId, String reason) {
        try {
            Order order = findOrderForUpdate(orderId);
            if (order.getOrderStatus() == OrderStatus.DELIVERING) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error(
                        "CANCEL_ORDER_FAILED",
                        "Không được hủy đơn hàng đang giao"
                ));
            }
            if (order.getPaymentStatus() == PaymentStatus.PAID && order.getPaymentMethod() == PaymentMethod.BANK_TRANSFER) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error(
                        "CANCEL_ORDER_FAILED",
                        "Không được hủy đơn hàng đã thanh toán qua stripe"
                ));
            }
            LocalDateTime now = LocalDateTime.now();
            order.setCancelledAt(now);
            order.setOrderStatus(OrderStatus.CANCELLED);

            orderNoteService.createOrderNoteByUser(order, NoteType.CANCEL_REASON, reason);

            Order updatedOrder = orderRepository.save(order);
            return ResponseEntity.ok(ResponseWrapper.success(new OrderDTO(updatedOrder)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "CANCEL_ORDER_FAILED",
                    "Lỗi khi hủy đơn hàng " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<ResponseWrapper<OrderDTO>> cancelOrderByStaff(Long orderId, String reason) {
        try {
            Order order = findOrderForUpdate(orderId);

            LocalDateTime now = LocalDateTime.now();
            order.setCancelledAt(now);
            order.setOrderStatus(OrderStatus.CANCELLED);

            orderNoteService.createOrderNoteByStaff(order, NoteType.CANCEL_REASON, reason);

            Order updatedOrder = orderRepository.save(order);
            return ResponseEntity.ok(ResponseWrapper.success(new OrderDTO(updatedOrder)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "CANCEL_ORDER_FAILED",
                    "Lỗi khi hủy đơn hàng " + e.getMessage()
            ));
        }
    }
}
