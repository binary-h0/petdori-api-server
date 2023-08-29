package wooyoungsoo.authserver.domain.auth.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import wooyoungsoo.authserver.domain.auth.entity.member.Member;

@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 24 * 30)
@Builder
@Getter
public class RefreshToken {
    @Id
    private String tokenValue;
//    @Indexed
//    private Member member;
}
