package kr.co.petdiary.global.error.exception;

import kr.co.petdiary.global.error.model.ErrorResult;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(ErrorResult errorResult) {
        super(errorResult.getMessage());
    }
}
