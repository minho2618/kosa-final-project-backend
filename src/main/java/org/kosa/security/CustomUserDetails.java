package org.kosa.security;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kosa.entity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

// UserDetails를 커스터마이징
@Slf4j
public class CustomUserDetails implements UserDetails {
    @Getter
    public final Users users;

    public CustomUserDetails(Users users) {
        this.users = users;
        log.info("CustomMemberDetails ===> {}", users);
    }

    // 인증된 사용자의 Role정보 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        log.info("getAuthorities ===>");
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(()-> users.getRole().name());
        return collection;
    }

    @Override
    public String getPassword() {
        log.info("getPassword ===>");
        return users.getPassword();
    }

    @Override
    public String getUsername() {
        log.info("getUsername ===>");
        return users.getUsername();
    }
}
