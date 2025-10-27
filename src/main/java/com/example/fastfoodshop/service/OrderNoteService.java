package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Order;
import com.example.fastfoodshop.entity.OrderNote;
import com.example.fastfoodshop.enums.AuthorType;
import com.example.fastfoodshop.enums.NoteType;
import com.example.fastfoodshop.repository.OrderNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderNoteService {
    private final OrderNoteRepository orderNoteRepository;

    public void createOrderNoteByUser(Order order, NoteType noteType, String message) {
        OrderNote orderNote = new OrderNote();
        orderNote.setAuthorType(AuthorType.USER);
        orderNote.setNoteType(noteType);
        orderNote.setMessage(message);
        orderNote.setOrder(order);

        orderNoteRepository.save(orderNote);
    }

    public void createOrderNoteByUser(Order order) {
        OrderNote orderNote = new OrderNote();
        orderNote.setAuthorType(AuthorType.STAFF);
        orderNote.setNoteType(NoteType.CANCEL_REASON);
        orderNote.setMessage("Đơn hàng bị hủy do không được thanh toán trong thời gian quy định");
        orderNote.setOrder(order);

        orderNoteRepository.save(orderNote);
    }
}
