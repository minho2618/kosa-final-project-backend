package org.kosa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.kosa.enums.UserRole;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 50)
    private String username;

    @Column(length = 100)
    private String email;

    private String password;

    @Column(length = 20)
    private String phoneNum;

    private UserRole role;

    @Column(length = 255)
    private String address;

    @Column(length = 20)
    private String name;
    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                '}';
    }
}