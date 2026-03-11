package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Address;
import com.example.fastfoodshop.request.AddressCreateRequest;
import com.example.fastfoodshop.response.address.AddressResponse;
import com.example.fastfoodshop.response.address.AddressesResponse;

public interface AddressService {
    Address findAddressOrThrow(Long id);

    AddressResponse createAddress(String phone, AddressCreateRequest request);

    AddressesResponse getAddressesByUser(String phone);

    AddressResponse deleteAddress(String phone, Long id);
}