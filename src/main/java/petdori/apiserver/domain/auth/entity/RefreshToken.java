package petdori.apiserver.domain.auth.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 24 * 30)
@Builder
@Getter
public class RefreshToken {
    @Id
    private String tokenValue;
//    @Indexed
//    private Member member;
}
