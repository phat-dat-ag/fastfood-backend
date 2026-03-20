package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.enums.AuthorType;
import com.example.fastfoodshop.enums.NoteType;

public interface OrderNoteService {
    void createOrderNote(Order order, NoteType noteType, String message, AuthorType authorType);
}
