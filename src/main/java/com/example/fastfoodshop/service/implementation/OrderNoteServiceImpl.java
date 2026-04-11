package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.OrderNote;
import com.example.fastfoodshop.enums.AuthorType;
import com.example.fastfoodshop.enums.NoteType;
import com.example.fastfoodshop.repository.OrderNoteRepository;
import com.example.fastfoodshop.service.OrderNoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderNoteServiceImpl implements OrderNoteService {
    private final OrderNoteRepository orderNoteRepository;

    public void createOrderNote(Order order, NoteType noteType, String message, AuthorType authorType) {
        OrderNote orderNote = new OrderNote();

        orderNote.setAuthorType(authorType);
        orderNote.setNoteType(noteType);
        orderNote.setMessage(message);
        orderNote.setOrder(order);

        log.info("[OrderNoteService] Successfully created note for order id={}", order.getId());

        orderNoteRepository.save(orderNote);
    }
}