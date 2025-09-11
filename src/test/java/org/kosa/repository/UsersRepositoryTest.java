package org.kosa.repository;

import org.junit.jupiter.api.Test;
import org.kosa.entity.Users;
import org.kosa.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UsersRepositoryTest {
    @Autowired
    UsersRepository usersRepository;


    @Test
    void findByUserId() {
        Users cUsers = new Users();
        cUsers.setName("test");
        cUsers.setAddress("테스트");
        cUsers.setEmail("test@test.com");
        cUsers.setRole(UserRole.ROLE_ADMIN);
        cUsers.setUsername("test");
        usersRepository.save(cUsers);
        Users fUsers = usersRepository.findByUserId(1L).orElseThrow();
        System.out.println(fUsers);
    }

}