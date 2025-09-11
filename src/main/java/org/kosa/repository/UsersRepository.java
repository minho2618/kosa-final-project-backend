package org.kosa.repository;

import org.kosa.entity.Users;
import org.kosa.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUserId(Long id);
    List<Users> findByRole(UserRole role);
    Users findByEmail(String email);

}
