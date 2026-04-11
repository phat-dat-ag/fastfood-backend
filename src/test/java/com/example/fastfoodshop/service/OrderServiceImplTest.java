package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.enums.PaymentStatus;
import com.example.fastfoodshop.exception.order.OrderAlreadyCancelledException;
import com.example.fastfoodshop.exception.order.OrderNotFoundException;
import com.example.fastfoodshop.factory.order.OrderFactory;
import com.example.fastfoodshop.factory.user.UserFactory;
import com.example.fastfoodshop.repository.OrderRepository;
import com.example.fastfoodshop.service.implementation.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderServiceImpl orderService;

    private static final Long ORDER_ID = 3456L;

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
}