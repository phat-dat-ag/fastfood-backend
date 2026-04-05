package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Address;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.exception.address.AddressNotFoundException;
import com.example.fastfoodshop.exception.user.UserNotFoundException;
import com.example.fastfoodshop.factory.address.AddressCreateRequestFactory;
import com.example.fastfoodshop.factory.address.AddressFactory;
import com.example.fastfoodshop.factory.user.UserFactory;
import com.example.fastfoodshop.repository.AddressRepository;
import com.example.fastfoodshop.request.AddressCreateRequest;
import com.example.fastfoodshop.response.address.AddressResponse;
import com.example.fastfoodshop.response.address.AddressesResponse;
import com.example.fastfoodshop.service.implementation.AddressServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class AddressServiceImplTest {
    @Mock
    AddressRepository addressRepository;

    @Mock
    UserService userService;

    @InjectMocks
    AddressServiceImpl addressService;

    private static final Long ADDRESS_ID = 999L;
    private static final String USER_PHONE = "0999998888";

    @Test
    void findAddressOrThrow_validAddressId_shouldReturnAddress() {
        User validUser = UserFactory.createActivatedUser();

        Address address = AddressFactory.createValidAddress(validUser);

        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));

        Address addressResponse = addressService.findAddressOrThrow(address.getId());

        assertNotNull(addressResponse);

        assertEquals(address.getId(), addressResponse.getId());
        assertEquals(validUser, addressResponse.getUser());

        verify(addressRepository).findById(address.getId());
    }

    @Test
    void findAddressOrThrow_notFoundAddressId_shouldThrowException() {
        when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.empty());

        assertThrows(
                AddressNotFoundException.class,
                () -> addressService.findAddressOrThrow(ADDRESS_ID)
        );

        verify(addressRepository).findById(ADDRESS_ID);
    }

    @Test
    void createAddress_validRequest_shouldReturnAddressResponse() {
        AddressCreateRequest validRequest = AddressCreateRequestFactory.createValid();

        User validUser = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(validUser.getPhone())).thenReturn(validUser);

        Address savedAddress = AddressFactory.createValidAddress(validUser);

        when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);

        AddressResponse addressResponse = addressService.createAddress(validUser.getPhone(), validRequest);

        assertNotNull(addressResponse);
        assertNotNull(addressResponse.address());
        assertEquals(savedAddress.getId(), addressResponse.address().id());

        verify(userService).findUserOrThrow(validUser.getPhone());
        verify(addressRepository).save(argThat(address ->
                address.getName().equals(validRequest.name())
                        && !address.isDeleted()
                        && address.getUser().equals(validUser)
        ));
    }

    @Test
    void createAddress_userNotFound_shouldThrowException() {
        AddressCreateRequest validRequest = AddressCreateRequestFactory.createValid();

        when(userService.findUserOrThrow(USER_PHONE)).thenThrow(new UserNotFoundException(USER_PHONE));

        assertThrows(
                UserNotFoundException.class,
                () -> addressService.createAddress(USER_PHONE, validRequest)
        );

        verify(userService).findUserOrThrow(USER_PHONE);
    }

    @Test
    void getAddressesByUser_validUser_shouldReturnAddresses() {
        User validUser = UserFactory.createActivatedUser();

        List<Address> addresses = List.of(
                AddressFactory.createValidAddress(validUser),
                AddressFactory.createValidAddress(validUser)
        );

        when(userService.findUserOrThrow(validUser.getPhone())).thenReturn(validUser);

        when(addressRepository.findByUserAndIsDeletedFalse(validUser)).thenReturn(addresses);

        AddressesResponse addressesResponse = addressService.getAddressesByUser(validUser.getPhone());

        assertNotNull(addressesResponse);
        assertNotNull(addressesResponse.addresses());

        assertEquals(addresses.size(), addressesResponse.addresses().size());
        assertEquals(addresses.get(1).getId(), addressesResponse.addresses().get(1).id());

        verify(userService).findUserOrThrow(validUser.getPhone());
        verify(addressRepository).findByUserAndIsDeletedFalse(validUser);
    }

    @Test
    void getAddressesByUser_notFoundUser_shouldThrowException() {
        when(userService.findUserOrThrow(USER_PHONE)).thenThrow(new UserNotFoundException(USER_PHONE));

        assertThrows(UserNotFoundException.class, () -> addressService.getAddressesByUser(USER_PHONE));

        verify(userService).findUserOrThrow(USER_PHONE);
    }

    @Test
    void deleteAddress_valid_shouldReturnAddressResponse() {
        User validUser = UserFactory.createActivatedUser();

        Address validAddress = AddressFactory.createValidAddress(validUser);

        when(userService.findUserOrThrow(validUser.getPhone())).thenReturn(validUser);

        when(addressRepository.findByUserAndId(validUser, validAddress.getId())).thenReturn(Optional.of(validAddress));

        when(addressRepository.save(validAddress)).thenReturn(validAddress);

        AddressResponse addressResponse = addressService.deleteAddress(validUser.getPhone(), validAddress.getId());

        assertNotNull(addressResponse);
        assertNotNull(addressResponse.address());

        assertEquals(validAddress.getId(), addressResponse.address().id());

        verify(userService).findUserOrThrow(validUser.getPhone());
        verify(addressRepository).findByUserAndId(validUser, validAddress.getId());
        verify(addressRepository).save(validAddress);

        verifyNoMoreInteractions(userService, addressRepository);
    }

    @Test
    void deleteAddress_userNotFound_shouldThrowException() {
        when(userService.findUserOrThrow(USER_PHONE)).thenThrow(new UserNotFoundException(USER_PHONE));

        assertThrows(
                UserNotFoundException.class,
                () -> addressService.deleteAddress(USER_PHONE, ADDRESS_ID)
        );

        verify(userService).findUserOrThrow(USER_PHONE);
    }

    @Test
    void deleteAddress_addressNotFound_shouldThrowException() {
        User validUser = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(validUser.getPhone())).thenReturn(validUser);

        when(addressRepository.findByUserAndId(validUser, ADDRESS_ID)).thenReturn(Optional.empty());

        assertThrows(
                AddressNotFoundException.class,
                () -> addressService.deleteAddress(validUser.getPhone(), ADDRESS_ID)
        );

        verify(userService).findUserOrThrow(validUser.getPhone());
        verify(addressRepository).findByUserAndId(validUser, ADDRESS_ID);
    }
}
