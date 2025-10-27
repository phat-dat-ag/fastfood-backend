package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.OrderNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderNoteRepository extends JpaRepository<OrderNote, Long> {
}
