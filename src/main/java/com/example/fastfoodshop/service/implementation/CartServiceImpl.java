package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.constant.CartConstant;
import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.dto.UserDTO;
import com.example.fastfoodshop.dto.PromotionDTO;
import com.example.fastfoodshop.dto.DeliveryDTO;
import com.example.fastfoodshop.entity.Cart;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.entity.Promotion;
import com.example.fastfoodshop.exception.cart.CartNotFoundException;
import com.example.fastfoodshop.exception.cart.ProductAmountExceededException;
import com.example.fastfoodshop.exception.cart.QuantityExceededException;
import com.example.fastfoodshop.repository.CartRepository;
import com.example.fastfoodshop.request.CartCreateRequest;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
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

        log.debug(
                "[CartService] Successfully set quantity for product id={} to {}",
                cart.getProduct().getId(), newQuantity
        );
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

        log.info(
                "[CartService] Successfully created cart for user phone={}: product id={}, quantity={}",
                userPhone, product.getId(), cartCreateRequest.quantity()
        );

        return new CartResponse(CartDTO.from(savedCart));
    }

    private List<CartDTO> buildCartDTOs(List<Cart> carts) {
        List<CartDTO> cartDTOs = new ArrayList<>();

        for (Cart cart : carts) {
            Category category = cart.getProduct().getCategory();
            Product product = cart.getProduct();

            categoryService.applyPromotion(product, category);

            cartDTOs.add(new CartDTO(
                    UserDTO.from(cart.getUser()),
                    ProductDTO.from(product),
                    cart.getQuantity()
            ));
        }

        return cartDTOs;
    }

    private int calculateSubtotalPrice(List<CartDTO> cartDTOs) {
        int subtotalPrice = cartDTOs.stream()
                .mapToInt(
                        cartDTO -> cartDTO.quantity() * cartDTO.product().discountedPrice()
                )
                .sum();

        log.debug("[CartService] Calculated subtotal price: {}", subtotalPrice);

        return subtotalPrice;
    }

    private Promotion applyPromotion(String promotionCode, int subtotalPrice) {
        if (promotionCode == null) return null;

        if (promotionCode.isBlank()) return null;

        return promotionService.checkPromotionCode(promotionCode, subtotalPrice);
    }

    private int calculateTotalPrice(
            int subtotalPrice,
            Promotion promotion,
            DeliveryDTO delivery
    ) {
        int totalPrice = subtotalPrice;

        if (promotion != null) {
            totalPrice = PromotionUtils.calculateDiscountedPrice(subtotalPrice, PromotionDTO.from(promotion));
        }

        int total = totalPrice + delivery.fee();

        log.debug("[CartService] Calculated total price: {}", total);

        return total;
    }

    public CartDetailResponse getCartDetailByUser(String phone, String promotionCode, Long addressId) {
        User user = userService.findUserOrThrow(phone);
        List<Cart> carts = cartRepository.findByUser(user);

        List<CartDTO> cartDTOs = buildCartDTOs(carts);

        int subtotalPrice = calculateSubtotalPrice(cartDTOs);

        Promotion promotion = applyPromotion(promotionCode, subtotalPrice);

        DeliveryDTO deliveryInformation = deliveryService.calculateDelivery(addressId);

        int totalPrice = calculateTotalPrice(subtotalPrice, promotion, deliveryInformation);

        PromotionDTO promotionDTO = promotion == null ? null : PromotionDTO.from(promotion);

        log.info(
                "[CartService] Successfully got cart detail, items={}, totalPrice={}, "
                        + "had delivery information={}, applied promotion: {}",
                cartDTOs.size(), totalPrice, deliveryInformation.success(), promotionDTO != null
        );

        return CartDetailResponse.from(cartDTOs, promotionDTO, deliveryInformation, totalPrice);
    }

    private void updateNewProductQuantityOrThrow(Cart cart, int newQuantity) {
        if (newQuantity > CartConstant.MAX_QUANTITY_PER_PRODUCT) {
            throw new QuantityExceededException();
        }
        cart.setQuantity(newQuantity);

        log.debug(
                "[CartService] Set new quantity for product id={} to {}",
                cart.getProduct().getId(), newQuantity
        );
    }

    public CartResponse updateCartItem(String userPhone, Long productId, int quantity) {
        User user = userService.findUserOrThrow(userPhone);
        Product product = productService.findProductOrThrow(productId);
        Cart cart = findCartOrThrow(user, product);

        updateNewProductQuantityOrThrow(cart, quantity);

        Cart updatedCart = cartRepository.save(cart);

        log.info(
                "[CartService] Successfully updated cart for user phone={}, product id={}, quantity={}",
                userPhone, productId, quantity
        );

        return new CartResponse(CartDTO.from(updatedCart));
    }

    public CartUpdateResponse deleteProductFromCart(String phone, Long productId) {
        User user = userService.findUserOrThrow(phone);
        Product product = productService.findProductOrThrow(productId);
        Cart cart = findCartOrThrow(user, product);

        cartRepository.delete(cart);

        log.info(
                "[CartService] Successfully deleted product id={} from cart of user phone={}",
                productId, phone
        );

        return new CartUpdateResponse("Xóa sản phẩm khỏi giỏ hàng thành công: " + product);
    }

    public void deleteAllProductFromCart(String phone) {
        User user = userService.findUserOrThrow(phone);
        cartRepository.deleteAllByUser(user);

        log.info("[CartService] Successfully deleted all cart for user phone={}", phone);
    }
}