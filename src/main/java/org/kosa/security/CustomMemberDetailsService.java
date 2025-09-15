package org.kosa.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.entity.Member;
import org.kosa.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//Repository의 함수를 호출...
@RequiredArgsConstructor
@Service
@Slf4j
public class CustomMemberDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("UserDetailsService loadUserByUsername() call... username {}", username);
        Member findMember = memberRepository.findByEmail(username);
        if (findMember != null) {
            log.info("findMember... 멤버 발견.. {}", findMember);
            return new CustomMemberDetails(findMember);
        }
        return null;
    }
}
