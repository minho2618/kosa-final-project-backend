package org.kosa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.kosa.enums.MemberProvider;
import org.kosa.enums.MemberRole;
import org.kosa.listener.MemberEntityListener;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@EntityListeners(MemberEntityListener.class)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(length = 100, unique = true, nullable = false)
    private String email;

    private String password;

    @Column(length = 20)
    private String phoneNum;

    private MemberRole role;

    private MemberProvider provider;

    @Column(length = 255)
    private String address;

    @Column(length = 20)
    private String name;
    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @Override
    public String toString() {
        return "Member{" +
                "memberId=" + memberId +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", role=" + role +
                ", provider=" + provider + '\'' +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
}