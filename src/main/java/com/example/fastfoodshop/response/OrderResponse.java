package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.OrderDTO;
import com.example.fastfoodshop.entity.Order;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderResponse {
    private ArrayList<OrderDTO> orders = new ArrayList<>();

    public OrderResponse(List<Order> orderList) {
        for (Order order : orderList) {
            this.orders.add(new OrderDTO(order));
        }
    }
}
