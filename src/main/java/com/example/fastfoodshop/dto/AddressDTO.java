package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Address;

import java.time.LocalDateTime;
import java.time.ZoneId;

public record AddressDTO(
        Long id,
        String name,
        String detail,
        Double latitude,
        Double longitude,
        String street,
        String ward,
        String district,
        String province,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AddressDTO from(Address address) {
        return new AddressDTO(
                address.getId(),
                address.getName(),
                address.getDetail(),
                address.getLatitude(),
                address.getLongitude(),
                address.getStreet(),
                address.getWard(),
                address.getDistrict(),
                address.getProvince(),
                address.getCreatedAt().
                        atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                address.getUpdatedAt().
                        atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );
    }
}