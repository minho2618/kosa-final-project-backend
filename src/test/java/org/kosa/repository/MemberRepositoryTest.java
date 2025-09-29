package org.kosa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.config.TestConfig;
import org.kosa.entity.Member;
import org.kosa.enums.MemberRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestConfig
// @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll(); // Clean up before each test
        
        testMember = Member.builder()
                .email("test@example.com")
                .password("encodedPassword123")
                .phoneNum("010-1234-5678")
                .role(MemberRole.ROLE_CUSTOMER)
                .address("Test Address 123")
                .name("Test User")
                .build();
    }

    @Test
    @DisplayName("신규 회원 저장 테스트")
    void testSaveMember() {
        // Given
        Member savedMember = memberRepository.save(testMember);

        // When & Then
        assertThat(savedMember.getMemberId()).isNotNull();
        assertThat(savedMember.getEmail()).isEqualTo("test@example.com");
        assertThat(savedMember.getPassword()).isEqualTo("encodedPassword123");
        assertThat(savedMember.getRole()).isEqualTo(MemberRole.ROLE_CUSTOMER);
    }

    @Test
    @DisplayName("이메일로 회원 조회 테스트")
    void testFindByEmail() {
        // Given
        Member savedMember = memberRepository.save(testMember);

        // When
        Member foundMember = memberRepository.findByEmail("test@example.com");

        // Then
        assertThat(foundMember).isNotNull();
        assertThat(foundMember.getEmail()).isEqualTo("test@example.com");
        assertThat(foundMember.getMemberId()).isEqualTo(savedMember.getMemberId());
    }

    @Test
    @DisplayName("이메일 중복 체크 테스트")
    void testDuplicateCheck() {
        // Given
        memberRepository.save(testMember);

        // When
        Member duplicateCheckResult = memberRepository.duplicateCheck("test@example.com");

        // Then
        assertThat(duplicateCheckResult).isNotNull();
        assertThat(duplicateCheckResult.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 테스트")
    void testExistsByEmail() {
        // Given
        memberRepository.save(testMember);

        // When & Then
        boolean exists = memberRepository.existsByEmail("test@example.com");
        assertTrue(exists);

        boolean notExists = memberRepository.existsByEmail("nonexistent@example.com");
        assertFalse(notExists);
    }

    @Test
    @DisplayName("멤버 ID로 회원 조회 테스트")
    void testFindByMemberId() {
        // Given
        Member savedMember = memberRepository.save(testMember);

        // When
        Optional<Member> foundMember = memberRepository.findByMemberId(savedMember.getMemberId());

        // Then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get()).isEqualTo(savedMember);
    }

    @Test
    @DisplayName("역할로 회원 조회 테스트")
    void testFindByRole() {
        // Given
        Member user = Member.builder()
                .email("user@example.com")
                .password("password")
                .role(MemberRole.ROLE_CUSTOMER)
                .name("User")
                .build();
        
        Member admin = Member.builder()
                .email("admin@example.com")
                .password("password")
                .role(MemberRole.ROLE_ADMIN)
                .name("Admin")
                .build();

        Member seller = Member.builder()
                .email("seller@example.com")
                .password("password")
                .role(MemberRole.ROLE_SELLER)
                .name("Seller")
                .build();
                
        memberRepository.save(user);
        memberRepository.save(admin);
        memberRepository.save(seller);

        // When
        List<Member> users = memberRepository.findByRole(MemberRole.ROLE_CUSTOMER);
        List<Member> admins = memberRepository.findByRole(MemberRole.ROLE_ADMIN);
        List<Member> sellers = memberRepository.findByRole(MemberRole.ROLE_SELLER);

        // Then
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("user@example.com");
        assertThat(admins).hasSize(1);
        assertThat(admins.get(0).getEmail()).isEqualTo("admin@example.com");
        assertThat(sellers).hasSize(1);
        assertThat(sellers.get(0).getEmail()).isEqualTo("seller@example.com");


    }

    @Test
    @DisplayName("전체 활성 회원 조회 테스트")
    void testFindAllMember() {
        // Given
        Member member1 = Member.builder()
                .email("user1@example.com")
                .password("password")
                .role(MemberRole.ROLE_CUSTOMER)
                .name("User 1")
                .build();
        
        Member member2 = Member.builder()
                .email("user2@example.com")
                .password("password")
                .role(MemberRole.ROLE_CUSTOMER)
                .name("User 2")
                .build();
                
        memberRepository.save(member1);
        memberRepository.save(member2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Member> members = memberRepository.findAllMember(pageable);

        // Then
        assertThat(members).hasSize(2);
        assertThat(members.getContent()).extracting("email")
                .containsExactlyInAnyOrder("user1@example.com", "user2@example.com");
    }

    @Test
    @DisplayName("회원 ID로 소프트 삭제 테스트")
    void testSoftDeleteById() {
        // Given
        Member savedMember = memberRepository.save(testMember);

        // When
        memberRepository.softDeleteById(savedMember.getMemberId());

        // Then
        Optional<Member> foundMember = memberRepository.findByMemberId(savedMember.getMemberId());
        assertThat(foundMember).isEmpty();
        
        // Check if the member exists in the soft deleted list
        Page<Member> deletedMembers = memberRepository.findAllDeletedMember(PageRequest.of(0, 10));
        assertThat(deletedMembers).hasSize(1);
        assertThat(deletedMembers.getContent().get(0).getRole()).isEqualTo(MemberRole.ROLE_DELETE);
    }
}