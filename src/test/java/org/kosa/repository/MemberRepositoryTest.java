package org.kosa.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.entity.Member;
import org.kosa.enums.MemberRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;

    @BeforeEach
    void setUp() {
        usersRepository.deleteAll();

        Member seller = Member.builder()
                .username("user1")
                .email("user1@example.com")
                .role(MemberRole.ROLE_SELLER)
                .name("판매자")
                .build();
        usersRepository.save(seller);

        Member customer = Member.builder()
                .username("user1")
                .email("seller1@example.com")
                .role(MemberRole.ROLE_CUSTOMER)
                .name("일반사용자")
                .build();
        usersRepository.save(customer);

        Member admin = Member.builder()
                .username("admin1")
                .email("admin1@example.com")
                .role(MemberRole.ROLE_ADMIN)
                .name("관리자")
                .build();
        usersRepository.save(admin);
    }

    @Test
    @DisplayName("사용자 ID로 조회")
    void findByUserId() {
        // given
        Member userToFind = usersRepository.findByEmail("user1@example.com");
        log.info(userToFind.toString());

        // when
        Member foundUser = usersRepository.findByMemberId(userToFind.getMemberId()).orElse(null);
        log.info(foundUser.toString());

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("역할(Role)로 사용자 목록 조회")
    void findByRole() {
        // when
        List<Member> customer = usersRepository.findByRole(MemberRole.ROLE_CUSTOMER);
        List<Member> seller = usersRepository.findByRole(MemberRole.ROLE_SELLER);
        List<Member> admins = usersRepository.findByRole(MemberRole.ROLE_ADMIN);

        // then
        assertThat(customer).hasSize(1);
        assertThat(customer.get(0).getName()).isEqualTo("일반사용자");
        assertThat(seller).hasSize(1);
        assertThat(seller.get(0).getName()).isEqualTo("판매자");
        assertThat(admins).hasSize(1);
        assertThat(admins.get(0).getName()).isEqualTo("관리자");
    }

    @Test
    @DisplayName("이메일로 사용자 조회")
    void findByEmail() {
        // when
        Member foundUser = usersRepository.findByEmail("admin1@example.com");

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("admin1");
        assertThat(foundUser.getRole()).isEqualTo(MemberRole.ROLE_ADMIN);
    }

    @Test
    @DisplayName("사용자 정보 저장 및 확인")
    void saveUser() {
        // given
        Member newUser = Member.builder()
                .username("newUser")
                .email("new@example.com")
                .role(MemberRole.ROLE_CUSTOMER)
                .name("새사용자")
                .build();

        // when
        Member savedUser = usersRepository.save(newUser);
        Member foundUser = usersRepository.findById(savedUser.getMemberId()).orElse(null);
        // log.info(foundUser.toString());

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("new@example.com");
    }
}
