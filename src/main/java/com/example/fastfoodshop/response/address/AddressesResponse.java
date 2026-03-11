package com.example.fastfoodshop.response.address;

import com.example.fastfoodshop.dto.AddressDTO;

import java.util.List;

public record AddressesResponse(
        List<AddressDTO> addresses
) {
}