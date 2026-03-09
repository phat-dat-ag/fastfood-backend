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
@Table(name = "topics")
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants(innerTypeName = "Field")
public class Topic extends BaseAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", length = 60, nullable = false)
    String name;

    @Column(name = "slug", nullable = false, unique = true)
    String slug;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    String description;

    @Column(name = "is_activated", nullable = false)
    boolean isActivated;

    @Column(name = "is_deleted", nullable = false)
    boolean isDeleted = false;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<TopicDifficulty> topicDifficulties = new ArrayList<>();
}
