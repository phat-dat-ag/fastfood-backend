package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.*;
import com.example.fastfoodshop.repository.ReviewRepository;
import com.example.fastfoodshop.request.ReviewCreateRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderService orderService;
    private final ProductService productService;
    private final ReviewImageService reviewImageService;

    @Transactional
    public ResponseEntity<ResponseWrapper<String>> createReviews(List<ReviewCreateRequest> reviewRequests, Long orderId) {
        try {
            Order order = orderService.findOrderOrThrow(orderId);
            if (order.getDeliveredAt() == null) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error(
                        "CREATE_REVIEW_FAILED",
                        "Không thể đánh giá cho đơn chưa giao thành công"
                ));
            }
            LocalDateTime now = LocalDateTime.now();
            if (order.getDeliveredAt().plusDays(2).isBefore(now)) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error(
                        "CREATE_REVIEW_FAILED",
                        "Đã quá hạn 2 ngày kể từ khi nhận hàng, không thể đánh giá"
                ));
            }
            if (!order.getReviews().isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error(
                        "CREATE_REVIEW_FAILED",
                        "Đơn hàng này đã được đánh giá từ trước"
                ));
            }
            Set<Long> orderProductIds = new HashSet<>();
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                orderProductIds.add(orderDetail.getProduct().getId());
            }
            Set<Long> reviewedProductIds = new HashSet<>();

            for (ReviewCreateRequest reviewRequest : reviewRequests) {
                if (!orderProductIds.contains(reviewRequest.getProductId())) {
                    return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "CREATE_REVIEW_FAILED",
                            "Sản phẩm được đánh giá không tồn tại trong đơn hàng"
                    ));
                }
                if (!reviewedProductIds.add(reviewRequest.getProductId())) {
                    return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "CREATE_REVIEW_FAILED",
                            "Sản phẩm đã được đánh giá trong đơn đơn hàng rồi"
                    ));
                }

                Product product = productService.findProductOrThrow(reviewRequest.getProductId());

                Review review = new Review();
                review.setOrder(order);
                review.setProduct(product);
                review.setRating(reviewRequest.getRating());
                review.setComment(reviewRequest.getComment());

                Review savedReview = reviewRepository.save(review);

                List<ReviewImage> reviewImages = reviewImageService.createReviewImages(reviewRequest.getImages(), savedReview);
                savedReview.setReviewImages(reviewImages);

                Review updatedReview = reviewRepository.save(savedReview);
            }
            if (reviewedProductIds.size() != order.getOrderDetails().size()) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error(
                        "CREATE_REVIEW_FAILED",
                        "Có sản phẩm chưa được đánh giá"
                ));
            }
            return ResponseEntity.ok(ResponseWrapper.success("Đã đánh giá"));
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                    "CREATE_REVIEW_FAILED",
                    "Lỗi tạo các đánh giá cho đơn hàng " + e.getMessage()
            ));
        }
    }
}
