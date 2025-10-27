package com.example.fastfoodshop.service;

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
    private final OrderService orderService;
    private final OrderDetailRepository orderDetailRepository;

    public void createOrderDetail(Long product_id, Long order_id, int quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("Không thể tạo chi tiết hóa đơn với số lượng sản phẩm không hợp lệ");
        }
        Order order = orderService.findOrderOrThrow(order_id);
        Product product = productService.findProductOrThrow(product_id);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        orderDetail.setQuantity(quantity);

        orderDetailRepository.save(orderDetail);
    }
}
