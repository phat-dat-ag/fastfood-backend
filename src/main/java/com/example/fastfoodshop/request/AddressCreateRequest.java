package com.example.fastfoodshop.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressCreateRequest {
    @NotBlank(message = "Tên địa chỉ không được để trống")
    @Size(min = 3, max = 40, message = "Tên địa chỉ từ 3 đến 40 ký tự")
    private String name;

    @Size(max = 100, message = "Nội dung ghi chú không quá 100 ký tự")
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