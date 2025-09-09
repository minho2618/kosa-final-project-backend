package org.kosa.security;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kosa.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

// UserDetails를 커스터마이징
@Slf4j
public class CustomUserDetails implements UserDetails {
    @Getter
    public final User user;

    public CustomUserDetails(User user) {
        this.user = user;
        log.info("CustomMemberDetails ===> {}", user);
    }

    // 인증된 사용자의 Role정보 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        log.info("getAuthorities ===>");
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(()->user.getRole().name());
        return collection;
    }

    @Override
    public String getPassword() {
        log.info("getPassword ===>");
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        log.info("getUsername ===>");
        return user.getUsername();
    }
}
