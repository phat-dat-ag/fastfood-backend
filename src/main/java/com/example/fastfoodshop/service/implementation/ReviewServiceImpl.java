package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.ReviewDTO;
import com.example.fastfoodshop.entity.Review;
import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.entity.ReviewImage;
import com.example.fastfoodshop.entity.base.BaseCreatedEntity;
import com.example.fastfoodshop.exception.review.OrderAlreadyReviewedException;
import com.example.fastfoodshop.exception.review.DuplicateReviewProductException;
import com.example.fastfoodshop.exception.review.IncompleteReviewException;
import com.example.fastfoodshop.exception.review.OrderNotDeliveredException;
import com.example.fastfoodshop.exception.review.ProductNotInOrderException;
import com.example.fastfoodshop.exception.review.ReviewExpiredException;
import com.example.fastfoodshop.exception.review.ReviewNotFoundException;
import com.example.fastfoodshop.exception.review.DeletedReviewException;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderService orderService;
    private final ProductService productService;
    private final ReviewImageService reviewImageService;

    private void validateOrderDelivered(Order order) {
        if (order.getDeliveredAt() == null) {
            throw new OrderNotDeliveredException(order.getId());
        }
    }

    private void validateReviewNotExpired(Order order) {
        if (order.getDeliveredAt().plusDays(2).isBefore(LocalDateTime.now())) {
            throw new ReviewExpiredException(order.getId());
        }
    }

    private void validateOrderNotReviewed(Order order) {
        if (!order.getReviews().isEmpty()) {
            throw new OrderAlreadyReviewedException(order.getId());
        }
    }

    private Order getValidOrderForReview(Long orderId) {
        Order order = orderService.findOrderOrThrow(orderId);

        validateOrderDelivered(order);
        validateReviewNotExpired(order);
        validateOrderNotReviewed(order);

        return order;
    }

    private Set<Long> extractOrderProductIds(Order order) {
        return order.getOrderDetails()
                .stream()
                .map(od -> od.getProduct().getId())
                .collect(Collectors.toSet());
    }

    private void validateReviewRequest(
            ReviewCreateRequest reviewCreateRequest,
            Set<Long> orderProductIds,
            Set<Long> reviewedProductIds
    ) {
        if (!orderProductIds.contains(reviewCreateRequest.productId())) {
            throw new ProductNotInOrderException();
        }

        if (!reviewedProductIds.add(reviewCreateRequest.productId())) {
            throw new DuplicateReviewProductException();
        }
    }

    private Review mapToReview(ReviewCreateRequest reviewCreateRequest, Order order, Product product) {
        Review review = new Review();

        review.setOrder(order);
        review.setProduct(product);
        review.setRating(reviewCreateRequest.rating());
        review.setComment(reviewCreateRequest.comment());

        return review;
    }

    private void attachReviewImages(ReviewCreateRequest reviewCreateRequest, Review review) {
        List<ReviewImage> images =
                reviewImageService.createReviewImages(reviewCreateRequest.images(), review);

        review.setReviewImages(images);
    }

    private Map<Long, Product> getProductMap(List<ReviewCreateRequest> reviewCreateRequests) {
        List<Long> productIds = reviewCreateRequests.stream()
                .map(ReviewCreateRequest::productId)
                .toList();

        return productService.findAllByIds(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
    }

    private void createReview(
            ReviewCreateRequest reviewCreateRequest, Order order, Map<Long, Product> productMap
    ) {
        Product product = productMap.get(reviewCreateRequest.productId());

        Review review = mapToReview(reviewCreateRequest, order, product);

        Review savedReview = reviewRepository.save(review);

        attachReviewImages(reviewCreateRequest, savedReview);
    }

    private void validateAllProductsReviewed(
            Set<Long> orderProductIds, Set<Long> reviewedProductIds
    ) {
        if (!reviewedProductIds.containsAll(orderProductIds)) {
            throw new IncompleteReviewException();
        }
    }

    @Transactional
    public ReviewUpdateResponse createReviews(List<ReviewCreateRequest> reviewCreateRequests, Long orderId) {
        Order order = getValidOrderForReview(orderId);

        Set<Long> orderProductIds = extractOrderProductIds(order);

        Map<Long, Product> productMap = getProductMap(reviewCreateRequests);

        Set<Long> reviewedProductIds = new HashSet<>();

        for (ReviewCreateRequest reviewCreateRequest : reviewCreateRequests) {
            validateReviewRequest(reviewCreateRequest, orderProductIds, reviewedProductIds);
            createReview(reviewCreateRequest, order, productMap);
        }

        validateAllProductsReviewed(orderProductIds, reviewedProductIds);

        return new ReviewUpdateResponse("Đánh giá đơn hàng thành công");
    }

    public ReviewProductsResponse getAllReviewsByProduct(Long productId) {
        Product product = productService.findProductOrThrow(productId);

        List<ReviewDTO> reviewDTOs = reviewRepository
                .findByProductAndIsDeletedFalse(product)
                .stream()
                .map(ReviewDTO::from)
                .toList();

        return new ReviewProductsResponse(reviewDTOs);
    }

    public ReviewPageResponse getAllReviewsByAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by(BaseCreatedEntity.Field.createdAt).descending()
        );

        Page<Review> reviewPage = reviewRepository.findByIsDeletedFalse(pageable);

        return ReviewPageResponse.from(reviewPage);
    }

    private Review findReviewOrThrow(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(
                () -> new ReviewNotFoundException(reviewId)
        );
    }

    public ReviewUpdateResponse deleteReview(Long reviewId) {
        Review review = findReviewOrThrow(reviewId);
        if (review.isDeleted()) {
            throw new DeletedReviewException();
        }

        review.setDeleted(true);
        reviewRepository.save(review);
        return new ReviewUpdateResponse("Đã xóa đánh giá: " + reviewId);
    }
}
