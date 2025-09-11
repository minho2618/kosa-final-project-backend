package org.kosa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.entity.*;
import org.kosa.enums.ProductCategory;
import org.kosa.enums.ProductQuestionsStatus;
import org.kosa.enums.SellerRole;
import org.kosa.enums.MemberRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ProductQuestionPhotoRepositoryTest {

    @Autowired private ProductQuestionPhotoRepository photoRepository;
    @Autowired private ProductQuestionRepository questionRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private SellerRepository sellerRepository;
    @Autowired private UsersRepository usersRepository;

    private ProductQuestion testQuestion;

    @BeforeEach
    void setUp() {
        Member user = usersRepository.save(Member.builder().username("photo_user").role(MemberRole.ROLE_CUSTOMER).build());
        Member sellerUser = usersRepository.save(Member.builder().username("photo_seller").role(MemberRole.ROLE_SELLER).build());
        Seller seller = sellerRepository.save(Seller.builder().member(sellerUser).memberId(sellerUser.getMemberId()).sellerName("사진 농장").role(SellerRole.authenticated).build());
        Product product = productRepository.save(Product.builder().name("사진 상품").seller(seller).category(ProductCategory.기타).isActive(true).price(BigDecimal.TEN).build());
        testQuestion = questionRepository.save(ProductQuestion.builder().product(product).member(user).content("사진 질문").status(ProductQuestionsStatus.OPEN).build());

        ProductQuestionPhoto photo1 = new ProductQuestionPhoto();
        photo1.setUrl("/photos/q1/1.jpg");
        photo1.setSortOrder(1);
        photoRepository.save(photo1);

        ProductQuestionPhoto photo2 = new ProductQuestionPhoto();
        photo2.setUrl("/photos/q1/2.jpg");
        photo2.setSortOrder(2);
        photoRepository.save(photo2);
    }

    @Test
    @DisplayName("URL로 사진 조회")
    void findByUrl() {
        // when
        ProductQuestionPhoto foundPhoto = photoRepository.findByUrl("/photos/q1/1.jpg");

        // then
        assertThat(foundPhoto).isNotNull();
        assertThat(foundPhoto.getSortOrder()).isEqualTo(1);
    }

    @Test
    @DisplayName("사진 정렬 순서 변경")
    void updateSortOrder() {
        // given
        ProductQuestionPhoto photo = photoRepository.findByUrl("/photos/q1/1.jpg");

        // when
        int updatedCount = photoRepository.updateSortOrder(photo.getPhotoId(), 5);
        ProductQuestionPhoto updatedPhoto = photoRepository.findById(photo.getPhotoId()).orElseThrow();

        // then
        assertThat(updatedCount).isEqualTo(1);
        assertThat(updatedPhoto.getSortOrder()).isEqualTo(5);
    }
}
