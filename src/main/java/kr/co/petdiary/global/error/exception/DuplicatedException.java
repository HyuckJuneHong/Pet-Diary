package kr.co.petdiary.global.error.exception;

import kr.co.petdiary.global.error.model.ErrorResult;

public class DuplicatedException extends RuntimeException {
    public DuplicatedException(ErrorResult errorResult) {
        super(errorResult.getMessage());
    }
}
