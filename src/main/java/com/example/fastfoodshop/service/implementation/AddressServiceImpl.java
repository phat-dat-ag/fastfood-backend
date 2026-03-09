package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.AddressDTO;
import com.example.fastfoodshop.entity.Address;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.exception.address.AddressNotFoundException;
import com.example.fastfoodshop.repository.AddressRepository;
import com.example.fastfoodshop.request.AddressCreateRequest;
import com.example.fastfoodshop.response.AddressResponse;
import com.example.fastfoodshop.service.AddressService;
import com.example.fastfoodshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserService userService;

    public Address findAddressOrThrow(Long id) {
        return addressRepository.findById(id).orElseThrow(AddressNotFoundException::new);
    }

    private void buildAddress(Address address, AddressCreateRequest request) {
        address.setName(request.getName());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setDetail(request.getDetail());
        address.setStreet(request.getStreet());
        address.setWard(request.getWard());
        address.setDistrict(request.getDistrict());
        address.setProvince(request.getProvince());
        address.setDeleted(false);
    }

    public AddressDTO createAddress(String phone, AddressCreateRequest request) {
        User user = userService.findUserOrThrow(phone);

        Address address = new Address();
        address.setUser(user);
        buildAddress(address, request);

        Address savedAddress = addressRepository.save(address);
        return new AddressDTO(savedAddress);
    }

    public AddressResponse getAddressesByUser(String phone) {
        User user = userService.findUserOrThrow(phone);
        List<Address> addresses = addressRepository.findByUserAndIsDeletedFalse(user);

        return new AddressResponse(addresses);
    }

    public AddressDTO deleteAddress(String phone, Long id) {
        User user = userService.findUserOrThrow(phone);
        Optional<Address> optionalAddress = addressRepository.findByUserAndId(user, id);
        if (optionalAddress.isEmpty()) {
            throw new AddressNotFoundException();
        }
        Address address = optionalAddress.get();
        address.setDeleted(true);
        Address deletedAddress = addressRepository.save(address);
        return new AddressDTO(deletedAddress);
    }
}
