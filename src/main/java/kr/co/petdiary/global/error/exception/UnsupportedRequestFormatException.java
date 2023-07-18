package kr.co.petdiary.global.error.exception;

import kr.co.petdiary.global.error.model.ErrorResult;

public class UnsupportedRequestFormatException extends RuntimeException {
    private final ErrorResult errorResult;

    public UnsupportedRequestFormatException(ErrorResult errorResult) {
        super(errorResult.getMessage());
        this.errorResult = errorResult;
    }
}
