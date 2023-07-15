package kr.co.petdiary.global.error.handler;

import kr.co.petdiary.global.error.exception.DuplicatedException;
import kr.co.petdiary.global.error.exception.EntityNotFoundException;
import kr.co.petdiary.global.error.exception.PasswordInvalidException;
import kr.co.petdiary.global.error.model.ErrorResponse;
import kr.co.petdiary.global.error.model.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleNotFoundException(EntityNotFoundException e) {
        log.warn("======= Handle EntityNotFoundException =======", e);
        return handleExceptionInternal(e.getErrorResult());
    }

    @ExceptionHandler(DuplicatedException.class)
    protected ResponseEntity<ErrorResponse> handleDuplicatedException(DuplicatedException e) {
        log.warn("======= Handle DuplicatedException =======", e);
        return handleExceptionInternal(e.getErrorResult());
    }

    @ExceptionHandler(PasswordInvalidException.class)
    protected ResponseEntity<ErrorResponse> handlePasswordInvalidException(PasswordInvalidException e) {
        log.warn("======= Handle PasswordInvalidException =======", e);
        return handleExceptionInternal(e.getErrorResult());
    }

    private ResponseEntity<ErrorResponse> handleExceptionInternal(ErrorResult errorResult) {
        return ResponseEntity
                .status(errorResult.getHttpStatus())
                .body(makeResponseErrorFormat(errorResult));
    }

    private ErrorResponse makeResponseErrorFormat(ErrorResult errorResult) {
        return ErrorResponse.builder()
                .message(errorResult.getMessage())
                .status(errorResult.getHttpStatus())
                .build();
    }
}
