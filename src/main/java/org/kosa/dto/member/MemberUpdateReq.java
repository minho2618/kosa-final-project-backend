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
public class MemberUpdateReq {
    private String name;
    private String phoneNum;
    private String email;
    private String address;

    public Member toMember(){
        return Member.builder()
                .name(name)
                .phoneNum(phoneNum)
                .email(email)
                .address(address)
                .build();
    }
}
