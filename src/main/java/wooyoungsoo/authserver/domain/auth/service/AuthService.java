package wooyoungsoo.authserver.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooyoungsoo.authserver.domain.auth.oauth2.apple.AppleEmailExtractor;
import wooyoungsoo.authserver.domain.auth.oauth2.google.GoogleEmailExtractor;
import wooyoungsoo.authserver.domain.auth.oauth2.kakao.KakaoEmailExtractor;
import wooyoungsoo.authserver.global.common.JwtProvider;
import wooyoungsoo.authserver.domain.auth.entity.member.WYSMemberDetails;
import wooyoungsoo.authserver.domain.auth.dto.response.JwtResponseDto;
import wooyoungsoo.authserver.domain.auth.dto.MemberRegisterDto;
import wooyoungsoo.authserver.domain.auth.entity.member.Member;
import wooyoungsoo.authserver.domain.auth.entity.member.Oauth2Provider;
import wooyoungsoo.authserver.domain.auth.entity.RefreshToken;
import wooyoungsoo.authserver.domain.auth.exception.member.MemberAlreadyExistException;
import wooyoungsoo.authserver.domain.auth.exception.member.MemberNotExistException;
import wooyoungsoo.authserver.domain.auth.repository.MemberRepository;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class AuthService {
    private final GoogleEmailExtractor googleEmailExtractor;
    private final KakaoEmailExtractor kakaoEmailExtractor;
    private final AppleEmailExtractor appleEmailExtractor;
    private final RefreshTokenService refreshTokenService;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public JwtResponseDto signup(Oauth2Provider oauth2Provider, MemberRegisterDto memberRegisterDto) {
        Member member = Member.from(oauth2Provider, memberRegisterDto);
        memberRepository.save(member);

        WYSMemberDetails userDetails = new WYSMemberDetails(member);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, "", userDetails.getAuthorities());

        String accessToken = jwtProvider.createAccessToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(member);

        return JwtResponseDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getTokenValue())
                .build();
    }

    public JwtResponseDto login(Oauth2Provider oauth2Provider, String oauth2Token) {
        return loginByOauthProvider(oauth2Provider, oauth2Token);
    }

    private void loginByEmailAndPassword(String email, String password) {
        return;
    }

    private JwtResponseDto loginByOauthProvider(Oauth2Provider oauth2Provider, String oauth2Token) {
        String email;

        try {
            email = extractEmailFromOauth2Token(oauth2Provider, oauth2Token);
        } catch (ParseException e) {
            throw new RuntimeException("parse error");
        }

        if (email != null) {
            // 여기서 이메일을 갖는 유저가 없으면 예외를 던진다
            Member member = memberRepository.findByEmail(email).orElseThrow(() ->
                    new MemberNotExistException(email));
            WYSMemberDetails userDetails = new WYSMemberDetails(member);
            log.info("member is present");

            if (oauth2Provider.equals(userDetails.getOauth2Provider())) {
                log.info("{}에서 온 유저", oauth2Provider.name());
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, "", userDetails.getAuthorities());

                log.info("이제 토큰 만들어유");

                String accessToken = jwtProvider.createAccessToken(authentication);
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(member);
                log.info("멤버서비스에서의 값 {}", refreshToken.getTokenValue());
                return JwtResponseDto
                        .builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken.getTokenValue())
                        .build();
            }

            log.info("다른 곳에서 가입했어요!");
            throw new MemberAlreadyExistException(member.getOauth2Provider());
        }

        // TODO: 로그인 실패 처리를 해야 함
        throw new RuntimeException("email == null");
    }

    private String extractEmailFromOauth2Token(Oauth2Provider oauth2Provider,
                                                     String oauth2Token)
            throws ParseException {
        String email = null;

        log.info("온 토큰 : {}", oauth2Token);
        if (Oauth2Provider.GOOGLE.equals(oauth2Provider)) {
            email = googleEmailExtractor.extractEmailFromGoogleAccessToken(oauth2Token);
        }

        if (Oauth2Provider.KAKAO.equals(oauth2Provider)) {
            email = kakaoEmailExtractor.extractEmailFromKakaoAccessToken(oauth2Token);
        }

        if (Oauth2Provider.APPLE.equals(oauth2Provider)) {
            email = appleEmailExtractor.extractEmailFromAppleIdToken(oauth2Token);
        }

        return email;
    }

    public JwtResponseDto reIssue(String refreshTokenValue) {
        RefreshToken oldRefreshToken = refreshTokenService
                .findOldRefreshTokenByTokenValue(refreshTokenValue);

        log.info("프론트쪽 리토: {}", refreshTokenValue);
        log.info("디비쪽 리토: {}", oldRefreshToken.getTokenValue());

        if (oldRefreshToken != null && refreshTokenValue.equals(oldRefreshToken.getTokenValue())) {
            jwtProvider.validateRefreshToken(refreshTokenValue);

            Member member = oldRefreshToken.getMember();
            WYSMemberDetails userDetails = new WYSMemberDetails(member);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, "", userDetails.getAuthorities());

            String newAccessToken = jwtProvider.createAccessToken(authentication);
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(member);
            return JwtResponseDto
                    .builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken.getTokenValue())
                    .build();
        }

        throw new RuntimeException("refresh token이 없거나.. 뭐 그렇습니다");
    }
}
