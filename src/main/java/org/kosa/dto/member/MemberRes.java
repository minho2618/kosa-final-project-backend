package org.kosa.dto.member;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.kosa.entity.Member;
import org.kosa.enums.MemberRole;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Slf4j
@Builder
public class MemberRes {
    private Long memberId;
    private String email;
    private String phoneNum;
    private String address;
    private LocalDateTime createdAt;
    private String name;
    private String role;

    public static Member toMember(MemberRes memberRes) {
        return Member.builder()
                .memberId(memberRes.getMemberId())
                .email(memberRes.getEmail())
                .phoneNum(memberRes.getPhoneNum())
                .address(memberRes.getAddress())
                .name(memberRes.getName())
                .role(MemberRole.valueOf(memberRes.getRole()))
                .createdAt(memberRes.getCreatedAt())
                .build();
    }

    public static MemberRes toMemberRes(Member member){
        return MemberRes.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .phoneNum(member.getPhoneNum())
                .address(member.getAddress())
                .name(member.getName())
                .createdAt(member.getCreatedAt())
                .role(member.getRole().name())
                .build();
    }
}
