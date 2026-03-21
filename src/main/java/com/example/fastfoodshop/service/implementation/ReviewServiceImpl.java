package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.ReviewDTO;
import com.example.fastfoodshop.entity.Review;
import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.OrderDetail;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.ReviewImage;
import com.example.fastfoodshop.exception.review.OrderAlreadyReviewedException;
import com.example.fastfoodshop.exception.review.DuplicateReviewProductException;
import com.example.fastfoodshop.exception.review.IncompleteReviewException;
import com.example.fastfoodshop.exception.review.OrderNotDeliveredException;
import com.example.fastfoodshop.exception.review.ProductNotInOrderException;
import com.example.fastfoodshop.exception.review.ReviewExpiredException;
import com.example.fastfoodshop.exception.review.ReviewNotFoundException;
import com.example.fastfoodshop.repository.ReviewRepository;
import com.example.fastfoodshop.request.ReviewCreateRequest;
import com.example.fastfoodshop.response.review.ReviewPageResponse;
import com.example.fastfoodshop.response.review.ReviewProductsResponse;
import com.example.fastfoodshop.response.review.ReviewUpdateResponse;
import com.example.fastfoodshop.service.OrderService;
import com.example.fastfoodshop.service.ProductService;
import com.example.fastfoodshop.service.ReviewImageService;
import com.example.fastfoodshop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderService orderService;
    private final ProductService productService;
    private final ReviewImageService reviewImageService;

    private Review findUndeletedReviewOrThrow(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(
                () -> new ReviewNotFoundException(reviewId)
        );
    }

    @Transactional
    public ReviewUpdateResponse createReviews(List<ReviewCreateRequest> reviewRequests, Long orderId) {
        Order order = orderService.findOrderOrThrow(orderId);
        if (order.getDeliveredAt() == null) {
            throw new OrderNotDeliveredException(orderId);
        }
        LocalDateTime now = LocalDateTime.now();
        if (order.getDeliveredAt().plusDays(2).isBefore(now)) {
            throw new ReviewExpiredException(order.getId());
        }
        if (!order.getReviews().isEmpty()) {
            throw new OrderAlreadyReviewedException(order.getId());
        }
        Set<Long> orderProductIds = new HashSet<>();
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            orderProductIds.add(orderDetail.getProduct().getId());
        }
        Set<Long> reviewedProductIds = new HashSet<>();

        for (ReviewCreateRequest reviewRequest : reviewRequests) {
            if (!orderProductIds.contains(reviewRequest.productId())) {
                throw new ProductNotInOrderException();
            }
            if (!reviewedProductIds.add(reviewRequest.productId())) {
                throw new DuplicateReviewProductException();
            }

            Product product = productService.findProductOrThrow(reviewRequest.productId());

            Review review = new Review();
            review.setOrder(order);
            review.setProduct(product);
            review.setRating(reviewRequest.rating());
            review.setComment(reviewRequest.comment());

            Review savedReview = reviewRepository.save(review);

            List<ReviewImage> reviewImages = reviewImageService.createReviewImages(reviewRequest.images(), savedReview);
            savedReview.setReviewImages(reviewImages);

            reviewRepository.save(savedReview);
        }
        if (reviewedProductIds.size() != order.getOrderDetails().size()) {
            throw new IncompleteReviewException();
        }
        return new ReviewUpdateResponse("Đánh giá sản đơn hàng thành công");
    }

    public ReviewProductsResponse getAllReviewsByProduct(Long productId) {
        Product product = productService.findProductOrThrow(productId);
        List<Review> reviews = reviewRepository.findByProductAndIsDeletedFalse(product);

        ArrayList<ReviewDTO> reviewDTOs = new ArrayList<>();
        for (Review review : reviews) {
            reviewDTOs.add(ReviewDTO.from(review));
        }

        return new ReviewProductsResponse(reviewDTOs);
    }

    public ReviewPageResponse getAllReviewsByAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviewPage = reviewRepository.findByIsDeletedFalse(pageable);
        return ReviewPageResponse.from(reviewPage);
    }

    public ReviewUpdateResponse deleteReview(Long reviewId) {
        Review review = findUndeletedReviewOrThrow(reviewId);
        review.setDeleted(true);
        reviewRepository.save(review);
        return new ReviewUpdateResponse("Đã xóa đánh giá: " + reviewId);
    }
}
