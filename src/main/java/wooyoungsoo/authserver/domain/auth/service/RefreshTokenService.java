package wooyoungsoo.authserver.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooyoungsoo.authserver.global.common.JwtProvider;
import wooyoungsoo.authserver.domain.auth.entity.member.Member;
import wooyoungsoo.authserver.domain.auth.entity.RefreshToken;
import wooyoungsoo.authserver.domain.auth.repository.RefreshTokenRepository;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    public RefreshToken createRefreshToken(Member member) {
        String refreshToken = jwtProvider.createRefreshToken();
        RefreshToken oldRefreshToken = findOldRefreshTokenByMember(member);
        if (oldRefreshToken != null) {
            log.info("refresh 토큰이 이미 존재합니다. 덮어씌웁니다");
            oldRefreshToken.setTokenValue(refreshToken);
            log.info("리프레시토큰서비스에서의 값 {}", oldRefreshToken.getTokenValue());
            return oldRefreshToken;
        }
        return refreshTokenRepository.save(
                RefreshToken.builder()
                        .member(member)
                        .tokenValue(refreshToken)
                        .build());
    }

    public RefreshToken findOldRefreshTokenByMember(Member member) {
        Optional<RefreshToken> oldRefreshToken = refreshTokenRepository.findByMember(member);
        return oldRefreshToken.orElse(null);
    }

    public RefreshToken findOldRefreshTokenByTokenValue(String tokenValue) {
        Optional<RefreshToken> oldRefreshToken = refreshTokenRepository.findByTokenValue(tokenValue);
        return oldRefreshToken.orElse(null);
    }
}
