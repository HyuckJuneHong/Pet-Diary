package kr.co.petdiary.global.error.exception;

import kr.co.petdiary.global.error.model.ErrorResult;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EntityNotFoundException extends RuntimeException {
    private final HttpStatus status;

    public EntityNotFoundException(ErrorResult errorResult) {
        super(errorResult.getMessage());
        this.status = errorResult.getHttpStatus();
    }
}
