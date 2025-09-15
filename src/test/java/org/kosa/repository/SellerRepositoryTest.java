package org.kosa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.entity.Member;
import org.kosa.entity.Seller;
import org.kosa.entity.Member;
import org.kosa.enums.MemberRole;
import org.kosa.enums.SellerRole;
import org.kosa.enums.MemberRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class SellerRepositoryTest {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("판매자 정보 저장 및 조회 테스트")
    void saveAndFindSeller() {
        // given
        Member newUser = new Member();
        newUser.setUsername("seller_user");
        newUser.setEmail("seller@example.com");
        newUser.setPassword("password");
        newUser.setName("판매자");
        newUser.setRole(MemberRole.ROLE_SELLER);
        Member savedUser = memberRepository.save(newUser);

        Seller newSeller = new Seller();
        newSeller.setMember(savedUser);
        newSeller.setSellerName("행복 농장");
        newSeller.setSellerRegNo("123-45-67890");
        newSeller.setRole(SellerRole.authenticated);

        // when
        Seller savedSeller = sellerRepository.save(newSeller);
        Seller foundSeller = sellerRepository.findById(savedSeller.getMemberId()).orElse(null);

        // then
        assertThat(foundSeller).isNotNull();
        assertThat(foundSeller.getSellerName()).isEqualTo("행복 농장");
        assertThat(foundSeller.getMember().getUsername()).isEqualTo("seller_user");
        System.out.println("Found Seller: " + foundSeller);
    }
}
