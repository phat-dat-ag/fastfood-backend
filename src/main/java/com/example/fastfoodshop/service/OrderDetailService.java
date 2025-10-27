package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.OrderDetail;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.repository.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderDetailService {
    private final ProductService productService;
    private final OrderDetailRepository orderDetailRepository;

    public void createOrderDetail(CartDTO cartDTO, Order order) {
        Product product = productService.findProductOrThrow(cartDTO.getProduct().getId());

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        orderDetail.setQuantity(cartDTO.getQuantity());
        orderDetail.setDiscountedPrice(cartDTO.getProduct().getDiscountedPrice());

        orderDetailRepository.save(orderDetail);
    }
}
