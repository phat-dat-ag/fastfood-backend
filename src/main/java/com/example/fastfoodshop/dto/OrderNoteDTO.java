package com.example.fastfoodshop.dto;

import com.example.fastfoodshop.entity.OrderNote;
import com.example.fastfoodshop.enums.AuthorType;
import com.example.fastfoodshop.enums.NoteType;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class OrderNoteDTO {
    private AuthorType authorType;
    private NoteType noteType;
    private String message;
    private LocalDateTime createdAt;

    public OrderNoteDTO(OrderNote orderNote) {
        this.authorType = orderNote.getAuthorType();
        this.noteType = orderNote.getNoteType();
        this.message = orderNote.getMessage();
        this.createdAt = orderNote.getCreatedAt().
                atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
