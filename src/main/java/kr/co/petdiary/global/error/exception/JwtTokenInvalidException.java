package kr.co.petdiary.global.error.exception;

import kr.co.petdiary.global.error.model.ErrorResult;

public class JwtTokenInvalidException extends RuntimeException {
    private final ErrorResult errorResult;

    public JwtTokenInvalidException(ErrorResult errorResult) {
        super(errorResult.getMessage());
        this.errorResult = errorResult;
    }
}
