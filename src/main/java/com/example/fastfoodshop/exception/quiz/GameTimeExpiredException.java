package com.example.fastfoodshop.exception.quiz;

import com.example.fastfoodshop.exception.base.BusinessException;

public class GameTimeExpiredException extends BusinessException {
    public GameTimeExpiredException() {
        super("GAME_TIME_EXPIRED", "Đã hết thời gian tham gia");
    }
}
