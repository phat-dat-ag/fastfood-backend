package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.entity.Cart;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.repository.CartRepository;
import com.example.fastfoodshop.response.CartResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
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

    public ResponseEntity<ResponseWrapper<CartDTO>> addProductToCart(String userPhone, Long productId, int quantity) {
        try {
            User user = userService.findUserOrThrow(userPhone);
            Product product = productService.findProductOrThrow(productId);

            Optional<Cart> optionalCart = cartRepository.findByUser_IdAndProduct_Id(user.getId(), product.getId());

            Cart cart = optionalCart.orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUser(user);
                newCart.setProduct(product);
                newCart.setQuantity(0);
                return newCart;
            });
            cart.setQuantity(cart.getQuantity() + quantity);

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

    public ResponseEntity<ResponseWrapper<CartResponse>> getCartDetailByUser(String phone) {
        try {
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
            CartResponse cartResponses = new CartResponse(cartDTOs);
            return ResponseEntity.ok(ResponseWrapper.success(cartResponses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "GET_CART_DETAIL_FAILED",
                            "Lỗi khi lấy giỏ hàng của người dùng " + e.getMessage()
                    )
            );
        }
    }

    public ResponseEntity<ResponseWrapper<CartDTO>> updateCart(String userPhone, Long productId, int quantity) {
        try {
            User user = userService.findUserOrThrow(userPhone);
            Cart cart = cartRepository.findByUserAndProduct_Id(user, productId);
            cart.setQuantity(quantity);

            Cart updatedCart = cartRepository.save(cart);
            return ResponseEntity.ok(ResponseWrapper.success(new CartDTO(updatedCart)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "UPDATE_cART_FAILED",
                            "Lỗi cập nhật giỏ hàng"
                    )
            );
        }
    }

    public ResponseEntity<ResponseWrapper<CartDTO>> deleteProductFromCart(String phone, Long productId) {
        try {
            User user = userService.findUserOrThrow(phone);
            Cart cart = cartRepository.findByUserAndProduct_Id(user, productId);

            cartRepository.delete(cart);
            return ResponseEntity.ok(ResponseWrapper.success(new CartDTO(cart)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "DELETE_PRODUCT_FROM_CART_FAILED",
                            "Có lỗi khi xóa sản phẩm khỏi giỏ hàng"
                    )
            );
        }
    }
}
