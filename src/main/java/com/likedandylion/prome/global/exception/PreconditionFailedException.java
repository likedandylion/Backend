package com.likedandylion.prome.global.exception;

public class PreconditionFailedException extends BusinessException {
    public PreconditionFailedException(String code, String message) {
        super(code, message);
    }
}
