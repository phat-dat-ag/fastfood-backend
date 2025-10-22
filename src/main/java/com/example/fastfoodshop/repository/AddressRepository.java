package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Address;
import com.example.fastfoodshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserAndIsDeletedFalse(User user);

    Optional<Address> findByUserAndId(User user, Long id);
}