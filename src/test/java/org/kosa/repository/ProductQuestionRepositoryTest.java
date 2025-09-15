package org.kosa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.entity.Member;
import org.kosa.entity.Product;
import org.kosa.entity.ProductQuestion;
import org.kosa.entity.Seller;
import org.kosa.enums.ProductCategory;
import org.kosa.enums.ProductQuestionsStatus;
import org.kosa.enums.SellerRole;
import org.kosa.enums.MemberRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ProductQuestionRepositoryTest {

    @Autowired
    private ProductQuestionRepository productQuestionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Product testProduct1;
    private Product testProduct2;
    private Member testMember1;
    private Member testMember2;

    @BeforeEach
    void setUp() {
        testMember1 = memberRepository.save(Member.builder().username("user1").email("user1@test.com").role(MemberRole.ROLE_CUSTOMER).build());
        testMember2 = memberRepository.save(Member.builder().username("user2").email("user2@test.com").role(MemberRole.ROLE_CUSTOMER).build());

        Member sellerUser = memberRepository.save(Member.builder().username("seller").email("seller@test.com").role(MemberRole.ROLE_SELLER).build());
        Seller seller = sellerRepository.save(Seller.builder().member(sellerUser).sellerName("테스트 농장").role(SellerRole.authenticated).build());

        testProduct1 = productRepository.save(Product.builder().name("테스트 상품 1").price(BigDecimal.TEN).category(ProductCategory.기타).seller(seller).isActive(true).build());
        testProduct2 = productRepository.save(Product.builder().name("테스트 상품 2").price(BigDecimal.ONE).category(ProductCategory.기타).seller(seller).isActive(true).build());

        productQuestionRepository.save(ProductQuestion.builder().product(testProduct1).member(testMember1).content("상품1에 대한 질문1").status(ProductQuestionsStatus.OPEN).build());
        productQuestionRepository.save(ProductQuestion.builder().product(testProduct1).member(testMember2).content("상품1에 대한 질문2").status(ProductQuestionsStatus.ANSWERED).build());
        productQuestionRepository.save(ProductQuestion.builder().product(testProduct2).member(testMember1).content("상품2에 대한 질문1").status(ProductQuestionsStatus.OPEN).build());
    }

    @Test
    @DisplayName("상품으로 질문 목록 조회")
    void findByProduct() {
        // when
        List<ProductQuestion> questions = productQuestionRepository.findByProduct(testProduct1);

        // then
        assertThat(questions).hasSize(2);
    }

    @Test
    @DisplayName("사용자로 질문 목록 조회")
    void findByUsers() {
        // when
        List<ProductQuestion> questions = productQuestionRepository.findByMember(testMember1);

        // then
        assertThat(questions).hasSize(2);
    }

    @Test
    @DisplayName("상태로 질문 목록 조회")
    void findByStatus() {
        // when
        List<ProductQuestion> pendingQuestions = productQuestionRepository.findByStatus(ProductQuestionsStatus.OPEN);
        List<ProductQuestion> answeredQuestions = productQuestionRepository.findByStatus(ProductQuestionsStatus.ANSWERED);

        // then
        assertThat(pendingQuestions).hasSize(2);
        assertThat(answeredQuestions).hasSize(1);
    }

    @Test
    @DisplayName("답변 대기 중인 질문 조회")
    void findPendingQuestions() {
        // when
        List<ProductQuestion> pendingQuestions = productQuestionRepository.findPendingQuestions(testProduct1, ProductQuestionsStatus.OPEN);

        // then
        assertThat(pendingQuestions).hasSize(1);
        assertThat(pendingQuestions.get(0).getContent()).isEqualTo("상품1에 대한 질문1");
    }

    @Test
    @DisplayName("질문 ID로 상세 정보 조회")
    void findByIdWithDetails() {
        // given
        ProductQuestion question = productQuestionRepository.findByMember(testMember1).get(0);

        // when
        ProductQuestion foundQuestion = productQuestionRepository.findByIdWithDetails(question.getQuestionId());

        // then
        assertThat(foundQuestion).isNotNull();
        assertThat(foundQuestion.getContent()).isEqualTo(question.getContent());
        assertThat(foundQuestion.getMember()).isNotNull();
        assertThat(foundQuestion.getProduct()).isNotNull();
    }
}
