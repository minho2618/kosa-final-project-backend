package org.kosa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.entity.*;
import org.kosa.enums.ProductCategory;
import org.kosa.enums.SellerRole;
import org.kosa.enums.MemberRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ReviewPhotoRepositoryTest {

    @Autowired
    private ReviewPhotoRepository reviewPhotoRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ProductRepository productRepository;

    private Review testReview;

    @BeforeEach
    void setUp() {
        Member member = new Member();
        member.setUsername("photo_reviewer");
        member.setEmail("photoreviewer@example.com");
        member.setRole(MemberRole.ROLE_CUSTOMER);
        Member savedMember = memberRepository.save(member);

        Member sellerMember = new Member();
        sellerMember.setUsername("photo_seller");
        sellerMember.setEmail("photoseller@example.com");
        sellerMember.setRole(MemberRole.ROLE_SELLER);
        Member savedSellerMember = memberRepository.save(sellerMember);

        Seller seller = new Seller();
        seller.setMember(savedSellerMember);
        seller.setSellerName("포토 농장");
        seller.setRole(SellerRole.authenticated);
        Seller savedSeller = sellerRepository.save(seller);

        Product product = new Product();
        product.setName("포토 사과");
        product.setPrice(new BigDecimal("20000"));
        product.setCategory(ProductCategory.과일);
        product.setSeller(savedSeller);
        product.setIsActive(true);
        Product savedProduct = productRepository.save(product);

        Review review = new Review();
        review.setMember(savedMember);
        review.setProduct(savedProduct);
        review.setRating(5L);
        review.setContent("사진 리뷰입니다!");
        testReview = reviewRepository.save(review);
    }

    @Test
    @DisplayName("리뷰 사진 저장 및 조회 테스트")
    void saveAndFindReviewPhoto() {
        // given
        ReviewPhoto newPhoto = new ReviewPhoto();
        newPhoto.setReview(testReview);
        newPhoto.setUrl("https://example.com/photo.jpg");
        newPhoto.setSortOrder(1);

        // when
        ReviewPhoto savedPhoto = reviewPhotoRepository.save(newPhoto);
        ReviewPhoto foundPhoto = reviewPhotoRepository.findById(savedPhoto.getPhotoId()).orElse(null);

        // then
        assertThat(foundPhoto).isNotNull();
        assertThat(foundPhoto.getUrl()).isEqualTo("https://example.com/photo.jpg");
        assertThat(foundPhoto.getReview().getContent()).isEqualTo("사진 리뷰입니다!");
        System.out.println("Found Review Photo: " + foundPhoto);
    }
}
