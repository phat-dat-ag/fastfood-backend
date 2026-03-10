package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.OrderNote;
import com.example.fastfoodshop.enums.AuthorType;
import com.example.fastfoodshop.enums.NoteType;

import java.time.LocalDateTime;
import java.time.ZoneId;

public record OrderNoteDTO(
        AuthorType authorType,
        NoteType noteType,
        String message,
        LocalDateTime createdAt
) {
    public static OrderNoteDTO from(OrderNote orderNote) {
        return new OrderNoteDTO(
                orderNote.getAuthorType(),
                orderNote.getNoteType(),
                orderNote.getMessage(),
                orderNote.getCreatedAt().
                        atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );
    }
}
