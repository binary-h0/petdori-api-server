package petdori.apiserver.domain.auth.exception.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum MemberErrorCode {
    MEMBER_NOT_EXIST(HttpStatus.NOT_FOUND, "이메일에 해당하는 유저가 없습니다"),
    MEMBER_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 %s로 가입된 유저입니다");

    private HttpStatus httpStatus;
    private String errorMessage;
}
