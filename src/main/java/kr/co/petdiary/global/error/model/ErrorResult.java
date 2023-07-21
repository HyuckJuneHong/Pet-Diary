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
    FAILURE_LOGIN("이메일 혹은 비밀번호가 잘못되었습니다.", HttpStatus.UNAUTHORIZED),

    FAILURE_JWT_TOKEN_EXTRACTION("JWT Token 추출에 실패하였습니다.", HttpStatus.UNAUTHORIZED),
    FAILURE_JWT_EMAIL_EXTRACTION("JWT Token에서 이메일 추출에 실패하였습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_JWT_CLAIMS("JWT 토큰의 클레임이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_JWT_TOKEN("JWT 토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    MALFORMED_JWT_TOKEN("JWT가 올바르게 구성되지 않아서 거부되었습니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_JWT_TOKEN("JWT가 만료되어 수락이 거부되었습니다.", HttpStatus.UNAUTHORIZED),
    FAILURE_KEY_CONVERSION("키 변환을 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String message;
    private final HttpStatus httpStatus;
}
