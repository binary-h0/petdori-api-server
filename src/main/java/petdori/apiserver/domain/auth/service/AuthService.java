package petdori.apiserver.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import petdori.apiserver.domain.auth.dto.MemberRegisterDto;
import petdori.apiserver.domain.auth.dto.request.SignupRequestDto;
import petdori.apiserver.domain.auth.dto.response.MemberProfileResponseDto;
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
import petdori.apiserver.global.common.S3Uploader;

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
    private final S3Uploader s3Uploader;

    public JwtResponseDto signup(Oauth2Provider oauth2Provider, MultipartFile profileImage,
                                 String email, String name) {
        // 클라이언트가 프로필 이미지를 첨부하지 않았을 경우에 대한 처리
        String profileImageUrl = profileImage == null || profileImage.isEmpty() ? null : s3Uploader.uploadProfileImage(profileImage);
        Member member = Member.from(oauth2Provider, profileImageUrl, email, name);
        memberRepository.save(member);

        PetdoriMemberDetails userDetails = new PetdoriMemberDetails(member);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, "", userDetails.getAuthorities());

        refreshTokenService.deleteRefreshToken(member.getEmail());
        return generateJwtResponse(authentication);
    }

    public JwtResponseDto login(Oauth2Provider oauth2Provider, String oauth2Token) {
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

    public MemberProfileResponseDto getMemberProfile() {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new MemberNotExistException(memberEmail));

        return MemberProfileResponseDto.builder()
                .name(member.getName())
                .email(member.getEmail())
                .provider(member.getOauth2Provider().name())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
