package org.kosa.dto.seller;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.kosa.entity.Seller;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Slf4j
@Builder
public class SellerSignUpInfo {
    private String sellerAddress;
    private String sellerIntro;
    private String sellerName;
    private String sellerRegNo;

    public static Seller toSeller(SellerSignUpInfo req){
        return Seller.builder()
                .sellerAddress(req.getSellerAddress())
                .sellerIntro(req.getSellerIntro())
                .sellerName(req.getSellerName())
                .sellerRegNo(req.getSellerRegNo())
                .build();
    }
}
