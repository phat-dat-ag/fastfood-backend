package com.example.fastfoodshop.response.order;

import com.example.fastfoodshop.dto.OrderDTO;
import com.example.fastfoodshop.entity.Order;
import org.springframework.data.domain.Page;

import java.util.List;

public record OrderPageResponse(
        List<OrderDTO> orders,
        int currentPage,
        int pageSize,
        long totalItems,
        int totalPages
) {
    public static OrderPageResponse from(Page<Order> page) {
        List<OrderDTO> orders = page.getContent().stream().map(OrderDTO::from).toList();
        return new OrderPageResponse(
                orders,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
