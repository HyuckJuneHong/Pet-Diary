package kr.co.petdiary.global.error.exception;

import kr.co.petdiary.global.error.model.ErrorResult;

public class PasswordInvalidException extends RuntimeException {
    public PasswordInvalidException(ErrorResult errorResult) {
        super(errorResult.getMessage());
    }
}
