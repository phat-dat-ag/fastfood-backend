package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Image;
import com.example.fastfoodshop.enums.PageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByPageType(PageType pageType);
}
