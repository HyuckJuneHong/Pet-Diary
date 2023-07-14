package kr.co.petdiary.global.error.handler;

import kr.co.petdiary.global.error.exception.DuplicatedException;
import kr.co.petdiary.global.error.exception.EntityNotFoundException;
import kr.co.petdiary.global.error.exception.PasswordInvalidException;
import kr.co.petdiary.global.error.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleNotFoundException(EntityNotFoundException e) {
        log.warn("======= Handle EntityNotFoundException =======", e);
        ErrorResponse errorResponse = makeResponseErrorFormat(e.getMessage(), e.getStatus());
        return handleExceptionInternal(errorResponse);
    }

    @ExceptionHandler(DuplicatedException.class)
    protected ResponseEntity<ErrorResponse> handleDuplicatedException(DuplicatedException e) {
        log.warn("======= Handle DuplicatedException =======", e);
        ErrorResponse errorResponse = makeResponseErrorFormat(e.getMessage(), e.getStatus());
        return handleExceptionInternal(errorResponse);
    }

    @ExceptionHandler(PasswordInvalidException.class)
    protected ResponseEntity<ErrorResponse> handlePasswordInvalidException(PasswordInvalidException e) {
        log.warn("======= Handle PasswordInvalidException =======", e);
        ErrorResponse errorResponse = makeResponseErrorFormat(e.getMessage(), e.getStatus());
        return handleExceptionInternal(errorResponse);
    }

    private ResponseEntity<ErrorResponse> handleExceptionInternal(ErrorResponse errorResponse) {
        return ResponseEntity
                .status(errorResponse.status())
                .body(errorResponse);
    }

    private ErrorResponse makeResponseErrorFormat(String message, HttpStatus status) {
        return ErrorResponse.builder()
                .message(message)
                .status(status)
                .build();
    }
}
