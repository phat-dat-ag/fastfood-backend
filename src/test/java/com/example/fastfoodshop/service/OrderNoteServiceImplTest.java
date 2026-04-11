package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.OrderNote;
import com.example.fastfoodshop.enums.AuthorType;
import com.example.fastfoodshop.enums.NoteType;
import com.example.fastfoodshop.factory.order.OrderFactory;
import com.example.fastfoodshop.factory.order.OrderNoteFactory;
import com.example.fastfoodshop.repository.OrderNoteRepository;
import com.example.fastfoodshop.service.implementation.OrderNoteServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderNoteServiceImplTest {
    @Mock
    OrderNoteRepository orderNoteRepository;

    @InjectMocks
    OrderNoteServiceImpl orderNoteService;

    private static final String NOTE_MESSAGE = "Giao nhanh chong";
    private static final NoteType NOTE_TYPE = NoteType.USER_NOTE;
    private static final AuthorType AUTHOR_TYPE = AuthorType.USER;

    @Test
    void createOrderNote_validRequest_shouldBeSuccessful() {
        Order order = OrderFactory.createpPendingOrder();

        OrderNote orderNote = OrderNoteFactory.createValid(order, NOTE_MESSAGE, NOTE_TYPE, AUTHOR_TYPE);

        when(orderNoteRepository.save(any(OrderNote.class))).thenReturn(orderNote);

        orderNoteService.createOrderNote(order, NOTE_TYPE, NOTE_MESSAGE, AUTHOR_TYPE);

        verify(orderNoteRepository).save(any(OrderNote.class));
    }
}