package org.kosa.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.entity.User;
import org.kosa.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//Repository의 함수를 호출...
@RequiredArgsConstructor
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("UserDetailsService loadUserByUsername() call... username {}", username);
        User findUser = userRepository.findByEmail(username);
        if (findUser != null) {
            log.info("findMember... 멤버 발견.. {}", findUser);
            return new CustomUserDetails(findUser);
        }
        return null;
    }
}
