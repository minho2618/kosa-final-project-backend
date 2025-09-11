package org.kosa.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.entity.Users;
import org.kosa.repository.UsersRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//Repository의 함수를 호출...
@RequiredArgsConstructor
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UsersRepository usersRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("UserDetailsService loadUserByUsername() call... username {}", username);
        Users findUsers = usersRepository.findByEmail(username);
        if (findUsers != null) {
            log.info("findMember... 멤버 발견.. {}", findUsers);
            return new CustomUserDetails(findUsers);
        }
        return null;
    }
}
