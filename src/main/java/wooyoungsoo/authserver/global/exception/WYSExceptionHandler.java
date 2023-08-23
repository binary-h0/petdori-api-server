package wooyoungsoo.authserver.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooyoungsoo.authserver.domain.auth.exception.oauth2.Oauth2Exception;
import wooyoungsoo.authserver.domain.auth.exception.token.CustomJwtException;
import wooyoungsoo.authserver.global.common.BaseResponse;
import wooyoungsoo.authserver.domain.auth.dto.response.DogTypeNotExistResponseDto;
import wooyoungsoo.authserver.domain.auth.dto.response.MemberNotExistResponseDto;
import wooyoungsoo.authserver.domain.auth.entity.member.Oauth2Provider;
import wooyoungsoo.authserver.domain.auth.exception.dog.DogException;
import wooyoungsoo.authserver.domain.auth.exception.dog.DogTypeNotExistException;
import wooyoungsoo.authserver.domain.auth.exception.member.MemberAlreadyExistException;
import wooyoungsoo.authserver.domain.auth.exception.member.MemberException;
import wooyoungsoo.authserver.domain.auth.exception.member.MemberNotExistException;

@Slf4j
@ControllerAdvice
public class WYSExceptionHandler {
    @ExceptionHandler(MemberNotExistException.class)
    public ResponseEntity<BaseResponse<?>> handleMemberNotExistException(MemberNotExistException ex) {
        String email = ex.getEmail();
        MemberNotExistResponseDto memberNotExistResponseDto =
                MemberNotExistResponseDto.builder()
                .email(email)
                .build();

        return ResponseEntity.status(ex.getMemberErrorCode().getHttpStatus())
                .body(BaseResponse.createErrorResponse(
                        memberNotExistResponseDto,
                        ex.getMemberErrorCode().getErrorMessage()
                ));
    }

    @ExceptionHandler(MemberAlreadyExistException.class)
    public ResponseEntity<BaseResponse<?>> handleMemberAlreadyExistException(MemberAlreadyExistException ex) {
        Oauth2Provider oauth2Provider = ex.getOauth2Provider();
        String errorMessage = String.format(ex.getMemberErrorCode().getErrorMessage(),
                oauth2Provider.name());

        return ResponseEntity.status(ex.getMemberErrorCode().getHttpStatus())
                .body(BaseResponse.createErrorResponseWithNoContent(errorMessage));
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<BaseResponse<?>> handleMemberException(MemberException ex) {
        return ResponseEntity.status(ex.getMemberErrorCode().getHttpStatus())
                .body(BaseResponse.createErrorResponseWithNoContent(
                        ex.getMemberErrorCode().getErrorMessage()
                ));
    }

    @ExceptionHandler(DogTypeNotExistException.class)
    public ResponseEntity<BaseResponse<?>> handleDogTypeNotExistException(DogTypeNotExistException ex) {
        String typeName = ex.getTypeName();
        DogTypeNotExistResponseDto dogTypeNotExistResponseDto =
                DogTypeNotExistResponseDto.builder()
                .typeName(typeName)
                .build();

        return ResponseEntity.status(ex.getDogErrorCode().getHttpStatus())
                .body(BaseResponse.createErrorResponse(
                        dogTypeNotExistResponseDto,
                        ex.getDogErrorCode().getErrorMessage()
                ));
    }

    @ExceptionHandler(DogException.class)
    public ResponseEntity<BaseResponse<?>> handleDogException(DogException ex) {
        return ResponseEntity.status(ex.getDogErrorCode().getHttpStatus())
                .body(BaseResponse.createErrorResponseWithNoContent(
                        ex.getDogErrorCode().getErrorMessage()
                ));
    }

    @ExceptionHandler(CustomJwtException.class)
    public ResponseEntity<BaseResponse<?>> handleCustomJwtException(CustomJwtException ex) {
        return ResponseEntity.status(ex.getCustomJwtErrorCode().getHttpStatus())
                .body(BaseResponse.createErrorResponseWithNoContent(
                        ex.getCustomJwtErrorCode().getErrorMessage()
                ));
    }

    @ExceptionHandler(Oauth2Exception.class)
    public ResponseEntity<BaseResponse<?>> handleOauth2Exception(Oauth2Exception ex) {
        log.info("oauth2 관련 오류 : {}", ex.getOauth2ErrorCode().getErrorMessage());
        return ResponseEntity.status(ex.getOauth2ErrorCode().getHttpStatus())
                .body(BaseResponse.createErrorResponseWithNoContent(
                        "알 수 없는 이유로 로그인에 실패했습니다. 다시 시도해주세요"
                ));
    }
}
