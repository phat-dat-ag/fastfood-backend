package com.example.fastfoodshop.entity;

import com.example.fastfoodshop.entity.base.BaseAuditableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants(innerTypeName = "Field")
public class Category extends BaseAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", nullable = false, length = 80)
    String name;

    @Column(name = "slug", nullable = false, unique = true)
    String slug;

    @Column(name = "description", nullable = false, length = 100)
    String description;

    @Column(name = "image_url")
    String imageUrl;

    @Column(name = "image_public_id")
    String imagePublicId;

    @Column(name = "is_activated", nullable = false)
    boolean isActivated = false;

    @Column(name = "is_deleted", nullable = false)
    boolean isDeleted = false;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Promotion> promotions = new ArrayList<>();
}
