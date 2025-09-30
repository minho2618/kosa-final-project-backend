package org.kosa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.config.TestConfig;
import org.kosa.entity.Member;
import org.kosa.entity.Seller;
import org.kosa.enums.MemberRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestConfig
// @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class SellerRepositoryTest {

    @Autowired
    private SellerRepository sellerRepository;
    private Member testMember;
    private Seller testSeller;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        sellerRepository.deleteAll();

        testMember = Member.builder()
                .email("test@example.com")
                .password("encodedPassword123")
                .phoneNum("010-1234-5678")
                .role(MemberRole.ROLE_SELLER)
                .address("Test Address 123")
                .name("Test User")
                .build();

        memberRepository.save(testMember);
        
        testSeller = Seller.builder()
                .sellerRegNo("123-45-67890")
                .sellerName("Test Company")
                .sellerAddress("Test Address 123")
                .sellerIntro("Test")
                .postalCode("12345")
                .country("한국")
                .member(testMember)
                .build();
    }

    @Test
    @DisplayName("신규 판매자 저장 테스트")
    void testSaveSeller() {
        // Given
        Seller savedSeller = sellerRepository.save(testSeller);

        // When & Then
        assertThat(savedSeller.getMemberId()).isNotNull();
        assertThat(savedSeller.getSellerRegNo()).isEqualTo("123-45-67890");
        assertThat(savedSeller.getSellerName()).isEqualTo("Test Company");
        assertThat(savedSeller.getSellerAddress()).isEqualTo("Test Address 123");
        assertThat(savedSeller.getSellerIntro()).isEqualTo("Test");
        assertThat(savedSeller.getPostalCode()).isEqualTo("12345");
        assertThat(savedSeller.getCountry()).isEqualTo("한국");
    }

    @Test
    @DisplayName("판매자 ID로 조회 테스트")
    void testFindById() {
        // Given
        Seller savedSeller = sellerRepository.save(testSeller);

        // When
        Optional<Seller> foundSeller = sellerRepository.findById(savedSeller.getMemberId());

        // Then
        assertThat(foundSeller).isPresent();
        assertThat(foundSeller.get().getMemberId()).isEqualTo(savedSeller.getMemberId());
        assertThat(foundSeller.get().getSellerName()).isEqualTo("Test Company");
    }

    @Test
    @DisplayName("판매자 업데이트 테스트")
    void testUpdateSeller() {
        // Given
        Seller savedSeller = sellerRepository.save(testSeller);

        // When
        savedSeller.setSellerName("Updated Company Name");
        Seller updatedSeller = sellerRepository.save(savedSeller);

        // Then
        assertThat(updatedSeller.getSellerName()).isEqualTo("Updated Company Name");
    }

    @Test
    @DisplayName("판매자 존재 여부 확인 테스트")
    void testExistsById() {
        // Given
        Seller savedSeller = sellerRepository.save(testSeller);

        // When & Then
        boolean exists = sellerRepository.existsById(savedSeller.getMemberId());
        assertThat(exists).isTrue();

        boolean notExists = sellerRepository.existsById(999L);
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("모든 판매자 조회 테스트")
    void testFindAllSellers() {
        // Given
        Member member1 = Member.builder()
                .email("member1@example.com")
                .password("encodedPassword123")
                .phoneNum("010-1234-5678")
                .role(MemberRole.ROLE_CUSTOMER)
                .address("Test Address 123")
                .name("Test User")
                .build();

        Member member2 = Member.builder()
                .email("member2@example.com")
                .password("encodedPassword123")
                .phoneNum("010-1234-5678")
                .role(MemberRole.ROLE_CUSTOMER)
                .address("Test Address 123")
                .name("Test User")
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);

        Seller seller1 = Seller.builder()
                .sellerRegNo("111-11-11111")
                .sellerName("Company 1")
                .sellerAddress("Address 1")
                .member(member1)
                .build();
        
        Seller seller2 = Seller.builder()
                .sellerRegNo("222-22-22222")
                .sellerName("Company 2")
                .sellerAddress("Address 2")
                .member(member2)
                .build();

        sellerRepository.save(seller1);
        sellerRepository.save(seller2);

        // When
        Iterable<Seller> allSellers = sellerRepository.findAll();

        // Then
        assertThat(allSellers).hasSize(2);
        assertThat(allSellers)
                .extracting("sellerName")
                .containsExactlyInAnyOrder("Company 1", "Company 2");
    }
}