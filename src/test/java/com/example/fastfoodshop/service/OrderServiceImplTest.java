package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.OrderQueryType;
import com.example.fastfoodshop.enums.PaymentStatus;
import com.example.fastfoodshop.enums.UserRole;
import com.example.fastfoodshop.exception.order.AccessDeniedException;
import com.example.fastfoodshop.exception.order.OrderAlreadyCancelledException;
import com.example.fastfoodshop.exception.order.OrderNotFoundException;
import com.example.fastfoodshop.exception.user.UserNotFoundException;
import com.example.fastfoodshop.factory.order.OrderFactory;
import com.example.fastfoodshop.factory.order.OrderPageFactory;
import com.example.fastfoodshop.factory.user.UserFactory;
import com.example.fastfoodshop.repository.OrderRepository;
import com.example.fastfoodshop.response.order.OrderPageResponse;
import com.example.fastfoodshop.service.implementation.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    @Mock
    OrderRepository orderRepository;

    @Mock
    UserService userService;

    @InjectMocks
    OrderServiceImpl orderService;

    private static final String USER_PHONE = "0999998888";

    private static final Long ORDER_ID = 3456L;

    private static final int PAGE = 5;
    private static final int SIZE = 5;

    private static final Pageable PAGEABLE = PageRequest.of(
            PAGE,
            SIZE,
            Sort.by(Order.Field.placedAt).descending()
    );

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
    void getOrders_activeOrder_shouldReturnOrderPageResponse() {
        User user = UserFactory.createActivatedUserWithRole(UserRole.USER);

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
        User user = UserFactory.createActivatedUserWithRole(UserRole.USER);

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
        User user = UserFactory.createActivatedUserWithRole(UserRole.USER);

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
        User user = UserFactory.createActivatedUserWithRole(UserRole.USER);

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
        User user = UserFactory.createActivatedUserWithRole(UserRole.STAFF);

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
        User user = UserFactory.createActivatedUserWithRole(UserRole.STAFF);

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
        User user = UserFactory.createActivatedUserWithRole(UserRole.ADMIN);

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        assertThrows(
                AccessDeniedException.class,
                () -> orderService.getOrders(user.getPhone(), OrderQueryType.UNFINISHED, PAGE, SIZE)
        );

        verify(userService).findUserOrThrow(user.getPhone());
    }

    @Test
    void getOrders_byAdmin_shouldReturnOrderPageResponse() {
        User user = UserFactory.createActivatedUserWithRole(UserRole.ADMIN);

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
        User user = UserFactory.createActivatedUserWithRole(UserRole.ADMIN);

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
        User user = UserFactory.createActivatedUserWithRole(UserRole.USER);

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        assertThrows(
                AccessDeniedException.class,
                () -> orderService.getOrders(user.getPhone(), OrderQueryType.ALL, PAGE, SIZE)
        );

        verify(userService).findUserOrThrow(user.getPhone());
    }
}