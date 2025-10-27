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
import com.example.fastfoodshop.response.OrderResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartService cartService;
    private final OrderNoteService orderNoteService;
    private final OrderDetailService orderDetailService;
    private final PromotionService promotionService;
    private final UserService userService;
    private final AddressService addressService;
    private final OrderRepository orderRepository;

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
    public ResponseEntity<ResponseWrapper<OrderResponse>> createCashOnDeliveryOrder(String phone, String promotionCode, String userNote, Long addressId) {
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

            return ResponseEntity.ok(ResponseWrapper.success(new OrderResponse(savedOrder)));
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "CREATE_COD_ORDER_FAILED",
                    "Lỗi tạo đơn hàng với phương thức vận chuyển COD: " + e.getMessage()
            ));
        }
    }
}
