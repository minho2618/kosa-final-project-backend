package org.kosa.dto.signUp;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.kosa.dto.member.MemberSignUpInfo;
import org.kosa.dto.seller.SellerSignUpInfo;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Slf4j
public class SignUpReq {
    private MemberSignUpInfo memberSignUpInfo;
    private SellerSignUpInfo sellerSignUpInfo;
}
