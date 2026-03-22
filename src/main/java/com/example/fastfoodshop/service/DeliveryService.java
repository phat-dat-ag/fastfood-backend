package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.DeliveryDTO;

public interface DeliveryService {
    DeliveryDTO calculateDelivery(Long addressId);
}
