package com.example.fastfoodshop.factory.order;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.OrderNote;
import com.example.fastfoodshop.enums.AuthorType;
import com.example.fastfoodshop.enums.NoteType;

public class OrderNoteFactory {
    private static final Long NOTE_ID = 789L;

    public static OrderNote createValid(Order order, String message, NoteType noteType, AuthorType authorType) {
        OrderNote orderNote = new OrderNote();

        orderNote.setId(NOTE_ID);
        orderNote.setOrder(order);
        orderNote.setMessage(message);
        orderNote.setNoteType(noteType);
        orderNote.setAuthorType(authorType);

        return orderNote;
    }
}
