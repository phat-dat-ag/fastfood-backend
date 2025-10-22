package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.AddressDTO;
import com.example.fastfoodshop.entity.Address;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AddressResponse {
    private ArrayList<AddressDTO> addresses = new ArrayList<>();

    public AddressResponse(List<Address> addressList) {
        for (Address address : addressList) {
            this.addresses.add(new AddressDTO(address));
        }
    }
}