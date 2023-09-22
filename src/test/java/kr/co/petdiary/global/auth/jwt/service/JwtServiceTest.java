package kr.co.petdiary.global.auth.jwt.service;

import kr.co.petdiary.global.error.exception.InvalidJwtTokenException;
import kr.co.petdiary.global.error.model.ErrorResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {
    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.access.header}")
    private String ACCESS_HEADER;

    @Value("${jwt.refresh.header}")
    private String REFRESH_HEADER;

    @Autowired
    private JwtService jwtService;

    private String email;
    private String accessToken;
    private String refreshToken;

    @BeforeEach
    void beforeEach() {
        email = "test@email.com";
        accessToken = jwtService.createAccessToken(email);
        refreshToken = jwtService.createRefreshToken();
    }

    @Test
    void AccessToken_발급() {
        //then
        assertThat(accessToken).isNotNull();
    }

    @Test
    void RefreshToken_발급() {
        //then
        assertThat(refreshToken).isNotNull();
    }

    @Test
    void AccessToken_검증() {
        //when, then
        assertThat(jwtService.validateToken(accessToken)).isTrue();
    }

    @Test
    void RefreshToken_검증() {
        //when, then
        assertThat(jwtService.validateToken(refreshToken)).isTrue();
    }

    @Test
    void AccessToken_추출() {
        //given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(ACCESS_HEADER, BEARER_PREFIX + accessToken);

        //when
        final String actual = jwtService.extractAccessToken(request)
                .orElseThrow(() -> new InvalidJwtTokenException(ErrorResult.FAILURE_JWT_TOKEN_EXTRACTION));

        //then
        assertThat(actual).isEqualTo(accessToken);
    }

    @Test
    void RefreshToken_추출() {
        //given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(REFRESH_HEADER, BEARER_PREFIX + refreshToken);

        //when
        final String actual = jwtService.extractRefreshToken(request)
                .orElseThrow(() -> new InvalidJwtTokenException(ErrorResult.FAILURE_JWT_TOKEN_EXTRACTION));

        //then
        assertThat(actual).isEqualTo(refreshToken);
    }

    @Test
    void AccessToken에서_이메일_추출() {
        //when
        final String actual = jwtService.extractEmailByAccessToken(accessToken)
                .orElseThrow(() -> new InvalidJwtTokenException(ErrorResult.FAILURE_JWT_EMAIL_EXTRACTION));

        //then
        assertThat(actual).isEqualTo(email);
    }
}
