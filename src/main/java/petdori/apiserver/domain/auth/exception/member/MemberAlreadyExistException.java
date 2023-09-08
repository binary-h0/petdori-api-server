package petdori.apiserver.domain.auth.exception.member;

import lombok.Getter;
import petdori.apiserver.domain.auth.entity.member.Oauth2Provider;

@Getter
public class MemberAlreadyExistException extends MemberException {
    private Oauth2Provider oauth2Provider;

    public MemberAlreadyExistException(Oauth2Provider oauth2Provider) {
        super(MemberErrorCode.MEMBER_ALREADY_EXIST);
        this.oauth2Provider = oauth2Provider;
    }
}
