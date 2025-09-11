package org.kosa.repository;

import org.kosa.entity.Member;
import org.kosa.enums.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberId(Long id);

    List<Member> findByRole(MemberRole role);

    Member findByEmail(String email);

}
