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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController extends BaseController {
    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<AddressDTO>> createAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AddressCreateRequest addressCreateRequest
    ) {
        return okResponse(addressService.createAddress(userDetails.getUsername(), addressCreateRequest));
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<AddressResponse>> getAddress(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return okResponse(addressService.getAddressesByUser(userDetails.getUsername()));
    }

    @DeleteMapping
    public ResponseEntity<ResponseWrapper<AddressDTO>> deleteAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("id") Long addressId
    ) {
        return okResponse(addressService.deleteAddress(userDetails.getUsername(), addressId));
    }
}