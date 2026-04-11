package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.CartDTO;
import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.OrderDetail;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.repository.OrderDetailRepository;
import com.example.fastfoodshop.service.OrderDetailService;
import com.example.fastfoodshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final ProductService productService;
    private final OrderDetailRepository orderDetailRepository;

    public void createOrderDetail(CartDTO cartDTO, Order order) {
        Product product = productService.findProductOrThrow(cartDTO.product().id());

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        orderDetail.setQuantity(cartDTO.quantity());
        orderDetail.setDiscountedPrice(cartDTO.product().discountedPrice());

        log.info(
                "[OrderDetailService] Successfully created order detail for order id={}, product id={}",
                order.getId(), product.getId()
        );

        orderDetailRepository.save(orderDetail);
    }
}
