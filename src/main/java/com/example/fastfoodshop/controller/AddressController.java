package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.AddressCreateRequest;
import com.example.fastfoodshop.response.address.AddressResponse;
import com.example.fastfoodshop.response.address.AddressesResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.AddressService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController extends BaseController {
    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<AddressResponse>> createAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid AddressCreateRequest addressCreateRequest
    ) {
        return okResponse(addressService.createAddress(userDetails.getUsername(), addressCreateRequest));
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<AddressesResponse>> getAddresses(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return okResponse(addressService.getAddressesByUser(userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<AddressResponse>> deleteAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long addressId
    ) {
        return okResponse(addressService.deleteAddress(userDetails.getUsername(), addressId));
    }
}