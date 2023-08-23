package wooyoungsoo.authserver.domain.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
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

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

@Transactional
@Slf4j
@Service
public class AuthService {
    private final String KAKAO_API_URL = "https://kapi.kakao.com/v2/user/me";
    private final String GOOGLE_API_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
    private final String APPLE_PUBLIC_KEY_URL = "https://appleid.apple.com/auth/keys";
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
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(oauth2Token);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        JSONParser jsonParser = new JSONParser();
        String email = null;

        log.info("온 토큰 : {}", oauth2Token);
        if (Oauth2Provider.GOOGLE.equals(oauth2Provider)) {
            ResponseEntity<String> res = restTemplate.exchange(
                    GOOGLE_API_URL, HttpMethod.GET, httpEntity, String.class);
            log.info("구글에서 온 토큰: {}", res.getBody());
            JSONObject userInfo = (JSONObject) jsonParser.parse(res.getBody());
            email = (String) userInfo.get("email");
        }

        if (Oauth2Provider.KAKAO.equals(oauth2Provider)) {
            ResponseEntity<String> res = restTemplate.exchange(KAKAO_API_URL, HttpMethod.GET, httpEntity, String.class);
            log.info("카카오에서 온 토큰: {}", res.getBody());
            JSONObject userInfo = (JSONObject) jsonParser.parse(res.getBody());
            JSONObject kakaoAccountInfo = (JSONObject) userInfo.get("kakao_account");
            email = (String) kakaoAccountInfo.get("email");
        }

        if (Oauth2Provider.APPLE.equals(oauth2Provider)) {
            email = extractEmailFromAppleIdToken(oauth2Token);
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

    public String extractEmailFromAppleIdToken(String idToken) throws ParseException {
        Map<String, String> algAndKid = getAlgAndKidFromIdToken(idToken);
        String alg = algAndKid.get("alg");
        String kid = algAndKid.get("kid");

        HttpEntity<String> httpEntity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<String> res = restTemplate.exchange(
                APPLE_PUBLIC_KEY_URL, HttpMethod.GET, httpEntity, String.class);
        JSONObject availablePublicKeysContent = (JSONObject) new JSONParser().parse(res.getBody());
        JSONArray availablePublicKeys = (JSONArray) availablePublicKeysContent.get("keys");

        JSONObject applePublicKeyObj = null;
        for (JSONObject jeyObj : (Iterable<JSONObject>) availablePublicKeys) {
            String appleAlg = jeyObj.get("alg").toString();
            String appleKid = jeyObj.get("kid").toString();

            if (alg.equals(appleAlg) && kid.equals(appleKid)) {
                applePublicKeyObj = jeyObj;
                break;
            }
        }
        // TODO: applePublicKeyObj가 null이면 예외처리 필요

        PublicKey applePublicKey = generatePublicKey(applePublicKeyObj);
        Claims claims = parseClaims(idToken, applePublicKey);

        verifyAppleIdTokenClaim(claims);

        return claims.get("email").toString();
    }

    private Map<String, String> getAlgAndKidFromIdToken(String idToken) throws ParseException {
        Map<String, String> algAndKid = new HashMap<>();

        String header = idToken.split("\\.")[0];
        Base64.Decoder decoder = Base64.getUrlDecoder();
        JSONObject headerContent = (JSONObject) new JSONParser().parse(
                new String(decoder.decode(header))
        );
        // TODO: null이면 toString이 안되는거 예외처리 필요
        algAndKid.put("alg", headerContent.get("alg").toString());
        algAndKid.put("kid", headerContent.get("kid").toString());

        return algAndKid;
    }

    private PublicKey generatePublicKey(JSONObject applePublicKeyObj) {
        String kty = applePublicKeyObj.get("kty").toString();
        byte[] modulusBytes = Base64.getUrlDecoder().decode((String) applePublicKeyObj.get("n"));
        byte[] exponentBytes = Base64.getUrlDecoder().decode((String) applePublicKeyObj.get("e"));

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(
                new BigInteger(1, modulusBytes),
                new BigInteger(1, exponentBytes)
        );

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(kty);
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public Claims parseClaims(String idToken, PublicKey publicKey) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(idToken)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void verifyAppleIdTokenClaim(Claims claims) {
        if (!claims.getIssuer().equals("https://appleid.apple.com")) {
            throw new RuntimeException("invalid issuer");
        }

        if (!claims.getAudience().equals("com.example.wooyoungsoo")) {
            throw new RuntimeException("invalid audience");
        }
    }
}
