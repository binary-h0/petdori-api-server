package wooyoungsoo.authserver.domain.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
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

@Transactional
@Slf4j
@Service
public class AuthService {
    private final String KAKAO_API_URL = "https://kapi.kakao.com/v2/user/me";
    private final String GOOGLE_API_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
    private final String APPLE_API_URL = "https://appleid.apple.com/auth/keys";
    private final RestTemplate restTemplate;
    private final RefreshTokenService refreshTokenService;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;


    @Autowired
    public AuthService(RefreshTokenService refreshTokenService,
                       MemberRepository memberRepository,
                       JwtProvider jwtProvider) {
        this.restTemplate = new RestTemplate();
        this.refreshTokenService = refreshTokenService;
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }

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

    public JwtResponseDto login(Oauth2Provider oauth2Provider, String oauth2AccessToken) {
        return loginByOauthProvider(oauth2Provider, oauth2AccessToken);
    }

    private void loginByEmailAndPassword(String email, String password) {
        return;
    }

    private JwtResponseDto loginByOauthProvider(Oauth2Provider oauth2Provider, String oauth2AccessToken) {
        String email;

        try {
            email = extractEmailFromOauth2AccessToken(oauth2Provider, oauth2AccessToken);
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

        throw new RuntimeException("email == null");
    }

    private String extractEmailFromOauth2AccessToken(Oauth2Provider oauth2Provider,
                                                     String oauth2AccessToken)
            throws ParseException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(oauth2AccessToken);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        JSONParser jsonParser = new JSONParser();
        String email = null;

        if (Oauth2Provider.GOOGLE.equals(oauth2Provider)) {
            ResponseEntity<String> res = restTemplate.exchange(
                    GOOGLE_API_URL, HttpMethod.GET, httpEntity, String.class);
            log.info("구글에서 온 토큰: {}", res.getBody());
            JSONObject jsonObject = (JSONObject) jsonParser.parse(res.getBody());
            email = (String) jsonObject.get("email");
        }

        if (Oauth2Provider.KAKAO.equals(oauth2Provider)) {
            ResponseEntity<String> res = restTemplate.exchange(KAKAO_API_URL, HttpMethod.GET, httpEntity, String.class);
            log.info("카카오에서 온 토큰: {}", res.getBody());
            JSONObject jsonObject = (JSONObject) jsonParser.parse(res.getBody());
            JSONObject kakaoAccountJsonObject = (JSONObject) jsonObject.get("kakao_account");
            email = (String) kakaoAccountJsonObject.get("email");
        }

        if (Oauth2Provider.APPLE.equals(oauth2Provider)) {
            ;
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
