package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.OrderQueryType;
import com.example.fastfoodshop.enums.PaymentStatus;
import com.example.fastfoodshop.enums.OrderStatus;
import com.example.fastfoodshop.enums.UserRole;
import com.example.fastfoodshop.exception.order.AccessDeniedException;
import com.example.fastfoodshop.exception.order.PaymentNotAllowedException;
import com.example.fastfoodshop.exception.order.PaymentFailedException;
import com.example.fastfoodshop.exception.order.OrderAlreadyCancelledException;
import com.example.fastfoodshop.exception.order.OrderNotFoundException;
import com.example.fastfoodshop.exception.order.ForbiddenException;
import com.example.fastfoodshop.exception.order.OrderCannotBeCancelledException;
import com.example.fastfoodshop.exception.order.InvalidOrderStatusException;
import com.example.fastfoodshop.exception.order.PaymentNotCompletedException;
import com.example.fastfoodshop.exception.user.UserNotFoundException;
import com.example.fastfoodshop.factory.order.OrderFactory;
import com.example.fastfoodshop.factory.order.OrderPageFactory;
import com.example.fastfoodshop.factory.order.OrderStatusUpdateRequestFactory;
import com.example.fastfoodshop.factory.user.UserFactory;
import com.example.fastfoodshop.projection.OrderStatsProjection;
import com.example.fastfoodshop.repository.OrderRepository;
import com.example.fastfoodshop.request.OrderStatusUpdateRequest;
import com.example.fastfoodshop.response.order.OrderPageResponse;
import com.example.fastfoodshop.response.order.OrderResponse;
import com.example.fastfoodshop.response.order.OrderStatsResponse;
import com.example.fastfoodshop.response.order.OrderUpdateResponse;
import com.example.fastfoodshop.service.implementation.OrderServiceImpl;
import com.stripe.exception.ApiException;
import com.stripe.exception.StripeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    @Mock
    OrderRepository orderRepository;

    @Mock
    UserService userService;

    @Mock
    OrderNoteService orderNoteService;

    @Mock
    PaymentService paymentService;

    @InjectMocks
    OrderServiceImpl orderService;

    private static final String USER_PHONE = "0999998888";

    private static final Long ORDER_ID = 3456L;

    private static final Long USER_ID = 111L;
    private static final Long STAFF_ID = 222L;
    private static final Long ADMIN_ID = 333L;

    private static final int PAGE = 5;
    private static final int SIZE = 5;

    private static final Pageable PAGEABLE = PageRequest.of(
            PAGE,
            SIZE,
            Sort.by(Order.Field.placedAt).descending()
    );

    private static final String CLIENT_SECRET = "payment-intent";

    @Test
    void getPaymentIntent_pendingAndUnpaidOnlineOrder_shouldReturnOrderResponse() throws StripeException {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Order pendingOrder = OrderFactory.createUnpaidOnlineAndPendingOrder(user, ORDER_ID);

        when(orderRepository.findByIdAndUserAndDeliveredAtIsNullAndCancelledAtIsNull(
                pendingOrder.getId(), user
        )).thenReturn(Optional.of(pendingOrder));

        when(paymentService.createPaymentIntent(pendingOrder)).thenReturn(CLIENT_SECRET);

        OrderResponse orderResponse = orderService.getPaymentIntent(user.getPhone(), pendingOrder.getId());

        assertNotNull(orderResponse);
        assertNotNull(orderResponse.order());

        assertEquals(CLIENT_SECRET, orderResponse.order().clientSecret());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(orderRepository)
                .findByIdAndUserAndDeliveredAtIsNullAndCancelledAtIsNull(pendingOrder.getId(), user);
        verify(paymentService).createPaymentIntent(pendingOrder);
    }

    @Test
    void getPaymentIntent_notFoundUser_shouldThrowUserNotFoundException() {
        when(userService.findUserOrThrow(USER_PHONE))
                .thenThrow(new UserNotFoundException(USER_PHONE));

        assertThrows(
                UserNotFoundException.class,
                () -> orderService.getPaymentIntent(USER_PHONE, ORDER_ID)
        );

        verify(userService).findUserOrThrow(USER_PHONE);
    }

    @Test
    void getPaymentIntent_notFoundOrder_shouldThrowOrderNotFoundException() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        when(orderRepository.findByIdAndUserAndDeliveredAtIsNullAndCancelledAtIsNull(
                ORDER_ID, user
        )).thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getPaymentIntent(user.getPhone(), ORDER_ID)
        );

        verify(userService).findUserOrThrow(user.getPhone());
        verify(orderRepository)
                .findByIdAndUserAndDeliveredAtIsNullAndCancelledAtIsNull(ORDER_ID, user);
    }

    @Test
    void getPaymentIntent_cancelledOrder_shouldThrowInvalidOrderStatusException() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Order cancelledOrder = OrderFactory.createCancelledOrder(user, ORDER_ID);

        when(orderRepository.findByIdAndUserAndDeliveredAtIsNullAndCancelledAtIsNull(
                ORDER_ID, user
        )).thenReturn(Optional.of(cancelledOrder));

        assertThrows(
                InvalidOrderStatusException.class,
                () -> orderService.getPaymentIntent(user.getPhone(), cancelledOrder.getId())
        );

        verify(userService).findUserOrThrow(user.getPhone());
        verify(orderRepository)
                .findByIdAndUserAndDeliveredAtIsNullAndCancelledAtIsNull(cancelledOrder.getId(), user);
    }

    @Test
    void getPaymentIntent_pendingAndUnpaidCODOrder_shouldThrowPaymentNotAllowedException() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Order order = OrderFactory.createUnpaidCODAndPendingOrder(user, ORDER_ID);

        when(orderRepository.findByIdAndUserAndDeliveredAtIsNullAndCancelledAtIsNull(
                ORDER_ID, user
        )).thenReturn(Optional.of(order));

        assertThrows(
                PaymentNotAllowedException.class,
                () -> orderService.getPaymentIntent(user.getPhone(), order.getId())
        );

        verify(userService).findUserOrThrow(user.getPhone());
        verify(orderRepository)
                .findByIdAndUserAndDeliveredAtIsNullAndCancelledAtIsNull(order.getId(), user);
    }

    @Test
    void getPaymentIntent_pendingAndPaidOnlineOrder_shouldThrowPaymentNotAllowedException() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Order order = OrderFactory.createPaidOnlineAndPendingOrder(user, ORDER_ID);

        when(orderRepository.findByIdAndUserAndDeliveredAtIsNullAndCancelledAtIsNull(
                ORDER_ID, user
        )).thenReturn(Optional.of(order));

        assertThrows(
                PaymentNotAllowedException.class,
                () -> orderService.getPaymentIntent(user.getPhone(), order.getId())
        );

        verify(userService).findUserOrThrow(user.getPhone());
        verify(orderRepository)
                .findByIdAndUserAndDeliveredAtIsNullAndCancelledAtIsNull(order.getId(), user);
    }

    @Test
    void getPaymentIntent_withStripeException_shouldThrowPaymentFailedException() throws StripeException {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Order pendingOrder = OrderFactory.createUnpaidOnlineAndPendingOrder(user, ORDER_ID);

        when(orderRepository.findByIdAndUserAndDeliveredAtIsNullAndCancelledAtIsNull(
                pendingOrder.getId(), user
        )).thenReturn(Optional.of(pendingOrder));

        when(paymentService.createPaymentIntent(pendingOrder))
                .thenThrow(new ApiException("Stripe error", null, null, 500, null));

        assertThrows(
                PaymentFailedException.class,
                () -> orderService.getPaymentIntent(user.getPhone(), pendingOrder.getId())
        );

        verify(userService).findUserOrThrow(user.getPhone());
        verify(orderRepository)
                .findByIdAndUserAndDeliveredAtIsNullAndCancelledAtIsNull(pendingOrder.getId(), user);
        verify(paymentService).createPaymentIntent(pendingOrder);
    }

    @Test
    void updatePaymentStatus_validRequest_shouldBeSuccessful() {
        User user = UserFactory.createActivatedUser();

        Order order = OrderFactory.createpPendingOrder(user, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        PaymentStatus newPaymentStatus = PaymentStatus.FAILED;

        orderService.updatePaymentStatus(order.getId(), newPaymentStatus);

        assertEquals(newPaymentStatus, order.getPaymentStatus());

        verify(orderRepository).findById(order.getId());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updatePaymentStatus_notFoundOrder_shouldThrowOrderNotFoundException() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        PaymentStatus newPaymentStatus = PaymentStatus.FAILED;

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.updatePaymentStatus(ORDER_ID, newPaymentStatus)
        );

        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void updatePaymentStatus_cancelledOrder_shouldThrowOrderAlreadyCancelledException() {
        User user = UserFactory.createActivatedUser();

        Order cancelledOrder = OrderFactory.createCancelledOrder(user, ORDER_ID);

        when(orderRepository.findById(cancelledOrder.getId()))
                .thenReturn(Optional.of(cancelledOrder));

        PaymentStatus newPaymentStatus = PaymentStatus.FAILED;

        assertThrows(
                OrderAlreadyCancelledException.class,
                () -> orderService.updatePaymentStatus(cancelledOrder.getId(), newPaymentStatus)
        );

        verify(orderRepository).findById(cancelledOrder.getId());
    }

    @Test
    void updateOrder_notFoundOrder_shouldThrowOrderNotFoundException() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        OrderStatusUpdateRequest cancelRequest = OrderStatusUpdateRequestFactory.createCancelRequest();

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.updateOrder(ORDER_ID, USER_PHONE, cancelRequest)
        );

        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void updateOrder_cancelledOrder_shouldThrowOrderAlreadyCancelledException() {
        User user = UserFactory.createActivatedUser();

        Order order = OrderFactory.createCancelledOrder(user, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        OrderStatusUpdateRequest cancelRequest =
                OrderStatusUpdateRequestFactory.createCancelRequest();

        assertThrows(OrderAlreadyCancelledException.class, () -> orderService.updateOrder(
                order.getId(), USER_PHONE, cancelRequest
        ));

        verify(orderRepository).findById(order.getId());
    }

    @Test
    void updateOrder_notFoundUser_shouldThrowUserNotFoundException() {
        User user = UserFactory.createActivatedUser();

        Order order = OrderFactory.createpPendingOrder(user, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        when(userService.findUserOrThrow(USER_PHONE))
                .thenThrow(new UserNotFoundException(USER_PHONE));

        OrderStatusUpdateRequest cancelRequest = OrderStatusUpdateRequestFactory.createCancelRequest();

        assertThrows(
                UserNotFoundException.class,
                () -> orderService.updateOrder(order.getId(), USER_PHONE, cancelRequest)
        );

        verify(orderRepository).findById(order.getId());
        verify(userService).findUserOrThrow(USER_PHONE);
    }

    @Test
    void updateOrder_notAllowedRole_shouldThrowForbiddenException() {
        User user = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.USER);

        Order order = OrderFactory.createpPendingOrder(user, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        OrderStatusUpdateRequest cancelRequest = OrderStatusUpdateRequestFactory.createConfirmRequest();

        assertThrows(ForbiddenException.class, () -> orderService.updateOrder(
                order.getId(), user.getPhone(), cancelRequest
        ));

        verify(orderRepository).findById(order.getId());
        verify(userService).findUserOrThrow(user.getPhone());
    }

    @Test
    void updateOrder_invalidNewStatus_shouldThrowInvalidOrderStatusException() {
        User staff = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.STAFF);

        Order order = OrderFactory.createDeliveredOrder(staff, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        when(userService.findUserOrThrow(staff.getPhone())).thenReturn(staff);

        OrderStatusUpdateRequest cancelRequest =
                OrderStatusUpdateRequestFactory.createMarkAsDeliveredRequest();

        assertThrows(InvalidOrderStatusException.class, () -> orderService.updateOrder(
                order.getId(), staff.getPhone(), cancelRequest
        ));

        verify(orderRepository).findById(order.getId());
        verify(userService).findUserOrThrow(staff.getPhone());
    }

    @Test
    void updateOrder_confirmRequest_paidOnlineOrder_shouldReturnOrderUpdateResponse() {
        User staff = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.STAFF);

        Order order = OrderFactory.createPaidOnlineAndPendingOrder(staff, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        when(userService.findUserOrThrow(staff.getPhone())).thenReturn(staff);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderStatusUpdateRequest cancelRequest = OrderStatusUpdateRequestFactory.createConfirmRequest();

        OrderUpdateResponse orderUpdateResponse = orderService.updateOrder(
                order.getId(), staff.getPhone(), cancelRequest
        );

        assertNotNull(orderUpdateResponse);
        assertNotNull(orderUpdateResponse.message());

        assertEquals(OrderStatus.CONFIRMED, order.getOrderStatus());

        verify(orderRepository).findById(order.getId());
        verify(userService).findUserOrThrow(staff.getPhone());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrder_confirmRequest_unpaidCODOrder_shouldReturnOrderUpdateResponse() {
        User staff = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.STAFF);

        Order order = OrderFactory.createUnpaidCODAndPendingOrder(staff, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        when(userService.findUserOrThrow(staff.getPhone())).thenReturn(staff);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderStatusUpdateRequest cancelRequest = OrderStatusUpdateRequestFactory.createConfirmRequest();

        OrderUpdateResponse orderUpdateResponse = orderService.updateOrder(
                order.getId(), staff.getPhone(), cancelRequest
        );

        assertNotNull(orderUpdateResponse);
        assertNotNull(orderUpdateResponse.message());

        assertEquals(OrderStatus.CONFIRMED, order.getOrderStatus());

        verify(orderRepository).findById(order.getId());
        verify(userService).findUserOrThrow(staff.getPhone());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrder_confirmRequest_unpaidOnlineOrder_shouldThrowPaymentNotCompletedException() {
        User staff = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.STAFF);

        Order order = OrderFactory.createUnpaidOnlineAndPendingOrder(staff, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        when(userService.findUserOrThrow(staff.getPhone())).thenReturn(staff);

        OrderStatusUpdateRequest cancelRequest = OrderStatusUpdateRequestFactory.createConfirmRequest();

        assertThrows(PaymentNotCompletedException.class, () -> orderService.updateOrder(
                order.getId(), staff.getPhone(), cancelRequest
        ));

        verify(orderRepository).findById(order.getId());
        verify(userService).findUserOrThrow(staff.getPhone());
    }

    @Test
    void updateOrder_deliveringRequest_unpaidCODOrder_shouldReturnOrderUpdateResponse() {
        User staff = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.STAFF);

        Order order = OrderFactory.createUnpaidCODAndConfirmedOrder(staff, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        when(userService.findUserOrThrow(staff.getPhone())).thenReturn(staff);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderStatusUpdateRequest cancelRequest =
                OrderStatusUpdateRequestFactory.createMarkAsDeliveringRequest();

        OrderUpdateResponse orderUpdateResponse = orderService.updateOrder(
                order.getId(), staff.getPhone(), cancelRequest
        );

        assertNotNull(orderUpdateResponse);
        assertNotNull(orderUpdateResponse.message());

        assertEquals(OrderStatus.DELIVERING, order.getOrderStatus());

        verify(orderRepository).findById(order.getId());
        verify(userService).findUserOrThrow(staff.getPhone());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrder_deliveredRequest_unpaidCODOrder_shouldReturnOrderUpdateResponse() {
        User staff = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.STAFF);

        Order order = OrderFactory.createUnpaidCODAndDeliveringOrder(staff, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        when(userService.findUserOrThrow(staff.getPhone())).thenReturn(staff);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderStatusUpdateRequest cancelRequest =
                OrderStatusUpdateRequestFactory.createMarkAsDeliveredRequest();

        OrderUpdateResponse orderUpdateResponse = orderService.updateOrder(
                order.getId(), staff.getPhone(), cancelRequest
        );

        assertNotNull(orderUpdateResponse);
        assertNotNull(orderUpdateResponse.message());

        assertEquals(OrderStatus.DELIVERED, order.getOrderStatus());
        assertEquals(PaymentStatus.PAID, order.getPaymentStatus());

        verify(orderRepository).findById(order.getId());
        verify(userService).findUserOrThrow(staff.getPhone());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrder_deliveredRequest_paidOnlineOrder_shouldReturnOrderUpdateResponse() {
        User staff = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.STAFF);

        Order order = OrderFactory.createPaidOnlineAndDeliveringOrder(staff, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        when(userService.findUserOrThrow(staff.getPhone())).thenReturn(staff);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderStatusUpdateRequest cancelRequest =
                OrderStatusUpdateRequestFactory.createMarkAsDeliveredRequest();

        OrderUpdateResponse orderUpdateResponse = orderService.updateOrder(
                order.getId(), staff.getPhone(), cancelRequest
        );

        assertNotNull(orderUpdateResponse);
        assertNotNull(orderUpdateResponse.message());

        assertEquals(OrderStatus.DELIVERED, order.getOrderStatus());

        verify(orderRepository).findById(order.getId());
        verify(userService).findUserOrThrow(staff.getPhone());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrder_cancelRequestByUser_unpaidCODAndPendingOrder_shouldReturnOrderUpdateResponse() {
        User user = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.USER);

        Order order = OrderFactory.createUnpaidCODAndPendingOrder(user, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderStatusUpdateRequest cancelRequest =
                OrderStatusUpdateRequestFactory.createCancelRequest();

        OrderUpdateResponse orderUpdateResponse = orderService.updateOrder(
                order.getId(), user.getPhone(), cancelRequest
        );

        assertNotNull(orderUpdateResponse);
        assertNotNull(orderUpdateResponse.message());

        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());

        verify(orderRepository).findById(order.getId());
        verify(userService).findUserOrThrow(user.getPhone());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrder_cancelRequestByUser_paidCODAndPendingOrder_shouldReturnOrderUpdateResponse() {
        User user = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.USER);

        Order order = OrderFactory.createPaidCODAndPendingOrder(user, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderStatusUpdateRequest cancelRequest =
                OrderStatusUpdateRequestFactory.createCancelRequest();

        OrderUpdateResponse orderUpdateResponse = orderService.updateOrder(
                order.getId(), user.getPhone(), cancelRequest
        );

        assertNotNull(orderUpdateResponse);
        assertNotNull(orderUpdateResponse.message());

        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());

        verify(orderRepository).findById(order.getId());
        verify(userService).findUserOrThrow(user.getPhone());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrder_cancelRequestByUser_unpaidCODAndDeliveringOrder_shouldThrowInvalidOrderStatusException() {
        User user = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.USER);

        Order order = OrderFactory.createUnpaidCODAndDeliveringOrder(user, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        OrderStatusUpdateRequest cancelRequest =
                OrderStatusUpdateRequestFactory.createCancelRequest();

        assertThrows(InvalidOrderStatusException.class, () -> orderService.updateOrder(
                order.getId(), user.getPhone(), cancelRequest
        ));

        verify(orderRepository).findById(order.getId());
        verify(userService).findUserOrThrow(user.getPhone());
    }

    @Test
    void updateOrder_cancelRequestByUser_paidOnlineAndPendingOrder_shouldThrowOrderCannotBeCancelledException() {
        User user = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.USER);

        Order order = OrderFactory.createPaidOnlineAndPendingOrder(user, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        OrderStatusUpdateRequest cancelRequest =
                OrderStatusUpdateRequestFactory.createCancelRequest();

        assertThrows(OrderCannotBeCancelledException.class, () -> orderService.updateOrder(
                order.getId(), user.getPhone(), cancelRequest
        ));

        verify(orderRepository).findById(order.getId());
        verify(userService).findUserOrThrow(user.getPhone());
    }

    @Test
    void updateOrder_cancelRequestByStaff_unpaidCODAndPendingOrder_shouldReturnOrderUpdateResponse() {
        User staff = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.STAFF);

        Order order = OrderFactory.createUnpaidCODAndPendingOrder(staff, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        when(userService.findUserOrThrow(staff.getPhone())).thenReturn(staff);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderStatusUpdateRequest cancelRequest =
                OrderStatusUpdateRequestFactory.createCancelRequest();

        OrderUpdateResponse orderUpdateResponse = orderService.updateOrder(
                order.getId(), staff.getPhone(), cancelRequest
        );

        assertNotNull(orderUpdateResponse);
        assertNotNull(orderUpdateResponse.message());

        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());

        verify(orderRepository).findById(order.getId());
        verify(userService).findUserOrThrow(staff.getPhone());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrder_cancelRequestByStaff_confirmedOrder_shouldReturnOrderUpdateResponse() {
        User staff = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.STAFF);

        Order order = OrderFactory.createUnpaidCODAndConfirmedOrder(staff, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        when(userService.findUserOrThrow(staff.getPhone())).thenReturn(staff);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderStatusUpdateRequest cancelRequest =
                OrderStatusUpdateRequestFactory.createCancelRequest();

        OrderUpdateResponse orderUpdateResponse = orderService.updateOrder(
                order.getId(), staff.getPhone(), cancelRequest
        );

        assertNotNull(orderUpdateResponse);
        assertNotNull(orderUpdateResponse.message());

        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());

        verify(orderRepository).findById(order.getId());
        verify(userService).findUserOrThrow(staff.getPhone());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrder_cancelRequestByAdmin_shouldThrowForbiddenException() {
        User user = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.USER);

        Order order = OrderFactory.createUnpaidCODAndPendingOrder(user, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        User admin = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.ADMIN);

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(admin);

        OrderStatusUpdateRequest cancelRequest =
                OrderStatusUpdateRequestFactory.createCancelRequest();

        assertThrows(
                ForbiddenException.class,
                () -> orderService.updateOrder(order.getId(), admin.getPhone(), cancelRequest)
        );

        verify(orderRepository).findById(order.getId());
        verify(userService).findUserOrThrow(user.getPhone());
    }

    @Test
    void getOrder_notFoundUser_shouldThrowUserNotFoundException() {
        when(userService.findUserOrThrow(USER_PHONE))
                .thenThrow(new UserNotFoundException(USER_PHONE));

        assertThrows(
                UserNotFoundException.class,
                () -> orderService.getOrder(USER_PHONE, ORDER_ID)
        );

        verify(userService).findUserOrThrow(USER_PHONE);
    }

    @Test
    void getOrder_notFoundOrder_shouldThrowOrderNotFoundException() {
        User user = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.USER);

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        when(orderRepository.findById(ORDER_ID)).thenThrow(new OrderNotFoundException(ORDER_ID));
        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrder(user.getPhone(), ORDER_ID)
        );

        verify(userService).findUserOrThrow(user.getPhone());
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void getOrder_byAdmin_shouldReturnOrderResponse() {
        User admin = UserFactory.createActivatedUserWithRole(ADMIN_ID, UserRole.ADMIN);

        when(userService.findUserOrThrow(admin.getPhone())).thenReturn(admin);

        Order order = OrderFactory.createpPendingOrder(admin, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        OrderResponse orderResponse = orderService.getOrder(admin.getPhone(), ORDER_ID);

        assertNotNull(orderResponse);
        assertNotNull(orderResponse.order());

        assertEquals(ORDER_ID, orderResponse.order().id());
        assertEquals(admin.getId(), orderResponse.order().user().id());

        verify(userService).findUserOrThrow(admin.getPhone());
        verify(orderRepository).findById(order.getId());
    }

    @Test
    void getOrder_byUser_shouldReturnOrderResponse() {
        User user = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.USER);

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Order order = OrderFactory.createpPendingOrder(user, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        OrderResponse orderResponse = orderService.getOrder(user.getPhone(), ORDER_ID);

        assertNotNull(orderResponse);
        assertNotNull(orderResponse.order());

        assertEquals(ORDER_ID, orderResponse.order().id());
        assertEquals(user.getId(), orderResponse.order().user().id());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(orderRepository).findById(order.getId());
    }

    @Test
    void getOrder_byUser_whenNotOwner_shouldThrowAccessDeniedException() {
        User user = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.USER);

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        User staff = UserFactory.createActivatedUserWithRole(STAFF_ID, UserRole.STAFF);

        Order order = OrderFactory.createpPendingOrder(staff, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThrows(
                AccessDeniedException.class,
                () -> orderService.getOrder(user.getPhone(), order.getId())
        );

        verify(userService).findUserOrThrow(user.getPhone());
        verify(orderRepository).findById(order.getId());
    }

    @Test
    void getOrder_byStaff_shouldReturnOrderResponse() {
        User staff = UserFactory.createActivatedUserWithRole(STAFF_ID, UserRole.STAFF);

        when(userService.findUserOrThrow(staff.getPhone())).thenReturn(staff);

        Order order = OrderFactory.createpPendingOrder(staff, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        OrderResponse orderResponse = orderService.getOrder(staff.getPhone(), ORDER_ID);

        assertNotNull(orderResponse);
        assertNotNull(orderResponse.order());

        assertEquals(ORDER_ID, orderResponse.order().id());
        assertEquals(staff.getId(), orderResponse.order().user().id());

        verify(userService).findUserOrThrow(staff.getPhone());
        verify(orderRepository).findById(order.getId());
    }

    @Test
    void getOrder_byStaff_whenNotOwnerAndIncompletedOrder_shouldReturnOrderResponse() {
        User staff = UserFactory.createActivatedUserWithRole(STAFF_ID, UserRole.STAFF);

        when(userService.findUserOrThrow(staff.getPhone())).thenReturn(staff);

        User user = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.USER);

        Order order = OrderFactory.createpPendingOrder(user, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        OrderResponse orderResponse = orderService.getOrder(staff.getPhone(), ORDER_ID);

        assertNotNull(orderResponse);
        assertNotNull(orderResponse.order());

        assertEquals(ORDER_ID, orderResponse.order().id());

        assertNotEquals(staff.getId(), orderResponse.order().user().id());

        verify(userService).findUserOrThrow(staff.getPhone());
        verify(orderRepository).findById(order.getId());
    }

    @Test
    void getOrder_byStaff_whenNotOwnerAndDeliveredOrder_shouldThrowAccessDeniedException() {
        User staff = UserFactory.createActivatedUserWithRole(STAFF_ID, UserRole.STAFF);

        when(userService.findUserOrThrow(staff.getPhone())).thenReturn(staff);

        User user = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.USER);

        Order order = OrderFactory.createDeliveredOrder(user, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThrows(
                AccessDeniedException.class,
                () -> orderService.getOrder(staff.getPhone(), order.getId())
        );

        verify(userService).findUserOrThrow(staff.getPhone());
        verify(orderRepository).findById(order.getId());
    }

    @Test
    void getOrder_byStaff_whenNotOwnerAndCancelledOrder_shouldThrowAccessDeniedException() {
        User staff = UserFactory.createActivatedUserWithRole(STAFF_ID, UserRole.STAFF);

        when(userService.findUserOrThrow(staff.getPhone())).thenReturn(staff);

        User user = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.USER);

        Order order = OrderFactory.createCancelledOrder(user, ORDER_ID);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThrows(
                AccessDeniedException.class,
                () -> orderService.getOrder(staff.getPhone(), order.getId())
        );

        verify(userService).findUserOrThrow(staff.getPhone());
        verify(orderRepository).findById(order.getId());
    }

    @Test
    void getOrders_activeOrder_shouldReturnOrderPageResponse() {
        User user = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.USER);

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Order pendingOrder = OrderFactory.createpPendingOrder(user, ORDER_ID);

        List<Order> activeOrders = List.of(pendingOrder);

        Page<Order> activeOrderPage = OrderPageFactory.createOrderPage(activeOrders);

        when(
                orderRepository.findByUserAndDeliveredAtIsNullAndCancelledAtIsNull(user, PAGEABLE)
        ).thenReturn(activeOrderPage);

        OrderPageResponse orderPageResponse =
                orderService.getOrders(user.getPhone(), OrderQueryType.ACTIVE, PAGE, SIZE);

        assertNotNull(orderPageResponse);

        assertEquals(activeOrders.size(), orderPageResponse.orders().size());

        verify(userService, times(2)).findUserOrThrow(user.getPhone());
        verify(orderRepository).findByUserAndDeliveredAtIsNullAndCancelledAtIsNull(user, PAGEABLE);
    }

    @Test
    void getOrders_activeOrder_emptyList_shouldReturnOrderPageResponse() {
        User user = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.USER);

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Page<Order> activeOrderPage = OrderPageFactory.createOrderPage(List.of());

        when(
                orderRepository.findByUserAndDeliveredAtIsNullAndCancelledAtIsNull(user, PAGEABLE)
        ).thenReturn(activeOrderPage);

        OrderPageResponse orderPageResponse =
                orderService.getOrders(user.getPhone(), OrderQueryType.ACTIVE, PAGE, SIZE);

        assertNotNull(orderPageResponse);

        assertTrue(orderPageResponse.orders().isEmpty());

        verify(userService, times(2)).findUserOrThrow(user.getPhone());
        verify(orderRepository).findByUserAndDeliveredAtIsNullAndCancelledAtIsNull(user, PAGEABLE);
    }

    @Test
    void getOrders_activeOrder_notFoundUser_shouldThrowUserNotFoundException() {
        when(userService.findUserOrThrow(USER_PHONE))
                .thenThrow(new UserNotFoundException(USER_PHONE));

        assertThrows(
                UserNotFoundException.class,
                () -> orderService.getOrders(USER_PHONE, OrderQueryType.ACTIVE, PAGE, SIZE)
        );

        verify(userService).findUserOrThrow(USER_PHONE);
    }

    @Test
    void getOrders_orderHistory_shouldReturnOrderPageResponse() {
        User user = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.USER);

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Order cancelledOrder = OrderFactory.createCancelledOrder(user, ORDER_ID);

        List<Order> cancelledOrders = List.of(cancelledOrder);

        Page<Order> cancelledOrderPage = OrderPageFactory.createOrderPage(cancelledOrders);

        when(
                orderRepository.findCompletedOrCancelledOrdersByUser(user, PAGEABLE)
        ).thenReturn(cancelledOrderPage);

        OrderPageResponse orderPageResponse =
                orderService.getOrders(user.getPhone(), OrderQueryType.HISTORY, PAGE, SIZE);

        assertNotNull(orderPageResponse);

        assertEquals(cancelledOrders.size(), orderPageResponse.orders().size());

        verify(userService, times(2)).findUserOrThrow(user.getPhone());
        verify(orderRepository).findCompletedOrCancelledOrdersByUser(user, PAGEABLE);
    }

    @Test
    void getOrders_orderHistory_emptyList_shouldReturnOrderPageResponse() {
        User user = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.USER);

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Page<Order> cancelledOrderPage = OrderPageFactory.createOrderPage(List.of());

        when(
                orderRepository.findCompletedOrCancelledOrdersByUser(user, PAGEABLE)
        ).thenReturn(cancelledOrderPage);

        OrderPageResponse orderPageResponse =
                orderService.getOrders(user.getPhone(), OrderQueryType.HISTORY, PAGE, SIZE);

        assertNotNull(orderPageResponse);

        assertTrue(orderPageResponse.orders().isEmpty());

        verify(userService, times(2)).findUserOrThrow(user.getPhone());
        verify(orderRepository).findCompletedOrCancelledOrdersByUser(user, PAGEABLE);
    }

    @Test
    void getOrders_unfinishedOrder_shouldReturnOrderPageResponse() {
        User user = UserFactory.createActivatedUserWithRole(STAFF_ID, UserRole.STAFF);

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Order pendingOrder = OrderFactory.createpPendingOrder(user, ORDER_ID);

        List<Order> pendingOrders = List.of(pendingOrder);

        Page<Order> pendingOrderPage = OrderPageFactory.createOrderPage(pendingOrders);

        when(
                orderRepository.findByDeliveredAtIsNullAndCancelledAtIsNull(PAGEABLE)
        ).thenReturn(pendingOrderPage);

        OrderPageResponse orderPageResponse =
                orderService.getOrders(user.getPhone(), OrderQueryType.UNFINISHED, PAGE, SIZE);

        assertNotNull(orderPageResponse);

        assertEquals(pendingOrders.size(), orderPageResponse.orders().size());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(orderRepository).findByDeliveredAtIsNullAndCancelledAtIsNull(PAGEABLE);
    }

    @Test
    void getOrders_unfinishedOrder_emptyList_shouldReturnOrderPageResponse() {
        User user = UserFactory.createActivatedUserWithRole(STAFF_ID, UserRole.STAFF);

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Page<Order> pendingOrderPage = OrderPageFactory.createOrderPage(List.of());

        when(
                orderRepository.findByDeliveredAtIsNullAndCancelledAtIsNull(PAGEABLE)
        ).thenReturn(pendingOrderPage);

        OrderPageResponse orderPageResponse =
                orderService.getOrders(user.getPhone(), OrderQueryType.UNFINISHED, PAGE, SIZE);

        assertNotNull(orderPageResponse);

        assertTrue(orderPageResponse.orders().isEmpty());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(orderRepository).findByDeliveredAtIsNullAndCancelledAtIsNull(PAGEABLE);
    }

    @Test
    void getOrders_unfinishedOrder_invalidRole_shouldThrowAccessDeniedException() {
        User user = UserFactory.createActivatedUserWithRole(ADMIN_ID, UserRole.ADMIN);

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        assertThrows(
                AccessDeniedException.class,
                () -> orderService.getOrders(user.getPhone(), OrderQueryType.UNFINISHED, PAGE, SIZE)
        );

        verify(userService).findUserOrThrow(user.getPhone());
    }

    @Test
    void getOrders_byAdmin_shouldReturnOrderPageResponse() {
        User user = UserFactory.createActivatedUserWithRole(ADMIN_ID, UserRole.ADMIN);

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Order pendingOrder = OrderFactory.createpPendingOrder(user, ORDER_ID);

        List<Order> pendingOrders = List.of(pendingOrder);

        Page<Order> pendingOrderPage = OrderPageFactory.createOrderPage(pendingOrders);

        when(orderRepository.findAll(PAGEABLE)).thenReturn(pendingOrderPage);

        OrderPageResponse orderPageResponse =
                orderService.getOrders(user.getPhone(), OrderQueryType.ALL, PAGE, SIZE);

        assertNotNull(orderPageResponse);

        assertEquals(pendingOrders.size(), orderPageResponse.orders().size());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(orderRepository).findAll(PAGEABLE);
    }

    @Test
    void getOrders_byAdmin_emptyList_shouldReturnOrderPageResponse() {
        User user = UserFactory.createActivatedUserWithRole(ADMIN_ID, UserRole.ADMIN);

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Page<Order> pendingOrderPage = OrderPageFactory.createOrderPage(List.of());

        when(orderRepository.findAll(PAGEABLE)).thenReturn(pendingOrderPage);

        OrderPageResponse orderPageResponse =
                orderService.getOrders(user.getPhone(), OrderQueryType.ALL, PAGE, SIZE);

        assertNotNull(orderPageResponse);

        assertTrue(orderPageResponse.orders().isEmpty());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(orderRepository).findAll(PAGEABLE);
    }

    @Test
    void getOrders_byAdmin_invalidRole_shouldThrowAccessDeniedException() {
        User user = UserFactory.createActivatedUserWithRole(USER_ID, UserRole.USER);

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        assertThrows(
                AccessDeniedException.class,
                () -> orderService.getOrders(user.getPhone(), OrderQueryType.ALL, PAGE, SIZE)
        );

        verify(userService).findUserOrThrow(user.getPhone());
    }

    @Test
    void getOrderStats_shouldReturnOrderStatsResponse() {
        OrderStatsProjection orderStatsProjection = mock(OrderStatsProjection.class);

        Long amount = 1000L;
        BigDecimal revenue = BigDecimal.valueOf(amount);

        when(orderStatsProjection.getPendingOrderAmount()).thenReturn(amount);
        when(orderStatsProjection.getConfirmedOrderAmount()).thenReturn(amount);
        when(orderStatsProjection.getDeliveringOrderAmount()).thenReturn(amount);
        when(orderStatsProjection.getDeliveredOrderAmount()).thenReturn(amount);
        when(orderStatsProjection.getCancelledOrderAmount()).thenReturn(amount);
        when(orderStatsProjection.getCashOnDeliveryOrderAmount()).thenReturn(amount);
        when(orderStatsProjection.getBankTransferOrderAmount()).thenReturn(amount);
        when(orderStatsProjection.getDiscountedOrderAmount()).thenReturn(amount);
        when(orderStatsProjection.getCashOnDeliveryRevenue()).thenReturn(revenue);
        when(orderStatsProjection.getBankTransferRevenue()).thenReturn(revenue);
        when(orderStatsProjection.getTotalRevenue()).thenReturn(revenue);

        when(orderRepository.getStats()).thenReturn(orderStatsProjection);

        OrderStatsResponse orderStatsResponse = orderService.getOrderStats();

        assertNotNull(orderStatsResponse);

        assertEquals(amount, orderStatsResponse.orderStats().pendingOrderAmount());
        assertEquals(revenue, orderStatsResponse.orderStats().totalRevenue());

        verify(orderRepository).getStats();
    }
}