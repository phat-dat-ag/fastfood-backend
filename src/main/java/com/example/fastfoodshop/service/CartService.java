package com.example.fastfoodshop.service;

import com.example.fastfoodshop.constant.CartConstant;
import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.dto.DeliveryDTO;
import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.dto.PromotionCodeCheckResultDTO;
import com.example.fastfoodshop.entity.Cart;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.repository.CartRepository;
import com.example.fastfoodshop.request.DeliveryRequest;
import com.example.fastfoodshop.response.CartResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.util.PromotionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final UserService userService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final CartRepository cartRepository;
    private final PromotionService promotionService;
    private final DeliveryService deliveryService;

    private Cart findCartOrThrow(User user, Product product) {
        return cartRepository.findByUserAndProduct(user, product).orElseThrow(
                () -> new RuntimeException("Không thấy sản phẩm này trong giỏ hàng của người dùng")
        );
    }

    private void setNewProductQuantityOrThrow(Cart cart, int quantity) {
        int newQuantity = cart.getQuantity() + quantity;
        if (newQuantity > CartConstant.MAX_QUANTITY_PER_PRODUCT) {
            throw new RuntimeException("Số lượng tối đa mỗi sản phẩm là " + CartConstant.MAX_QUANTITY_PER_PRODUCT);
        }
        cart.setQuantity(newQuantity);
    }

    public ResponseEntity<ResponseWrapper<CartDTO>> addProductToCart(String userPhone, Long productId, int quantity) {
        try {
            User user = userService.findUserOrThrow(userPhone);
            Product product = productService.findProductOrThrow(productId);
            List<Cart> carts = cartRepository.findByUser(user);
            Optional<Cart> optionalCart = cartRepository.findByUserAndProduct(user, product);

            if (optionalCart.isEmpty()) {
                if (carts.size() >= CartConstant.MAX_PRODUCT_TYPES_PER_CART) {
                    return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "ADD_PRODUCT_TO_CART_FAILED",
                            "Số loại sản phẩm trong giỏ hàng tối đa là " + CartConstant.MAX_PRODUCT_TYPES_PER_CART
                    ));
                }
            }

            Cart cart = optionalCart.orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUser(user);
                newCart.setProduct(product);
                newCart.setQuantity(0);
                return newCart;
            });

            setNewProductQuantityOrThrow(cart, quantity);

            Cart savedCart = cartRepository.save(cart);
            return ResponseEntity.ok(ResponseWrapper.success(new CartDTO(savedCart)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "ADD_PRODUCT_TO_CART_FAILED",
                            "Lỗi thêm sản phẩm vào giỏ hàng " + e.getMessage()
                    )
            );
        }
    }

    public CartResponse getCartResponse(String phone, String promotionCode, DeliveryRequest deliveryRequest) {
        User user = userService.findUserOrThrow(phone);
        List<Cart> carts = cartRepository.findByUser(user);
        ArrayList<CartDTO> cartDTOs = new ArrayList<>();
        for (Cart cart : carts) {
            Category category = cart.getProduct().getCategory();
            ProductDTO productDTO = new ProductDTO(cart.getProduct());
            categoryService.applyPromotion(productDTO, category);
            CartDTO cartDTO = new CartDTO(cart);
            cartDTO.setProduct(productDTO);
            cartDTOs.add(cartDTO);
        }
        CartResponse cartResponse = new CartResponse(cartDTOs);

        if (promotionCode != null && !promotionCode.isEmpty()) {
            PromotionCodeCheckResultDTO result = promotionService.checkPromotionCode(promotionCode, cartResponse.getSubtotalPrice());
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

    public ResponseEntity<ResponseWrapper<CartResponse>> getCartDetailByUser(String phone, String promotionCode, DeliveryRequest deliveryRequest) {
        try {
            CartResponse cartResponse = getCartResponse(phone, promotionCode, deliveryRequest);
            return ResponseEntity.ok(ResponseWrapper.success(cartResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "GET_CART_DETAIL_FAILED",
                            "Lỗi khi lấy giỏ hàng của người dùng " + e.getMessage()
                    )
            );
        }
    }

    public ResponseEntity<ResponseWrapper<CartDTO>> increaseProductQuantityInCart(String userPhone, Long productId, int quantity) {
        try {
            User user = userService.findUserOrThrow(userPhone);
            Product product = productService.findProductOrThrow(productId);
            Cart cart = findCartOrThrow(user, product);

            setNewProductQuantityOrThrow(cart, quantity);

            Cart updatedCart = cartRepository.save(cart);
            return ResponseEntity.ok(ResponseWrapper.success(new CartDTO(updatedCart)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "UPDATE_CART_FAILED",
                            "Lỗi tăng số lượng cho sản phẩm trong giỏ hàng " + e.getMessage()
                    )
            );
        }
    }

    public ResponseEntity<ResponseWrapper<CartDTO>> deleteProductFromCart(String phone, Long productId) {
        try {
            User user = userService.findUserOrThrow(phone);
            Product product = productService.findProductOrThrow(productId);
            Cart cart = findCartOrThrow(user, product);

            cartRepository.delete(cart);
            return ResponseEntity.ok(ResponseWrapper.success(new CartDTO(cart)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "DELETE_PRODUCT_FROM_CART_FAILED",
                            "Có lỗi khi xóa sản phẩm khỏi giỏ hàng " + e.getMessage()
                    )
            );
        }
    }

    public void deleteAllProductFromCart(String phone) {
        User user = userService.findUserOrThrow(phone);
        cartRepository.deleteAllByUser(user);
    }
}
