package kr.co.petdiary.global.error.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorResult {
    NOT_FOUND_OWNER("반려인을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATED_EMAIL("중복된 이메일 입니다.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PARAMETER("파라미터 값이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_SUPPORTED_AUTHENTICATION_CONTENT_TYPE("해당 Content-Type은 지원하지 않습니다.", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    FAILURE_JWT_TOKEN_EXTRACTION("JWT Token 추출에 실패하였습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_JWT_CLAIMS("JWT 토큰의 클레임이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    ;

    private final String message;
    private final HttpStatus httpStatus;
}
