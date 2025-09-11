package org.kosa.repository;

import org.kosa.entity.User;
import org.kosa.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(Long id);
    List<User> findByRole(UserRole role);
    User findByEmail(String email);

}
