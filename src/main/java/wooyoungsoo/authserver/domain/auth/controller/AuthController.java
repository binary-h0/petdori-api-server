package wooyoungsoo.authserver.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wooyoungsoo.authserver.domain.auth.dto.request.Oauth2TokenDto;
import wooyoungsoo.authserver.global.common.BaseResponse;
import wooyoungsoo.authserver.domain.auth.dto.*;
import wooyoungsoo.authserver.domain.auth.dto.request.ReissueRequestDto;
import wooyoungsoo.authserver.domain.auth.dto.request.SignupRequestDto;
import wooyoungsoo.authserver.domain.auth.dto.response.JwtResponseDto;
import wooyoungsoo.authserver.domain.auth.entity.member.Oauth2Provider;
import wooyoungsoo.authserver.domain.auth.service.DogService;
import wooyoungsoo.authserver.domain.auth.service.AuthService;
import wooyoungsoo.authserver.global.common.HeaderUtil;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final DogService dogService;

    @PostMapping("/signup")
    public BaseResponse<JwtResponseDto> signup(@RequestParam("provider") String oauth2ProviderName,
                                               @RequestBody SignupRequestDto signupRequestDto) {
        Oauth2Provider oauth2Provider =
                Oauth2Provider.getOauth2ProviderByName(oauth2ProviderName);
        MemberRegisterDto memberRegisterDto = signupRequestDto.toMemberRegisterDto();
        DogRegisterDto dogRegisterDto = signupRequestDto.toDogRegisterDto();

        JwtResponseDto jwtResponseDto = authService.signup(oauth2Provider, memberRegisterDto);
        dogService.registerDog(dogRegisterDto);

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
}
