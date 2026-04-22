package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.exception.product.ProductNotFoundException;
import com.example.fastfoodshop.factory.product.ProductFactory;
import com.example.fastfoodshop.repository.ProductRepository;
import com.example.fastfoodshop.service.implementation.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductServiceImpl productService;

    private static final Long PRODUCT_ID = 123L;

    @Test
    void findAllByIds_shouldReturnProductList() {
        List<Long> productIds = List.of(PRODUCT_ID);

        Product product = ProductFactory.createActivatedProduct(PRODUCT_ID);

        List<Product> products = List.of(product);

        when(productRepository.findAllById(productIds)).thenReturn(products);

        List<Product> productResponse = productService.findAllByIds(productIds);

        assertEquals(products.size(), productResponse.size());
        assertEquals(products.get(0).getId(), productResponse.get(0).getId());

        verify(productRepository).findAllById(productIds);
    }

    @Test
    void findAllByIds_notFoundProduct_shouldThrowProductNotFoundException() {
        List<Long> productIds = List.of(PRODUCT_ID);

        when(productRepository.findAllById(productIds)).thenReturn(List.of());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.findAllByIds(productIds)
        );

        verify(productRepository).findAllById(productIds);
    }
}