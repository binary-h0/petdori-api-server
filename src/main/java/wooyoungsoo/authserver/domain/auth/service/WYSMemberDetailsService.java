package wooyoungsoo.authserver.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import wooyoungsoo.authserver.domain.auth.entity.member.WYSMemberDetails;
import wooyoungsoo.authserver.domain.auth.entity.member.Member;
import wooyoungsoo.authserver.domain.auth.exception.member.MemberNotExistException;
import wooyoungsoo.authserver.domain.auth.repository.MemberRepository;

@RequiredArgsConstructor
@Service
public class WYSMemberDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username).orElseThrow(
                () -> new MemberNotExistException(username)
        );
        return new WYSMemberDetails(member);
    }
}
