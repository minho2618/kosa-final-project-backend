package org.kosa.security;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kosa.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

// UserDetails를 커스터마이징
@Slf4j
public class CustomMemberDetails implements UserDetails {
    @Getter
    public final Member member;

    public CustomMemberDetails(Member member) {
        this.member = member;
        log.info("CustomMemberDetails ===> {}", member);
    }

    // 인증된 사용자의 Role정보 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        log.info("getAuthorities ===>");
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(()-> member.getRole().name());
        return collection;
    }

    @Override
    public String getPassword() {
        log.info("getPassword ===>");
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        log.info("getUsername ===>");
        return member.getUsername();
    }
}
