package wooyoungsoo.authserver.domain.auth.exception.member;

import lombok.Getter;


@Getter
public class MemberException extends RuntimeException {
    private final MemberErrorCode memberErrorCode;

    public MemberException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode.getErrorMessage());
        this.memberErrorCode = memberErrorCode;
    }
}
