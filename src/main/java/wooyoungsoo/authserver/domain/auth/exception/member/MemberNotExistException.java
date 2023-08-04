package wooyoungsoo.authserver.domain.auth.exception.member;

import lombok.Getter;

@Getter
public class MemberNotExistException extends MemberException {
    private String email;
    public MemberNotExistException(String email) {
        super(MemberErrorCode.MEMBER_NOT_EXIST);
        this.email = email;
    }
}
