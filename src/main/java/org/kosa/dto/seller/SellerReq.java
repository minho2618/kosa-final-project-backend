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
    private String sellerRegNo;
    private String sellerAddress;
    private String postalCode;
    private String country;
    private SellerRole role;

}
