package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.OrderDTO;
import com.example.fastfoodshop.entity.Order;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderResponse {
    private List<OrderDTO> orders = new ArrayList<>();
    private int currentPage;
    private int pageSize;
    private long totalItems;
    private int totalPages;

    public OrderResponse(Page<Order> page) {
        for (Order order : page.getContent()) {
            this.orders.add(new OrderDTO(order));
        }
        this.currentPage = page.getNumber();
        this.pageSize = page.getSize();
        this.totalItems = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}
