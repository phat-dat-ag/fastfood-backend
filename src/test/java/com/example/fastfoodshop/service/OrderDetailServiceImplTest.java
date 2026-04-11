package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.entity.Cart;
import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.OrderDetail;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.exception.product.ProductNotFoundException;
import com.example.fastfoodshop.factory.cart.CartFactory;
import com.example.fastfoodshop.factory.order.OrderDetailFactory;
import com.example.fastfoodshop.factory.order.OrderFactory;
import com.example.fastfoodshop.factory.product.ProductFactory;
import com.example.fastfoodshop.factory.user.UserFactory;
import com.example.fastfoodshop.repository.OrderDetailRepository;
import com.example.fastfoodshop.service.implementation.OrderDetailServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderDetailServiceImplTest {
    @Mock
    ProductService productService;

    @Mock
    OrderDetailRepository orderDetailRepository;

    @InjectMocks
    OrderDetailServiceImpl orderDetailService;

    private static final Long PRODUCT_ID = 234L;
    private static final Long ORDER_ID = 123L;

    @Test
    void createOrderDetail_validRequest_shouldBeSuccessful() {
        User user = UserFactory.createActivatedUser();
        Product product = ProductFactory.createActivatedProduct(PRODUCT_ID);
        Cart cart = CartFactory.createValidCart(user, product);

        CartDTO cartDTO = CartDTO.from(cart);

        Order order = OrderFactory.createpPendingOrder(user, ORDER_ID);

        when(productService.findProductOrThrow(cartDTO.product().id())).thenReturn(product);

        OrderDetail orderDetail = OrderDetailFactory.createValid(
                order,
                product,
                cartDTO.quantity(),
                cartDTO.product().discountedPrice()
        );

        when(orderDetailRepository.save(any(OrderDetail.class))).thenReturn(orderDetail);

        orderDetailService.createOrderDetail(cartDTO, order);

        assertEquals(order.getId(), orderDetail.getOrder().getId());
        assertEquals(product.getId(), orderDetail.getProduct().getId());
        assertEquals(cartDTO.quantity(), orderDetail.getQuantity());

        verify(productService).findProductOrThrow(cartDTO.product().id());
        verify(orderDetailRepository).save(any(OrderDetail.class));
    }

    @Test
    void createOrderDetail_notFoundProduct_shouldThrowProductNotFoundException() {
        User user = UserFactory.createActivatedUser();
        Product product = ProductFactory.createActivatedProduct(PRODUCT_ID);
        Cart cart = CartFactory.createValidCart(user, product);

        CartDTO cartDTO = CartDTO.from(cart);

        Order order = OrderFactory.createpPendingOrder(user, ORDER_ID);

        when(productService.findProductOrThrow(cartDTO.product().id()))
                .thenThrow(new ProductNotFoundException(product.getId()));

        assertThrows(
                ProductNotFoundException.class,
                () -> orderDetailService.createOrderDetail(cartDTO, order)
        );

        verify(productService).findProductOrThrow(cartDTO.product().id());
    }
}