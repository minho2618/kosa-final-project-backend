package org.kosa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.kosa.entity.Member;
import org.kosa.enums.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query(value = "select m from Member m where m.email= :email")
    Member duplicateCheck(String email);

    //Query Method... findBy로 시작... CamelCase
    Boolean existsByEmail(String email);


    @Query("SELECT m FROM Member m WHERE m.deletedAt IS NULL")
    Page<Member> findAllMember(Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.memberId = :id AND m.deletedAt IS NULL")
    Optional<Member> findByMemberId(@Param("id") Long id);

    @Query("SELECT m FROM Member m WHERE m.role = :role AND m.deletedAt IS NULL")
    List<Member> findByRole(@Param("role") MemberRole role);

    @Query("SELECT m FROM Member m WHERE m.email = :email AND m.deletedAt IS NULL")
    Member findByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.deletedAt = CURRENT_TIMESTAMP, m.role = org.kosa.enums.MemberRole.ROLE_DELETE, m.email = CONCAT(m.email, '_', FUNCTION('UUID')) WHERE m.memberId = :id")
    void softDeleteById(@Param("id") Long id);

    @Query("SELECT m FROM Member m WHERE m.deletedAt IS NOT NULL")
    Page<Member> findAllDeletedMember(Pageable pageable);
}