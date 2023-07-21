package kr.co.petdiary.global.error.exception;

import kr.co.petdiary.global.error.model.ErrorResult;

public class MalformedJwtTokenException extends RuntimeException {
    private final ErrorResult errorResult;

    public MalformedJwtTokenException(ErrorResult errorResult) {
        super(errorResult.getMessage());
        this.errorResult = errorResult;
    }
}
