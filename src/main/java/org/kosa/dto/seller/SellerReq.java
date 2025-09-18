package org.kosa.dto.seller;

import lombok.*;
import org.kosa.entity.Seller;
import org.kosa.enums.SellerRole;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerReq {
    private Long memberId;
    private String sellerName;
    private String sellerIntro;
    private String sellerAddress;
    private String postalCode;
    private String country;

    public static Seller toSeller(SellerReq sellerReq){
        return Seller.builder()
                .memberId(sellerReq.getMemberId())
                .sellerName(sellerReq.getSellerName())
                .sellerIntro(sellerReq.getSellerIntro())
                .sellerAddress(sellerReq.getPostalCode())
                .postalCode(sellerReq.getPostalCode())
                .country(sellerReq.getCountry())
                .build();
    }

}
