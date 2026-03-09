package com.example.fastfoodshop.exception.address;

import com.example.fastfoodshop.exception.base.BusinessException;

public class AddressNotFoundException extends BusinessException {
    public AddressNotFoundException() {
        super("ADDRESS_NOT_FOUND", "Địa chỉ không tồn tại");
    }
}
