package com.example.fastfoodshop.response;

import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.entity.Product;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductResponse {
    List<ProductDTO> products = new ArrayList<>();
    private int currentPage;
    private int pageSize;
    private long totalItems;
    private int totalPages;

    public ProductResponse(Page<Product> page) {
        for (Product product : page.getContent()) {
            this.products.add(new ProductDTO(product));
        }
        this.currentPage = page.getNumber();
        this.pageSize = page.getSize();
        this.totalItems = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}
