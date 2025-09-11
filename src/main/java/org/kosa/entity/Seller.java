package org.kosa.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.kosa.enums.SellerRole;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
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

    @Enumerated(EnumType.STRING)
    private SellerRole role;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private Users user;

    @Override
    public String toString() {
        return "Seller{" +
                "createdAt=" + createdAt +
                ", country='" + country + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", sellerName='" + sellerName + '\'' +
                ", sellerIntro='" + sellerIntro + '\'' +
                ", sellerRegNo='" + sellerRegNo + '\'' +
                ", sellerAddress='" + sellerAddress + '\'' +
                '}';
    }
}
