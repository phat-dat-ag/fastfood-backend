package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.Address;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class AddressDTO {
    private Long id;
    private String name;
    private String detail;
    private Double latitude;
    private Double longitude;
    private String street;
    private String ward;
    private String district;
    private String province;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AddressDTO(Address address) {
        this.id = address.getId();
        ;
        this.latitude = address.getLatitude();
        this.longitude = address.getLongitude();
        this.name = address.getName();
        this.detail = address.getDetail();
        this.street = address.getStreet();
        this.ward = address.getWard();
        this.district = address.getDistrict();
        this.province = address.getProvince();
        this.createdAt = address.getCreatedAt().
                atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        ;
        this.updatedAt = address.getUpdatedAt().
                atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        ;
    }
}