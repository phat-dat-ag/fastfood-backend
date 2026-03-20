package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.constant.CartConstant;
import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.dto.PromotionCodeCheckResultDTO;
import com.example.fastfoodshop.dto.UserDTO;
import com.example.fastfoodshop.dto.DeliveryDTO;
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
import com.example.fastfoodshop.response.cart.CartResponse;
import com.example.fastfoodshop.response.cart.CartDetailResponse;
import com.example.fastfoodshop.response.cart.CartUpdateResponse;
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

    public CartResponse addProductToCart(String userPhone, CartCreateRequest cartCreateRequest) {
        User user = userService.findUserOrThrow(userPhone);
        Product product = productService.findProductOrThrow(cartCreateRequest.productId());
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

        setNewProductQuantityOrThrow(cart, cartCreateRequest.quantity());

        Cart savedCart = cartRepository.save(cart);
        return new CartResponse(CartDTO.from(savedCart));
    }

    public CartDetailResponse getCartResponse(String phone, String promotionCode, DeliveryRequest deliveryRequest) {
        User user = userService.findUserOrThrow(phone);
        List<Cart> carts = cartRepository.findByUser(user);
        ArrayList<CartDTO> cartDTOs = new ArrayList<>();
        for (Cart cart : carts) {
            Category category = cart.getProduct().getCategory();
            Product product = cart.getProduct();
            categoryService.applyPromotion(product, category);
            CartDTO cartDTO = new CartDTO(
                    UserDTO.from(cart.getUser()),
                    ProductDTO.from(product),
                    cart.getQuantity()
            );
            cartDTOs.add(cartDTO);
        }
        int subtotalPrice = 0;
        for (CartDTO cartDTO : cartDTOs) {
            subtotalPrice += (cartDTO.quantity() * cartDTO.product().discountedPrice());
        }

        PromotionCodeCheckResultDTO promotionCodeCheckResultDTO = null;
        int totalPrice = 0;
        if (promotionCode != null && !promotionCode.isEmpty()) {
            promotionCodeCheckResultDTO = promotionService.checkPromotionCode(promotionCode, subtotalPrice);
            if (!promotionCodeCheckResultDTO.success()) {
                throw new RuntimeException(promotionCodeCheckResultDTO.message());
            }
            totalPrice = PromotionUtils.calculateDiscountedPrice(subtotalPrice, promotionCodeCheckResultDTO.promotion());
        }

        DeliveryDTO deliveryInformation = deliveryService.calculateDelivery(deliveryRequest);
        totalPrice += deliveryInformation.fee();

        return CartDetailResponse.from(cartDTOs, promotionCodeCheckResultDTO, deliveryInformation, totalPrice);
    }

    public CartDetailResponse getCartDetailByUser(String phone, String promotionCode, DeliveryRequest deliveryRequest) {
        return getCartResponse(phone, promotionCode, deliveryRequest);
    }

    public CartResponse updateCart(String userPhone, CartUpdateRequest cartUpdateRequest) {
        User user = userService.findUserOrThrow(userPhone);
        Product product = productService.findProductOrThrow(cartUpdateRequest.getProductId());
        Cart cart = findCartOrThrow(user, product);

        updateNewProductQuantityOrThrow(cart, cartUpdateRequest.getQuantity());

        Cart updatedCart = cartRepository.save(cart);
        return new CartResponse(CartDTO.from(updatedCart));
    }

    public CartUpdateResponse deleteProductFromCart(String phone, Long productId) {
        User user = userService.findUserOrThrow(phone);
        Product product = productService.findProductOrThrow(productId);
        Cart cart = findCartOrThrow(user, product);

        cartRepository.delete(cart);
        return new CartUpdateResponse("Xóa sản phẩm khỏi giỏ hàng thành công: " + product);
    }

    public void deleteAllProductFromCart(String phone) {
        User user = userService.findUserOrThrow(phone);
        cartRepository.deleteAllByUser(user);
    }
}