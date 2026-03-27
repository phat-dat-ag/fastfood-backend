package com.example.fastfoodshop.entity;

import com.example.fastfoodshop.entity.base.BaseAuditableEntity;
import com.example.fastfoodshop.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants(innerTypeName = "Field")
public class User extends BaseAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", length = 200, nullable = false)
    String name;

    @Column(name = "phone", length = 15, nullable = false, unique = true)
    String phone;

    @Column(name = "email", length = 255, nullable = false)
    String email;

    @Column(name = "birthday", nullable = false)
    LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 15, nullable = false)
    UserRole role = UserRole.USER;

    @Column(name = "password_hash", length = 255, nullable = false)
    @JsonIgnore
    String passwordHash;

    @Column(name = "avatar_url", length = 255)
    String avatarUrl;

    @Column(name = "avatar_public_id", length = 255)
    String avatarPublicId;

    @Column(name = "is_activated", nullable = false)
    boolean isActivated = false;

    @Column(name = "is_deleted", nullable = false)
    boolean isDeleted = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<OTPCode> otpCodes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Cart> carts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Promotion> promotions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Quiz> quizzes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Image> images = new ArrayList<>();
}
