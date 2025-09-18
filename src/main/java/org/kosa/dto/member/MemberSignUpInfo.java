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
public class MemberSignUpInfo {
    private String email;
    private String password;
    private String name;
    private String phoneNum;
    private String address;

    public static Member toMember(MemberSignUpInfo req){
        return Member.builder()
                .email(req.getEmail())
                .name(req.getName())
                .phoneNum(req.getPhoneNum())
                .address(req.getAddress())
                .build();
    }
}
