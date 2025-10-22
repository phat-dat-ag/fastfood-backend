package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.AddressDTO;
import com.example.fastfoodshop.entity.Address;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.repository.AddressRepository;
import com.example.fastfoodshop.request.AddressCreateRequest;
import com.example.fastfoodshop.response.AddressResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserService userService;

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

    public ResponseEntity<ResponseWrapper<AddressDTO>> createAddress(String phone, AddressCreateRequest request) {
        try {
            User user = userService.findUserOrThrow(phone);

            Address address = new Address();
            address.setUser(user);
            buildAddress(address, request);

            Address savedAddress = addressRepository.save(address);
            return ResponseEntity.ok(ResponseWrapper.success(new AddressDTO(savedAddress)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "CREATE_ADDRESS_FAILED",
                            "Lỗi khi tạo địa chỉ mới " + e.getMessage()
                    )
            );
        }
    }

    public ResponseEntity<ResponseWrapper<AddressResponse>> getAddressesByUser(String phone) {
        try {
            User user = userService.findUserOrThrow(phone);
            List<Address> addresses = addressRepository.findByUserAndIsDeletedFalse(user);

            return ResponseEntity.ok(ResponseWrapper.success(new AddressResponse(addresses)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "GET_ADDRESSES_FAILED",
                            "Lỗi khi lấy địa chỉ của người dùng " + e.getMessage()
                    )
            );
        }
    }

    public ResponseEntity<ResponseWrapper<AddressDTO>> deleteAddress(String phone, Long id) {
        try {
            User user = userService.findUserOrThrow(phone);
            Optional<Address> optionalAddress = addressRepository.findByUserAndId(user, id);
            if (optionalAddress.isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseWrapper.error(
                                "INVALID_ADDRESSES_FAILED",
                                "Địa chỉ không tồn tại"
                        )
                );
            }
            Address address = optionalAddress.get();
            address.setDeleted(true);
            Address deletedAddress = addressRepository.save(address);
            return ResponseEntity.ok(ResponseWrapper.success(new AddressDTO(deletedAddress)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.error(
                            "GET_ADDRESSES_FAILED",
                            "Lỗi khi xóa địa chỉ của người dùng " + e.getMessage()
                    )
            );
        }
    }
}