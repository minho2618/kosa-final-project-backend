package org.kosa.dto.member;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.kosa.entity.Member;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Slf4j
@Builder
public class SignUpReq {
    private String username;
    private String email;
    private String password;
    private String name;
    private String phoneNum;
    private String address;

    public Member toMember(){
        return Member.builder()
                .username(username)
                .email(email)
                .name(name)
                .phoneNum(phoneNum)
                .address(address)
                .build();
    }
}
