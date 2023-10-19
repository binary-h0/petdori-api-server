package petdori.apiserver.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import petdori.apiserver.domain.auth.dto.response.MemberProfileResponseDto;
import petdori.apiserver.domain.auth.service.AuthService;
import petdori.apiserver.global.common.BaseResponse;
import petdori.apiserver.global.common.HeaderUtil;
import petdori.apiserver.domain.auth.dto.request.Oauth2TokenDto;
import petdori.apiserver.domain.auth.dto.request.ReissueRequestDto;
import petdori.apiserver.domain.auth.dto.request.SignupRequestDto;
import petdori.apiserver.domain.auth.dto.response.JwtResponseDto;
import petdori.apiserver.domain.auth.entity.member.Oauth2Provider;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public BaseResponse<JwtResponseDto> signup(@RequestParam("provider") String oauth2ProviderName,
                                               @RequestParam(value = "profile_image", required = false) MultipartFile profileImage,
                                               @RequestParam(value = "email") String email,
                                               @RequestParam(value = "name") String name) {
        Oauth2Provider oauth2Provider =
                Oauth2Provider.getOauth2ProviderByName(oauth2ProviderName);

        JwtResponseDto jwtResponseDto = authService.signup(oauth2Provider, profileImage, email, name);

        return BaseResponse.createSuccessResponse(jwtResponseDto);
    }

    @PostMapping("/login")
    public BaseResponse<JwtResponseDto> login(@RequestParam("provider") String oauth2ProviderName,
                                              @RequestBody Oauth2TokenDto oauth2TokenDto) {
        Oauth2Provider oauth2Provider =
                Oauth2Provider.getOauth2ProviderByName(oauth2ProviderName);
        String oauth2Token = oauth2TokenDto.getOauth2Token();
        JwtResponseDto jwtResponseDto = authService.login(oauth2Provider, oauth2Token);
        return BaseResponse.createSuccessResponse(jwtResponseDto);
    }

    @PostMapping("/reissue")
    public BaseResponse<JwtResponseDto> reissue(HttpServletRequest request,
                                                @RequestBody ReissueRequestDto reissueRequestDto) {
        String accessToken = HeaderUtil.resolveTokenFromHeader(request);
        String refreshToken = reissueRequestDto.getRefreshToken();
        JwtResponseDto jwtResponseDto = authService.reIssue(accessToken, refreshToken);
        return BaseResponse.createSuccessResponse(jwtResponseDto);
    }

    @GetMapping("/profile")
    public BaseResponse<MemberProfileResponseDto> getProfile() {
        MemberProfileResponseDto memberProfileResponseDto = authService.getMemberProfile();
        return BaseResponse.createSuccessResponse(memberProfileResponseDto);
    }
}
