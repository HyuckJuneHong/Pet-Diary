package kr.co.petdiary.global.error.exception;

import kr.co.petdiary.global.error.model.ErrorResult;
import lombok.Getter;

@Getter
public class InvalidPasswordException extends RuntimeException {
    private final ErrorResult errorResult;

    public InvalidPasswordException(ErrorResult errorResult) {
        super(errorResult.getMessage());
        this.errorResult = errorResult;
    }
}
