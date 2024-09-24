package com.example.merchant.global.error;

import lombok.Getter;

@Getter
public class ErrorResponseDto {
    private final String message;

    public ErrorResponseDto(ErrorCode errorCode) {
        this.message = errorCode.getMessage();
    }
}