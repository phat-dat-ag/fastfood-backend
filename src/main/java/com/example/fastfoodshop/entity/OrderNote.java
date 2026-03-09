package com.example.fastfoodshop.entity;

import com.example.fastfoodshop.entity.base.BaseCreatedEntity;
import com.example.fastfoodshop.enums.AuthorType;
import com.example.fastfoodshop.enums.NoteType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_notes")
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants(innerTypeName = "Field")
public class OrderNote extends BaseCreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "author_type", length = 20, nullable = false)
    AuthorType authorType;

    @Enumerated(EnumType.STRING)
    @Column(name = "note_type", length = 20, nullable = false)
    NoteType noteType;

    @Column(name = "message", length = 100)
    String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    Order order;
}
