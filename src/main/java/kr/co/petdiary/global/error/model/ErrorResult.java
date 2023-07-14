package kr.co.petdiary.global.error.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorResult {
    NOT_FOUND_OWNER("반려인을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATED_EMAIL("중복된 이메일 입니다.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD("비밀번호와 확인 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    ;
    
    private final String message;
    private final HttpStatus httpStatus;
}
