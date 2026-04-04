package com.example.fastfoodshop.service;

import com.example.fastfoodshop.constant.CartConstant;
import com.example.fastfoodshop.entity.Cart;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.exception.cart.ProductAmountExceededException;
import com.example.fastfoodshop.exception.cart.QuantityExceededException;
import com.example.fastfoodshop.factory.cart.CartCreateRequestFactory;
import com.example.fastfoodshop.factory.cart.CartFactory;
import com.example.fastfoodshop.factory.product.ProductFactory;
import com.example.fastfoodshop.factory.user.UserFactory;
import com.example.fastfoodshop.repository.CartRepository;
import com.example.fastfoodshop.request.CartCreateRequest;
import com.example.fastfoodshop.response.cart.CartResponse;
import com.example.fastfoodshop.service.implementation.CartServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {
    @Mock
    UserService userService;

    @Mock
    ProductService productService;

    @Mock
    CartRepository cartRepository;

    @InjectMocks
    CartServiceImpl cartService;

    private static final Long PRODUCT_ID = 100L;

    @Test
    void addProductToCart_existedValidCart_shouldReturnCartResponse() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Product product = ProductFactory.createActivatedProduct(PRODUCT_ID);

        when(productService.findProductOrThrow(product.getId())).thenReturn(product);

        List<Cart> carts = CartFactory.createCartsForUser(user);

        when(cartRepository.findByUser(user)).thenReturn(carts);

        Optional<Cart> validOptionalCart = CartFactory.createValidOptionalCart(user, product);

        when(cartRepository.findByUserAndProduct(user, product)).thenReturn(validOptionalCart);

        Cart cart = CartFactory.createValidCartForUser(user);

        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartCreateRequest validRequest = CartCreateRequestFactory.createValidForProduct(product);

        CartResponse cartResponse = cartService.addProductToCart(user.getPhone(), validRequest);

        assertNotNull(cartResponse);
        assertNotNull(cartResponse.cart());

        assertTrue(cartResponse.cart().quantity() <= CartConstant.MAX_QUANTITY_PER_PRODUCT);
        assertEquals(user.getId(), cartResponse.cart().user().id());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(productService).findProductOrThrow(product.getId());
        verify(cartRepository).findByUser(user);
        verify(cartRepository).findByUserAndProduct(user, product);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addProductToCart_newValidCart_shouldReturnCartResponse() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Product product = ProductFactory.createActivatedProduct(PRODUCT_ID);

        when(productService.findProductOrThrow(product.getId())).thenReturn(product);

        List<Cart> carts = CartFactory.createCartsForUser(user);

        when(cartRepository.findByUser(user)).thenReturn(carts);

        Optional<Cart> emptyOptionalCart = Optional.empty();

        when(cartRepository.findByUserAndProduct(user, product)).thenReturn(emptyOptionalCart);

        Cart cart = CartFactory.createValidCartForUser(user);

        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartCreateRequest validRequest = CartCreateRequestFactory.createValidForProduct(product);

        CartResponse cartResponse = cartService.addProductToCart(user.getPhone(), validRequest);

        assertNotNull(cartResponse);

        assertNotNull(cartResponse);
        assertNotNull(cartResponse.cart());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(productService).findProductOrThrow(product.getId());
        verify(cartRepository).findByUser(user);
        verify(cartRepository).findByUserAndProduct(user, product);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addProductToCart_productAmountExceeded_shouldThrowProductAmountExceededException() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Product product = ProductFactory.createActivatedProduct(PRODUCT_ID);

        when(productService.findProductOrThrow(product.getId())).thenReturn(product);

        List<Cart> carts = CartFactory.createCartForUserWithProductAmountExceeded(user);

        when(cartRepository.findByUser(user)).thenReturn(carts);

        Optional<Cart> emptyOptionalCart = Optional.empty();

        when(cartRepository.findByUserAndProduct(user, product)).thenReturn(emptyOptionalCart);

        CartCreateRequest validRequest = CartCreateRequestFactory.createValidForProduct(product);

        assertThrows(
                ProductAmountExceededException.class,
                () -> cartService.addProductToCart(user.getPhone(), validRequest)
        );

        verify(userService).findUserOrThrow(user.getPhone());
        verify(productService).findProductOrThrow(product.getId());
        verify(cartRepository).findByUser(user);
        verify(cartRepository).findByUserAndProduct(user, product);
    }

    @Test
    void addProductToCart_maxQuantity_shouldThrowQuantityExceededException() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Product product = ProductFactory.createActivatedProduct(PRODUCT_ID);

        when(productService.findProductOrThrow(product.getId())).thenReturn(product);

        List<Cart> carts = CartFactory.createCartsForUser(user);

        when(cartRepository.findByUser(user)).thenReturn(carts);

        Optional<Cart> optionalCart = CartFactory.createValidOptionalCart(user, product);

        when(cartRepository.findByUserAndProduct(user, product)).thenReturn(optionalCart);

        CartCreateRequest invalidRequest = CartCreateRequestFactory.createInvalidForProductWithMaxQuantity(product);

        assertThrows(
                QuantityExceededException.class,
                () -> cartService.addProductToCart(user.getPhone(), invalidRequest)
        );

        verify(userService).findUserOrThrow(user.getPhone());
        verify(productService).findProductOrThrow(product.getId());
        verify(cartRepository).findByUser(user);
        verify(cartRepository).findByUserAndProduct(user, product);
    }
}
