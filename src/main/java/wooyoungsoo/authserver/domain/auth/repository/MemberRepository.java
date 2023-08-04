package wooyoungsoo.authserver.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wooyoungsoo.authserver.domain.auth.entity.member.Member;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
}
