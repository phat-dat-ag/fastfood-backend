package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.AddressDTO;
import com.example.fastfoodshop.entity.Address;
import com.example.fastfoodshop.request.AddressCreateRequest;
import com.example.fastfoodshop.response.AddressResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import org.springframework.http.ResponseEntity;

public interface AddressService {
    Address findAddressOrThrow(Long id);

    ResponseEntity<ResponseWrapper<AddressDTO>> createAddress(String phone, AddressCreateRequest request);

    ResponseEntity<ResponseWrapper<AddressResponse>> getAddressesByUser(String phone);

    ResponseEntity<ResponseWrapper<AddressDTO>> deleteAddress(String phone, Long id);
}