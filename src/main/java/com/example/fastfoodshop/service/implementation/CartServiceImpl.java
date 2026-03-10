package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.constant.CartConstant;
import com.example.fastfoodshop.dto.*;
import com.example.fastfoodshop.entity.Cart;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.exception.cart.CartNotFoundException;
import com.example.fastfoodshop.exception.cart.ProductAmountExceededException;
import com.example.fastfoodshop.exception.cart.QuantityExceededException;
import com.example.fastfoodshop.repository.CartRepository;
import com.example.fastfoodshop.request.CartCreateRequest;
import com.example.fastfoodshop.request.CartUpdateRequest;
import com.example.fastfoodshop.request.DeliveryRequest;
import com.example.fastfoodshop.response.CartResponse;
import com.example.fastfoodshop.service.UserService;
import com.example.fastfoodshop.service.CategoryService;
import com.example.fastfoodshop.service.ProductService;
import com.example.fastfoodshop.service.PromotionService;
import com.example.fastfoodshop.service.DeliveryService;
import com.example.fastfoodshop.service.CartService;
import com.example.fastfoodshop.util.PromotionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final UserService userService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final CartRepository cartRepository;
    private final PromotionService promotionService;
    private final DeliveryService deliveryService;

    private Cart findCartOrThrow(User user, Product product) {
        return cartRepository.findByUserAndProduct(user, product).orElseThrow(
                () -> new CartNotFoundException(product.getId())
        );
    }

    private void setNewProductQuantityOrThrow(Cart cart, int quantity) {
        int newQuantity = cart.getQuantity() + quantity;
        if (newQuantity > CartConstant.MAX_QUANTITY_PER_PRODUCT) {
            throw new QuantityExceededException();
        }
        cart.setQuantity(newQuantity);
    }

    private void updateNewProductQuantityOrThrow(Cart cart, int newQuantity) {
        if (newQuantity > CartConstant.MAX_QUANTITY_PER_PRODUCT) {
            throw new QuantityExceededException();
        }
        cart.setQuantity(newQuantity);
    }

    public CartDTO addProductToCart(String userPhone, CartCreateRequest cartCreateRequest) {
        User user = userService.findUserOrThrow(userPhone);
        Product product = productService.findProductOrThrow(cartCreateRequest.getProductId());
        List<Cart> carts = cartRepository.findByUser(user);
        Optional<Cart> optionalCart = cartRepository.findByUserAndProduct(user, product);

        if (optionalCart.isEmpty()) {
            if (carts.size() >= CartConstant.MAX_PRODUCT_TYPES_PER_CART) {
                throw new ProductAmountExceededException();
            }
        }

        Cart cart = optionalCart.orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setProduct(product);
            newCart.setQuantity(0);
            return newCart;
        });

        setNewProductQuantityOrThrow(cart, cartCreateRequest.getQuantity());

        Cart savedCart = cartRepository.save(cart);
        return CartDTO.from(savedCart);
    }

    public CartResponse getCartResponse(String phone, String promotionCode, DeliveryRequest deliveryRequest) {
        User user = userService.findUserOrThrow(phone);
        List<Cart> carts = cartRepository.findByUser(user);
        ArrayList<CartDTO> cartDTOs = new ArrayList<>();
        for (Cart cart : carts) {
            Category category = cart.getProduct().getCategory();
            ProductDTO productDTO = new ProductDTO(cart.getProduct());
            categoryService.applyPromotion(productDTO, category);
            CartDTO cartDTO = new CartDTO(
                    new UserDTO(cart.getUser()),
                    productDTO,
                    cart.getQuantity()
            );
            cartDTOs.add(cartDTO);
        }
        CartResponse cartResponse = new CartResponse(cartDTOs);

        if (promotionCode != null && !promotionCode.isEmpty()) {
            PromotionCodeCheckResultDTO result = promotionService.checkPromotionCode(promotionCode, cartResponse.getSubtotalPrice());
            if (!result.isSuccess()) {
                throw new RuntimeException(result.getMessage());
            }
            cartResponse.setApplyPromotionResult(result);
            int totalPrice = PromotionUtils.calculateDiscountedPrice(cartResponse.getSubtotalPrice(), result.getPromotion());
            cartResponse.setTotalPrice(totalPrice);
        }

        DeliveryDTO deliveryInformation = deliveryService.calculateDelivery(deliveryRequest);
        cartResponse.setDeliveryInformation(deliveryInformation);
        cartResponse.setDeliveryFee(deliveryInformation.getFee());
        int totalPrice = cartResponse.getTotalPrice() + deliveryInformation.getFee();
        cartResponse.setTotalPrice(totalPrice);

        return cartResponse;
    }

    public CartResponse getCartDetailByUser(String phone, String promotionCode, DeliveryRequest deliveryRequest) {
        return getCartResponse(phone, promotionCode, deliveryRequest);
    }

    public CartDTO updateCart(String userPhone, CartUpdateRequest cartUpdateRequest) {
        User user = userService.findUserOrThrow(userPhone);
        Product product = productService.findProductOrThrow(cartUpdateRequest.getProductId());
        Cart cart = findCartOrThrow(user, product);

        updateNewProductQuantityOrThrow(cart, cartUpdateRequest.getQuantity());

        Cart updatedCart = cartRepository.save(cart);
        return CartDTO.from(updatedCart);
    }

    public CartDTO deleteProductFromCart(String phone, Long productId) {
        User user = userService.findUserOrThrow(phone);
        Product product = productService.findProductOrThrow(productId);
        Cart cart = findCartOrThrow(user, product);

        cartRepository.delete(cart);
        return CartDTO.from(cart);
    }

    public void deleteAllProductFromCart(String phone) {
        User user = userService.findUserOrThrow(phone);
        cartRepository.deleteAllByUser(user);
    }
}