package wooyoungsoo.authserver.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
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

        return generateJwtResponse(authentication, member);
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


            if (oauth2Provider.equals(userDetails.getOauth2Provider())) {
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, "", userDetails.getAuthorities());

                return generateJwtResponse(authentication, member);
            }

            // 다른 소셜 플랫폼에서 가입한 경우 아래 예외를 던진다
            throw new MemberAlreadyExistException(member.getOauth2Provider());
        }
        // 토큰으로부터 이메일을 가져오지 못한 경우
        throw new RuntimeException("email == null");
    }

    private String extractEmailFromOauth2Token(Oauth2Provider oauth2Provider,
                                                     String oauth2Token)
            throws ParseException {
        String email = null;

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

        if (oldRefreshToken != null && refreshTokenValue.equals(oldRefreshToken.getTokenValue())) {
            jwtProvider.validateRefreshToken(refreshTokenValue);

            Member member = oldRefreshToken.getMember();
            WYSMemberDetails userDetails = new WYSMemberDetails(member);
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, "", userDetails.getAuthorities());

            return generateJwtResponse(authentication, member);
        }

        throw new RuntimeException("refresh token이 없거나.. 뭐 그렇습니다");
    }

    private JwtResponseDto generateJwtResponse(Authentication authentication, Member member) {
        String accessToken = jwtProvider.createAccessToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(member);

        return JwtResponseDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getTokenValue())
                .build();
    }
}
