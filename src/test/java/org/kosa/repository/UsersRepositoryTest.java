package org.kosa.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.entity.Users;
import org.kosa.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
class UsersRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;

    @BeforeEach
    void setUp() {
        usersRepository.deleteAll();

        Users seller = Users.builder()
                .username("user1")
                .email("user1@example.com")
                .role(UserRole.ROLE_SELLER)
                .name("판매자")
                .build();
        usersRepository.save(seller);

        Users customer = Users.builder()
                .username("user1")
                .email("seller1@example.com")
                .role(UserRole.ROLE_CUSTOMER)
                .name("일반사용자")
                .build();
        usersRepository.save(customer);

        Users admin = Users.builder()
                .username("admin1")
                .email("admin1@example.com")
                .role(UserRole.ROLE_ADMIN)
                .name("관리자")
                .build();
        usersRepository.save(admin);
    }

    @Test
    @DisplayName("사용자 ID로 조회")
    void findByUserId() {
        // given
        Users userToFind = usersRepository.findByEmail("user1@example.com");
        log.info(userToFind.toString());

        // when
        Users foundUser = usersRepository.findByUserId(userToFind.getUserId()).orElse(null);
        log.info(foundUser.toString());

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("역할(Role)로 사용자 목록 조회")
    void findByRole() {
        // when
        List<Users> customer = usersRepository.findByRole(UserRole.ROLE_CUSTOMER);
        List<Users> seller = usersRepository.findByRole(UserRole.ROLE_SELLER);
        List<Users> admins = usersRepository.findByRole(UserRole.ROLE_ADMIN);

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
        Users foundUser = usersRepository.findByEmail("admin1@example.com");

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("admin1");
        assertThat(foundUser.getRole()).isEqualTo(UserRole.ROLE_ADMIN);
    }

    @Test
    @DisplayName("사용자 정보 저장 및 확인")
    void saveUser() {
        // given
        Users newUser = Users.builder()
                .username("newUser")
                .email("new@example.com")
                .role(UserRole.ROLE_CUSTOMER)
                .name("새사용자")
                .build();

        // when
        Users savedUser = usersRepository.save(newUser);
        Users foundUser = usersRepository.findById(savedUser.getUserId()).orElse(null);
        // log.info(foundUser.toString());

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("new@example.com");
    }
}
