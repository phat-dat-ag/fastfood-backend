package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.UserDTO;
import com.example.fastfoodshop.entity.User;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserResponse {
    private List<UserDTO> users = new ArrayList<>();
    private int currentPage;
    private int pageSize;
    private long totalItems;
    private int totalPages;

    public UserResponse(Page<User> page) {
        for (User user : page.getContent()) {
            this.users.add(new UserDTO(user));
        }
        this.currentPage = page.getNumber();
        this.pageSize = page.getSize();
        this.totalItems = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}
