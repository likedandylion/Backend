package com.likedandylion.prome.global.exception;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String code, String message) {
        super(code, message);
    }
}
