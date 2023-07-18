package kr.co.petdiary.global.error.exception;

import kr.co.petdiary.global.error.model.ErrorResult;

public class JwtTokenExtractException extends RuntimeException {
    private final ErrorResult errorResult;

    public JwtTokenExtractException(ErrorResult errorResult) {
        super(errorResult.getMessage());
        this.errorResult = errorResult;
    }
}
