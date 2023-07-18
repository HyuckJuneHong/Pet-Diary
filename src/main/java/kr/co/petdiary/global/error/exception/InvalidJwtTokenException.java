package kr.co.petdiary.global.error.exception;

import kr.co.petdiary.global.error.model.ErrorResult;

public class InvalidJwtTokenException extends RuntimeException {
    private final ErrorResult errorResult;

    public InvalidJwtTokenException(ErrorResult errorResult) {
        super(errorResult.getMessage());
        this.errorResult = errorResult;
    }
}
