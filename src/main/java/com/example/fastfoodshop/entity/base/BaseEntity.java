package com.example.fastfoodshop.entity.base;

import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

@MappedSuperclass
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants(innerTypeName = "Field")
public abstract class BaseEntity {
}
