package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.dto.AddressDTO;
import com.example.fastfoodshop.request.AddressCreateRequest;
import com.example.fastfoodshop.response.AddressResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<AddressDTO>> createAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AddressCreateRequest request
    ) {
        return addressService.createAddress(userDetails.getUsername(), request);
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<AddressResponse>> getAddress(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return addressService.getAddressesByUser(userDetails.getUsername());
    }

    @DeleteMapping
    public ResponseEntity<ResponseWrapper<AddressDTO>> deleteAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("id") Long id
    ) {
        return addressService.deleteAddress(userDetails.getUsername(), id);
    }
}