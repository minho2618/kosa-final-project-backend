package org.kosa.dto.seller;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.kosa.entity.Member;
import org.kosa.entity.Seller;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Slf4j
@Builder
public class SellerSignUpReq {
    private String country;
    private String postalCode;
    private String sellerAddress;
    private String sellerIntro;
    private String sellerName;
    private String sellerRegNo;

    public static SellerSignUpReq toSellerSignUpReq(SellerSignUpReq req){
        return SellerSignUpReq.builder()
                .country(req.getCountry())
                .postalCode(req.getPostalCode())
                .sellerAddress(req.getSellerAddress())
                .sellerIntro(req.getSellerIntro())
                .sellerName(req.getSellerName())
                .sellerRegNo(req.getSellerRegNo())
                .build();
    }
}
