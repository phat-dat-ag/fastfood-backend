package com.example.fastfoodshop.service;

import com.example.fastfoodshop.constant.CartConstant;
import com.example.fastfoodshop.dto.PromotionResult;
import com.example.fastfoodshop.dto.DeliveryDTO;
import com.example.fastfoodshop.entity.Cart;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.exception.cart.CartNotFoundException;
import com.example.fastfoodshop.exception.cart.ProductAmountExceededException;
import com.example.fastfoodshop.exception.cart.QuantityExceededException;
import com.example.fastfoodshop.exception.product.ProductNotFoundException;
import com.example.fastfoodshop.exception.user.UserNotFoundException;
import com.example.fastfoodshop.exception.address.AddressNotFoundException;
import com.example.fastfoodshop.factory.cart.CartCreateRequestFactory;
import com.example.fastfoodshop.factory.cart.CartFactory;
import com.example.fastfoodshop.factory.delivery.DeliveryDTOFactory;
import com.example.fastfoodshop.factory.product.ProductFactory;
import com.example.fastfoodshop.factory.promotion.PromotionFactory;
import com.example.fastfoodshop.factory.promotion.PromotionResultFactory;
import com.example.fastfoodshop.factory.user.UserFactory;
import com.example.fastfoodshop.repository.CartRepository;
import com.example.fastfoodshop.request.CartCreateRequest;
import com.example.fastfoodshop.response.cart.CartDetailResponse;
import com.example.fastfoodshop.response.cart.CartResponse;
import com.example.fastfoodshop.response.cart.CartUpdateResponse;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {
    @Mock
    UserService userService;

    @Mock
    ProductService productService;

    @Mock
    CartRepository cartRepository;

    @Mock
    CategoryService categoryService;

    @Mock
    PromotionService promotionService;

    @Mock
    DeliveryService deliveryService;

    @InjectMocks
    CartServiceImpl cartService;

    private static final String USER_PHONE = "0999999999";
    private static final Long PRODUCT_ID = 100L;
    private static final int NEW_VALID_QUANTITY = 1;

    private static final Long ADDRESS_ID = 1111L;
    private static final Long NULL_ADDRESS_ID = null;

    private static final String PROMOTION_CODE = "KM-123-TraSua";
    private static final String NULL_PROMOTION_CODE = null;
    private static final String BLANK_PROMOTION_CODE = "           ";

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

    @Test
    void getCartDetailByUser_withValidPromotion_shouldReturnCartDetailResponse() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        List<Cart> carts = CartFactory.createCartsForUser(user);

        when(cartRepository.findByUser(user)).thenReturn(carts);

        Promotion promotion = PromotionFactory.createActivatedPromotion();

        PromotionResult promotionResult = PromotionResultFactory.createValid(promotion.getId());

        when(categoryService.applyPromotion(any(Product.class), any(Category.class)))
                .thenReturn(promotionResult);

        when(promotionService.checkPromotionCode(eq(PROMOTION_CODE), anyInt())).thenReturn(promotion);

        DeliveryDTO deliveryInformation = DeliveryDTOFactory.createAcceptedDelivery();

        when(deliveryService.calculateDelivery(any(Long.class))).thenReturn(deliveryInformation);

        CartDetailResponse cartDetailResponse = cartService.getCartDetailByUser(
                user.getPhone(), PROMOTION_CODE, ADDRESS_ID
        );

        assertNotNull(cartDetailResponse);
        assertNotNull(cartDetailResponse.carts());
        assertNotNull(cartDetailResponse.deliveryInformation());

        assertFalse(cartDetailResponse.carts().isEmpty());

        assertEquals(carts.size(), cartDetailResponse.carts().size());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(cartRepository).findByUser(user);
        verify(categoryService, times(carts.size())).applyPromotion(any(Product.class), any(Category.class));
        verify(promotionService).checkPromotionCode(eq(PROMOTION_CODE), anyInt());
        verify(deliveryService).calculateDelivery(any(Long.class));
    }

    @Test
    void getCartDetailByUser_withNullPromotionCode_shouldReturnCartDetailResponse() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        List<Cart> carts = CartFactory.createCartsForUser(user);

        when(cartRepository.findByUser(user)).thenReturn(carts);

        PromotionResult promotionResult = PromotionResultFactory.createValid(null);

        when(categoryService.applyPromotion(any(Product.class), any(Category.class)))
                .thenReturn(promotionResult);

        DeliveryDTO deliveryInformation = DeliveryDTOFactory.createAcceptedDelivery();

        when(deliveryService.calculateDelivery(any(Long.class))).thenReturn(deliveryInformation);

        CartDetailResponse cartDetailResponse = cartService.getCartDetailByUser(
                user.getPhone(), NULL_PROMOTION_CODE, ADDRESS_ID
        );

        assertNotNull(cartDetailResponse);
        assertNotNull(cartDetailResponse.carts());
        assertNotNull(cartDetailResponse.deliveryInformation());

        assertFalse(cartDetailResponse.carts().isEmpty());

        assertEquals(carts.size(), cartDetailResponse.carts().size());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(cartRepository).findByUser(user);
        verify(categoryService, times(carts.size())).applyPromotion(any(Product.class), any(Category.class));
        verify(deliveryService).calculateDelivery(any(Long.class));
    }

    @Test
    void getCartDetailByUser_withBlankPromotionCode_shouldReturnCartDetailResponse() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        List<Cart> carts = CartFactory.createCartsForUser(user);

        when(cartRepository.findByUser(user)).thenReturn(carts);

        PromotionResult promotionResult = PromotionResultFactory.createValid(null);

        when(categoryService.applyPromotion(any(Product.class), any(Category.class)))
                .thenReturn(promotionResult);


        DeliveryDTO deliveryInformation = DeliveryDTOFactory.createAcceptedDelivery();

        when(deliveryService.calculateDelivery(any(Long.class))).thenReturn(deliveryInformation);

        CartDetailResponse cartDetailResponse = cartService.getCartDetailByUser(
                user.getPhone(), BLANK_PROMOTION_CODE, ADDRESS_ID
        );

        assertNotNull(cartDetailResponse);
        assertNotNull(cartDetailResponse.carts());
        assertNotNull(cartDetailResponse.deliveryInformation());

        assertFalse(cartDetailResponse.carts().isEmpty());

        assertEquals(carts.size(), cartDetailResponse.carts().size());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(cartRepository).findByUser(user);
        verify(categoryService, times(carts.size())).applyPromotion(any(Product.class), any(Category.class));
        verify(deliveryService).calculateDelivery(any(Long.class));
    }

    @Test
    void getCartDetailByUser_withEmptyCart_shouldReturnCartDetailResponse() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        List<Cart> carts = List.of();

        when(cartRepository.findByUser(user)).thenReturn(carts);

        DeliveryDTO deliveryInformation = DeliveryDTOFactory.createAcceptedDelivery();

        when(deliveryService.calculateDelivery(any(Long.class))).thenReturn(deliveryInformation);

        CartDetailResponse cartDetailResponse = cartService.getCartDetailByUser(
                user.getPhone(), BLANK_PROMOTION_CODE, ADDRESS_ID
        );

        assertNotNull(cartDetailResponse);
        assertNotNull(cartDetailResponse.carts());
        assertNotNull(cartDetailResponse.deliveryInformation());

        assertTrue(cartDetailResponse.carts().isEmpty());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(cartRepository).findByUser(user);
        verify(deliveryService).calculateDelivery(any(Long.class));
    }

    @Test
    void getCartDetailByUser_withNullAddressId_shouldReturnCartDetailResponse() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        List<Cart> carts = CartFactory.createCartsForUser(user);

        when(cartRepository.findByUser(user)).thenReturn(carts);

        Promotion promotion = PromotionFactory.createActivatedPromotion();

        PromotionResult promotionResult = PromotionResultFactory.createValid(promotion.getId());

        when(categoryService.applyPromotion(any(Product.class), any(Category.class)))
                .thenReturn(promotionResult);

        when(promotionService.checkPromotionCode(eq(PROMOTION_CODE), anyInt())).thenReturn(promotion);

        DeliveryDTO rejectedDelivery = DeliveryDTOFactory.createRejectedDelivery();

        when(deliveryService.calculateDelivery(NULL_ADDRESS_ID)).thenReturn(rejectedDelivery);

        CartDetailResponse cartDetailResponse = cartService.getCartDetailByUser(
                user.getPhone(), PROMOTION_CODE, NULL_ADDRESS_ID
        );

        assertNotNull(cartDetailResponse);
        assertNotNull(cartDetailResponse.carts());
        assertNotNull(cartDetailResponse.deliveryInformation());

        assertFalse(cartDetailResponse.carts().isEmpty());

        assertEquals(carts.size(), cartDetailResponse.carts().size());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(cartRepository).findByUser(user);
        verify(categoryService, times(carts.size())).applyPromotion(any(Product.class), any(Category.class));
        verify(promotionService).checkPromotionCode(eq(PROMOTION_CODE), anyInt());
        verify(deliveryService).calculateDelivery(NULL_ADDRESS_ID);
    }

    @Test
    void getCartDetailByUser_notFoundUser_shouldThrowUserNotFoundException() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone()))
                .thenThrow(new UserNotFoundException(user.getPhone()));

        assertThrows(
                UserNotFoundException.class,
                () -> cartService.getCartDetailByUser(user.getPhone(), BLANK_PROMOTION_CODE, ADDRESS_ID)
        );

        verify(userService).findUserOrThrow(user.getPhone());
    }

    @Test
    void getCartDetailByUser_notFoundAddress_shouldThrowAddressNotFoundException() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        List<Cart> carts = CartFactory.createCartsForUser(user);

        when(cartRepository.findByUser(user)).thenReturn(carts);

        Promotion promotion = PromotionFactory.createActivatedPromotion();

        PromotionResult promotionResult = PromotionResultFactory.createValid(promotion.getId());

        when(categoryService.applyPromotion(any(Product.class), any(Category.class)))
                .thenReturn(promotionResult);

        when(promotionService.checkPromotionCode(eq(PROMOTION_CODE), anyInt())).thenReturn(promotion);

        when(deliveryService.calculateDelivery(any(Long.class))).thenThrow(new AddressNotFoundException());

        assertThrows(
                AddressNotFoundException.class,
                () -> cartService.getCartDetailByUser(
                        user.getPhone(), PROMOTION_CODE, ADDRESS_ID
                )
        );

        verify(userService).findUserOrThrow(user.getPhone());
        verify(cartRepository).findByUser(user);
        verify(categoryService, times(carts.size())).applyPromotion(any(Product.class), any(Category.class));
        verify(promotionService).checkPromotionCode(eq(PROMOTION_CODE), anyInt());
        verify(deliveryService).calculateDelivery(any(Long.class));
    }

    @Test
    void updateCartItem_validRequest_shouldReturnCartResponse() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Product product = ProductFactory.createActivatedProduct(PRODUCT_ID);

        when(productService.findProductOrThrow(product.getId())).thenReturn(product);

        Cart cart = CartFactory.createValidCart(user, product);

        when(cartRepository.findByUserAndProduct(user, product)).thenReturn(Optional.of(cart));

        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartResponse cartResponse = cartService.updateCartItem(
                user.getPhone(), product.getId(), NEW_VALID_QUANTITY
        );

        assertNotNull(cartResponse);
        assertNotNull(cartResponse.cart());

        assertEquals(user.getPhone(), cartResponse.cart().user().phone());
        assertEquals(product.getId(), cartResponse.cart().product().id());
        assertEquals(NEW_VALID_QUANTITY, cartResponse.cart().quantity());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(productService).findProductOrThrow(product.getId());
        verify(cartRepository).findByUserAndProduct(user, product);
    }

    @Test
    void updateCartItem_notFoundUser_shouldThrowUserNotFoundException() {
        when(userService.findUserOrThrow(USER_PHONE))
                .thenThrow(new UserNotFoundException(USER_PHONE));

        assertThrows(
                UserNotFoundException.class,
                () -> cartService.updateCartItem(USER_PHONE, PRODUCT_ID, NEW_VALID_QUANTITY)
        );

        verify(userService).findUserOrThrow(USER_PHONE);
    }

    @Test
    void updateCartItem_notFoundProduct_shouldThrowProductNotFoundException() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        when(productService.findProductOrThrow(PRODUCT_ID))
                .thenThrow(new ProductNotFoundException(PRODUCT_ID));

        assertThrows(
                ProductNotFoundException.class,
                () -> cartService.updateCartItem(user.getPhone(), PRODUCT_ID, NEW_VALID_QUANTITY)
        );

        verify(userService).findUserOrThrow(user.getPhone());
        verify(productService).findProductOrThrow(PRODUCT_ID);
    }

    @Test
    void updateCartItem_notFoundCart_shouldThrowCartNotFoundException() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Product product = ProductFactory.createActivatedProduct(PRODUCT_ID);

        when(productService.findProductOrThrow(product.getId())).thenReturn(product);

        when(cartRepository.findByUserAndProduct(user, product)).thenReturn(Optional.empty());

        assertThrows(
                CartNotFoundException.class,
                () -> cartService.updateCartItem(user.getPhone(), product.getId(), NEW_VALID_QUANTITY)
        );

        verify(userService).findUserOrThrow(user.getPhone());
        verify(productService).findProductOrThrow(product.getId());
        verify(cartRepository).findByUserAndProduct(user, product);
    }

    @Test
    void updateCartItem_newQuantityExceeded_shouldThrowQuantityExceededException() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Product product = ProductFactory.createActivatedProduct(PRODUCT_ID);

        when(productService.findProductOrThrow(product.getId())).thenReturn(product);

        Cart cart = CartFactory.createValidCart(user, product);

        when(cartRepository.findByUserAndProduct(user, product)).thenReturn(Optional.of(cart));

        assertThrows(
                QuantityExceededException.class,
                () -> cartService.updateCartItem(
                        user.getPhone(), product.getId(), CartConstant.MAX_QUANTITY_PER_PRODUCT * 2
                )
        );

        verify(userService).findUserOrThrow(user.getPhone());
        verify(productService).findProductOrThrow(product.getId());
        verify(cartRepository).findByUserAndProduct(user, product);
    }

    @Test
    void deleteProductFromCart_valiRequest_shouldReturnCartUpdateResponse() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Product product = ProductFactory.createActivatedProduct(PRODUCT_ID);

        when(productService.findProductOrThrow(product.getId())).thenReturn(product);

        Cart cart = CartFactory.createValidCart(user, product);

        when(cartRepository.findByUserAndProduct(user, product)).thenReturn(Optional.of(cart));

        doNothing().when(cartRepository).delete(cart);

        CartUpdateResponse cartUpdateResponse =
                cartService.deleteProductFromCart(user.getPhone(), product.getId());

        assertNotNull(cartUpdateResponse);
        assertNotNull(cartUpdateResponse.message());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(productService).findProductOrThrow(product.getId());
        verify(cartRepository).findByUserAndProduct(user, product);
        verify(cartRepository).delete(cart);
    }

    @Test
    void deleteProductFromCart_notFoundUser_shouldThrowUserNotFoundException() {
        when(userService.findUserOrThrow(USER_PHONE))
                .thenThrow(new UserNotFoundException(USER_PHONE));

        assertThrows(
                UserNotFoundException.class,
                () -> cartService.deleteProductFromCart(USER_PHONE, PRODUCT_ID)
        );

        verify(userService).findUserOrThrow(USER_PHONE);
    }

    @Test
    void deleteProductFromCart_notFoundProduct_shouldThrowProductNotFoundException() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        when(productService.findProductOrThrow(PRODUCT_ID))
                .thenThrow(new ProductNotFoundException(PRODUCT_ID));

        assertThrows(
                ProductNotFoundException.class,
                () -> cartService.deleteProductFromCart(user.getPhone(), PRODUCT_ID)
        );

        verify(userService).findUserOrThrow(user.getPhone());
        verify(productService).findProductOrThrow(PRODUCT_ID);
    }

    @Test
    void deleteProductFromCart_notFoundCart_shouldThrowCartNotFoundException() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        Product product = ProductFactory.createActivatedProduct(PRODUCT_ID);

        when(productService.findProductOrThrow(product.getId())).thenReturn(product);

        when(cartRepository.findByUserAndProduct(user, product)).thenReturn(Optional.empty());

        assertThrows(
                CartNotFoundException.class,
                () -> cartService.deleteProductFromCart(user.getPhone(), product.getId())
        );

        verify(userService).findUserOrThrow(user.getPhone());
        verify(productService).findProductOrThrow(product.getId());
        verify(cartRepository).findByUserAndProduct(user, product);
    }

    @Test
    void deleteAllProductFromCart_validRequest_shouldBeSuccessful() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        doNothing().when(cartRepository).deleteAllByUser(user);

        cartService.deleteAllProductFromCart(user.getPhone());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(cartRepository).deleteAllByUser(user);
    }

    @Test
    void deleteAllProductFromCart_notFoundUser_shouldThrowUserNotFoundException() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone()))
                .thenThrow(new UserNotFoundException(user.getPhone()));

        assertThrows(
                UserNotFoundException.class,
                () -> cartService.deleteAllProductFromCart(user.getPhone())
        );

        verify(userService).findUserOrThrow(user.getPhone());
    }
}