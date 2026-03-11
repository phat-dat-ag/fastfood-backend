package com.example.fastfoodshop.response.user;

import com.example.fastfoodshop.dto.UserDTO;
import com.example.fastfoodshop.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public record UserPageResponse(
        List<UserDTO> users,
        int currentPage,
        int pageSize,
        long totalItems,
        int totalPages
) {
    public static UserPageResponse from(Page<User> page) {
        List<UserDTO> userDTOs = page.getContent().stream().map(UserDTO::from).toList();

        return new UserPageResponse(
                userDTOs,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
