package wooyoungsoo.authserver.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 60 * 60 * 24 * 30;

    public void saveRefreshToken(String email, String refreshToken) {
        redisTemplate.opsForValue().set(email, refreshToken, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    public String getRefreshToken(String email) {
        Object refreshToken = redisTemplate.opsForValue().get(email);
        return refreshToken == null ? null : (String) refreshToken;
    }

    public void deleteRefreshToken(String email) {
        redisTemplate.delete(email);
    }
}
