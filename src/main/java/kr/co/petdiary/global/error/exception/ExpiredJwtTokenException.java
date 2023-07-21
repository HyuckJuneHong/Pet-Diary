package kr.co.petdiary.global.error.exception;

import kr.co.petdiary.global.error.model.ErrorResult;

public class ExpiredJwtTokenException extends RuntimeException {
    private final ErrorResult errorResult;

    public ExpiredJwtTokenException(ErrorResult errorResult) {
        super(errorResult.getMessage());
        this.errorResult = errorResult;
    }
}
