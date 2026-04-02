package com.example.fastfoodshop.factory.address;

import com.example.fastfoodshop.entity.Address;
import com.example.fastfoodshop.entity.User;

import java.time.Instant;

public class AddressFactory {
    public static Address createValidAddress(User validUser) {
        Address address = new Address();

        address.setId(99L);
        address.setName("Nhà riêng");
        address.setDetail("Gần công viên");

        address.setLatitude(10.762622);
        address.setLongitude(106.660172);

        address.setStreet("Lưu Hữu Phước");
        address.setWard("Xuân Khánh");
        address.setDistrict("Ninh Kiều");
        address.setProvince("Cần Thơ");

        address.setDeleted(false);

        address.setCreatedAt(Instant.now());
        address.setUpdatedAt(Instant.now());

        address.setUser(validUser);

        return address;
    }
}
