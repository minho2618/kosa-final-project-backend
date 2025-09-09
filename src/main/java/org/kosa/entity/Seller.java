package org.kosa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Slf4j
public class Seller {
    @Id
    private Long userId;

    private String sellerName;

    @Column(length = 500)
    private String sellerIntro;

    @Column(length = 64)
    private String sellerRegNo;

    private String sellerAddress;

    @Column(length = 20)
    private String postalCode;
    @Column(length = 2)
    private String country;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Override
    public String toString() {
        return "Seller{" +
                "createdAt=" + createdAt +
                ", country='" + country + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", userId=" + userId +
                ", sellerName='" + sellerName + '\'' +
                ", sellerIntro='" + sellerIntro + '\'' +
                ", sellerRegNo='" + sellerRegNo + '\'' +
                ", sellerAddress='" + sellerAddress + '\'' +
                '}';
    }
}
