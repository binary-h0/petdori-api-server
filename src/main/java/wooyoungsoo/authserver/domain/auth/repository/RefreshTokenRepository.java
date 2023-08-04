package wooyoungsoo.authserver.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wooyoungsoo.authserver.domain.auth.entity.member.Member;
import wooyoungsoo.authserver.domain.auth.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByMember(Member member);
    Optional<RefreshToken> findByTokenValue(String tokenValue);
}
