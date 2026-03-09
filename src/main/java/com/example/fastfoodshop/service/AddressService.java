package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.AddressDTO;
import com.example.fastfoodshop.entity.Address;
import com.example.fastfoodshop.request.AddressCreateRequest;
import com.example.fastfoodshop.response.AddressResponse;

public interface AddressService {
    Address findAddressOrThrow(Long id);

    AddressDTO createAddress(String phone, AddressCreateRequest request);

    AddressResponse getAddressesByUser(String phone);

    AddressDTO deleteAddress(String phone, Long id);
}