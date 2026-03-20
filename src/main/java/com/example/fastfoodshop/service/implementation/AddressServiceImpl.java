package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.AddressDTO;
import com.example.fastfoodshop.entity.Address;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.exception.address.AddressNotFoundException;
import com.example.fastfoodshop.repository.AddressRepository;
import com.example.fastfoodshop.request.AddressCreateRequest;
import com.example.fastfoodshop.response.address.AddressResponse;
import com.example.fastfoodshop.response.address.AddressesResponse;
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
        address.setName(request.name());
        address.setLatitude(request.latitude());
        address.setLongitude(request.longitude());
        address.setDetail(request.detail());
        address.setStreet(request.street());
        address.setWard(request.ward());
        address.setDistrict(request.district());
        address.setProvince(request.province());
        address.setDeleted(false);
    }

    public AddressResponse createAddress(String phone, AddressCreateRequest request) {
        User user = userService.findUserOrThrow(phone);

        Address address = new Address();
        address.setUser(user);
        buildAddress(address, request);

        Address savedAddress = addressRepository.save(address);
        return new AddressResponse(AddressDTO.from(savedAddress));
    }

    public AddressesResponse getAddressesByUser(String phone) {
        User user = userService.findUserOrThrow(phone);

        List<AddressDTO> addressDTOs = addressRepository
                .findByUserAndIsDeletedFalse(user)
                .stream()
                .map(AddressDTO::from)
                .toList();

        return new AddressesResponse(addressDTOs);
    }

    public AddressResponse deleteAddress(String phone, Long id) {
        User user = userService.findUserOrThrow(phone);

        Optional<Address> optionalAddress = addressRepository.findByUserAndId(user, id);
        if (optionalAddress.isEmpty()) {
            throw new AddressNotFoundException();
        }
        Address address = optionalAddress.get();
        address.setDeleted(true);
        Address deletedAddress = addressRepository.save(address);

        return new AddressResponse(AddressDTO.from(deletedAddress));
    }
}
