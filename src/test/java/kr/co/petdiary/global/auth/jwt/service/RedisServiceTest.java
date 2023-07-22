package kr.co.petdiary.global.auth.jwt.service;


import kr.co.petdiary.global.error.exception.InvalidJwtTokenException;
import kr.co.petdiary.global.error.model.ErrorResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class RedisServiceTest {
    @Autowired
    private RedisService redisService;

    @Autowired
    private JwtService jwtService;

    private String email;

    @BeforeEach
    void beforeEach() {
        email = "test@email.com";
    }

    @Test
    void 레디스에_RefreshToken_저장_후_확인() {
        //given
        final String refreshToken = jwtService.createRefreshToken();

        //when
        redisService.setRefreshTokenToRedis(refreshToken, email);
        final String actual = redisService.getEmailByRefreshToken(refreshToken);

        //then
        assertThat(actual).isEqualTo(email);
    }

    @Test
    void RefreshToken으로_AccessToken_재발급() {
        //given
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final String refreshToken = jwtService.createRefreshToken();
        redisService.setRefreshTokenToRedis(refreshToken, email);

        //when
        final String accessToken = redisService.reissueAccessTokenByRefreshToken(response, refreshToken);
        final String actual = jwtService.extractEmailByAccessToken(accessToken)
                .orElseThrow(() -> new InvalidJwtTokenException(ErrorResult.FAILURE_JWT_EMAIL_EXTRACTION));

        //then
        assertThat(actual).isEqualTo(email);
        assertThat(redisService.getEmailByRefreshToken(refreshToken)).isNull();
    }
}
