package kr.co.petdiary.global.error.exception;

import kr.co.petdiary.global.error.model.ErrorResult;

public class ExtractJwtTokenException extends RuntimeException {
    private final ErrorResult errorResult;

    public ExtractJwtTokenException(ErrorResult errorResult) {
        super(errorResult.getMessage());
        this.errorResult = errorResult;
    }
}
