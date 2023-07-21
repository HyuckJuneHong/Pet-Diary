package kr.co.petdiary.global.auth.jwt.service;

import kr.co.petdiary.global.error.exception.InvalidJwtTokenException;
import kr.co.petdiary.global.error.model.ErrorResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    @Value("${jwt.refresh.expire}")
    private long REFRESH_EXPIRE;

    private final RedisTemplate<String, String> redisTemplate;

    public String getEmailByRefreshToken(String refreshToken) {
        isRefreshToken(refreshToken);
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(refreshToken);
    }

    public void setRefreshTokenToRedis(String refreshToken, String email) {
        redisTemplate.opsForValue()
                .set(refreshToken, email, REFRESH_EXPIRE, TimeUnit.MILLISECONDS);
    }

    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete(refreshToken);
    }

    private void isRefreshToken(String refreshToken) {
        if (Boolean.FALSE.equals(this.redisTemplate.hasKey(refreshToken))) {
            throw new InvalidJwtTokenException(ErrorResult.INVALID_JWT_TOKEN);
        }
    }
}
