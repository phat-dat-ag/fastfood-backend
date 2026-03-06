package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.enums.NoteType;

public interface OrderNoteService {
    void createOrderNoteByUser(Order order, NoteType noteType, String message);

    void createOrderNoteByStaff(Order order, NoteType noteType, String message);
}
