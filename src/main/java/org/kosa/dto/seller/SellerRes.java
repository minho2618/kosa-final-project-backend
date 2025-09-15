package org.kosa.dto.seller;

import lombok.*;
import org.kosa.entity.Seller;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerRes {
    private Long memberId;
    private String sellerName;
    private String sellerIntro;

    public static SellerRes from(Seller entity) {
        return SellerRes.builder()
                .memberId(entity.getMemberId())
                .sellerName(entity.getSellerName())
                .sellerIntro(entity.getSellerIntro())
                .build();
    }
}