package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddressCreateRequest {
    @NotBlank(message = "Tên địa chỉ không được để trống")
    private String name;

    private String detail;

    @NotNull(message = "Vĩ độ không được để trống")
    private Double latitude;

    @NotNull(message = "Kinh độ không được để trống")
    private Double longitude;

    private String street;

    private String ward;

    private String district;

    private String province;
}