package kr.co.petdiary.global.auth.jwt.service;

import jakarta.servlet.http.HttpServletResponse;
import kr.co.petdiary.global.error.exception.InvalidJwtTokenException;
import kr.co.petdiary.global.error.model.ErrorResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    @Value("${jwt.access.header}")
    private String ACCESS_HEADER;

    @Value("${jwt.refresh.header}")
    private String REFRESH_HEADER;

    @Value("${jwt.refresh.expire}")
    private long REFRESH_EXPIRE;

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtService jwtService;

    public void setRefreshTokenToRedis(String refreshToken, String email) {
        deleteByRefreshToken(refreshToken);
        redisTemplate.opsForValue()
                .set(refreshToken, email, REFRESH_EXPIRE, TimeUnit.MILLISECONDS);
    }

    public String reissueAccessTokenByRefreshToken(HttpServletResponse response, String refreshToken) {
        isRefreshToken(refreshToken);
        final String email = getEmailByRefreshToken(refreshToken);
        log.info("======= " + refreshToken + " =======");
        deleteByRefreshToken(refreshToken);
        final String accessToken = jwtService.createAccessToken(email);
        final String reissueRefreshToken = jwtService.createRefreshToken();
        setRefreshTokenToRedis(reissueRefreshToken, email);
        log.info("======= reissue : " + reissueRefreshToken + " =======");
        sendTokens(response, accessToken, reissueRefreshToken);
        return accessToken;
    }

    public String getEmailByRefreshToken(String refreshToken) {
        final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(refreshToken);
    }

    private void isRefreshToken(String refreshToken) {
        if (Boolean.FALSE.equals(this.redisTemplate.hasKey(refreshToken))) {
            throw new InvalidJwtTokenException(ErrorResult.INVALID_JWT_TOKEN);
        }
    }

    private void deleteByRefreshToken(String refreshToken) {
        if (Boolean.TRUE.equals(this.redisTemplate.hasKey(refreshToken))) {
            redisTemplate.delete(refreshToken);
        }
    }

    private void sendTokens(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(ACCESS_HEADER, accessToken);
        response.setHeader(REFRESH_HEADER, refreshToken);
        log.info("======= AccessToken and RefreshToken resend to Header =======");
    }
}
