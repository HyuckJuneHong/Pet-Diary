package kr.co.petdiary.global.error.exception;

import kr.co.petdiary.global.error.model.ErrorResult;

public class OAuthLoginException extends RuntimeException {
    private final ErrorResult errorResult;

    public OAuthLoginException(ErrorResult errorResult) {
        super(errorResult.getMessage());
        this.errorResult = errorResult;
    }
}
