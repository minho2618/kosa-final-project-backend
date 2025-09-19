package org.kosa.dto.seller;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.kosa.entity.Seller;
import org.kosa.enums.SellerRole;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerRes {
    private Long memberId;
    private String sellerName;
    private String sellerIntro;
    private String sellerRegNo;
    private String sellerAddress;
    private String postalCode;
    private String country;
    private LocalDateTime createdAt;
    private String role;

    public static SellerRes toSellerRes(Seller entity) {
        return SellerRes.builder()
                .memberId(entity.getMemberId())
                .sellerName(entity.getSellerName())
                .sellerIntro(entity.getSellerIntro())
                .sellerRegNo(entity.getSellerRegNo())
                .sellerAddress(entity.getSellerAddress())
                .postalCode(entity.getPostalCode())
                .country(entity.getCountry())
                .createdAt(entity.getCreatedAt())
                .role(entity.getRole().name())
                .build();
    }
}
