package com.example.fastfoodshop.factory.address;

import com.example.fastfoodshop.request.AddressCreateRequest;

public class AddressCreateRequestFactory {
    public static AddressCreateRequest createValid() {
        return new AddressCreateRequest(
                "Nhà riêng",
                "Gần Đại học Cần Thơ",
                10.0,
                106.0,
                "14/12 Trần Hoàng Na",
                "Xuân Khánh",
                "Ninh Kiều",
                "Cần Thơ"
        );
    }
}
