package petdori.apiserver.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petdori.apiserver.domain.auth.dto.MemberRegisterDto;
import petdori.apiserver.global.common.JwtProvider;
import petdori.apiserver.domain.auth.exception.token.RefreshTokenNotMatchedException;
import petdori.apiserver.domain.auth.oauth2.apple.AppleEmailExtractor;
import petdori.apiserver.domain.auth.oauth2.google.GoogleEmailExtractor;
import petdori.apiserver.domain.auth.oauth2.kakao.KakaoEmailExtractor;
import petdori.apiserver.domain.auth.entity.member.PetdoriMemberDetails;
import petdori.apiserver.domain.auth.dto.response.JwtResponseDto;
import petdori.apiserver.domain.auth.entity.member.Member;
import petdori.apiserver.domain.auth.entity.member.Oauth2Provider;
import petdori.apiserver.domain.auth.exception.member.MemberAlreadyExistException;
import petdori.apiserver.domain.auth.exception.member.MemberNotExistException;
import petdori.apiserver.domain.auth.repository.MemberRepository;

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

        PetdoriMemberDetails userDetails = new PetdoriMemberDetails(member);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, "", userDetails.getAuthorities());

        refreshTokenService.deleteRefreshToken(member.getEmail());
        return generateJwtResponse(authentication);
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
            PetdoriMemberDetails userDetails = new PetdoriMemberDetails(member);


            if (oauth2Provider.equals(member.getOauth2Provider())) {
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, "", userDetails.getAuthorities());

                refreshTokenService.deleteRefreshToken(email);
                return generateJwtResponse(authentication);
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

    public JwtResponseDto reIssue(String accessToken, String refreshToken) {
        Authentication authentication = jwtProvider.getAuthenticationFromToken(accessToken);
        String email = authentication.getName();
        log.info("액세스에서 뽑은 email: {}", email);

        String refreshTokenAtRedis = refreshTokenService.getRefreshToken(email);

        if (refreshTokenAtRedis != null && refreshToken.equals(refreshTokenAtRedis)) {
            jwtProvider.validateRefreshToken(refreshToken);
            refreshTokenService.deleteRefreshToken(email);
            return generateJwtResponse(authentication);
        }

        throw new RefreshTokenNotMatchedException();
    }

    private JwtResponseDto generateJwtResponse(Authentication authentication) {
        String accessToken = jwtProvider.createAccessToken(authentication);
        String refreshToken = jwtProvider.createRefreshToken();
        refreshTokenService.saveRefreshToken(authentication.getName(), refreshToken);

        return JwtResponseDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
