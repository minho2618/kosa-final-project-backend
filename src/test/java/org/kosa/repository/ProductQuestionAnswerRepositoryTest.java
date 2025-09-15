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
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ProductQuestionAnswerRepositoryTest {

    @Autowired private ProductQuestionAnswerRepository answerRepository;
    @Autowired private ProductQuestionRepository questionRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private SellerRepository sellerRepository;
    @Autowired private MemberRepository memberRepository;

    private Member testMember1;
    private Member testMember2;
    private ProductQuestion testQuestion1;

    @BeforeEach
    void setUp() {
        testMember1 = memberRepository.save(Member.builder().username("ans_user1").role(MemberRole.ROLE_CUSTOMER).build());
        testMember2 = memberRepository.save(Member.builder().username("ans_user2").role(MemberRole.ROLE_CUSTOMER).build());
        Member sellerMember = memberRepository.save(Member.builder().username("ans_seller").role(MemberRole.ROLE_SELLER).build());
        Seller seller = sellerRepository.save(Seller.builder().member(sellerMember).sellerName("답변 농장").role(SellerRole.authenticated).build());
        Product product = productRepository.save(Product.builder().name("답변 상품").seller(seller).category(ProductCategory.기타).isActive(true).price(BigDecimal.TEN).build());

        testQuestion1 = questionRepository.save(ProductQuestion.builder().product(product).member(testMember1).content("답변 받을 질문").status(ProductQuestionsStatus.OPEN).build());
        ProductQuestion testQuestion2 = questionRepository.save(ProductQuestion.builder().product(product).member(testMember2).content("다른 질문").status(ProductQuestionsStatus.OPEN).build());

        answerRepository.save(ProductQuestionAnswer.builder().productQuestionId(testQuestion1.getQuestionId()).member(sellerMember).content("첫번째 답변").build());
    }

    @Test
    @DisplayName("답변자로 답변 목록 조회")
    void findByUsers() {
        // when
        List<ProductQuestionAnswer> answers = answerRepository.findByMember(testMember1);
        // then
        assertThat(answers).isEmpty();

        // when
        List<ProductQuestionAnswer> sellerAnswers = answerRepository.findByMember(sellerRepository.findAll().get(0).getMember());
        // then
        assertThat(sellerAnswers).hasSize(1);
    }

    @Test
    @DisplayName("생성일 기간으로 답변 조회")
    void findByCreatedAtBetween() {
        // when
        List<ProductQuestionAnswer> answers = answerRepository.findByCreatedAtBetween(LocalDateTime.now().minusDays(1), LocalDateTime.now());

        // then
        assertThat(answers).hasSize(1);
        assertThat(answers.get(0).getContent()).isEqualTo("첫번째 답변");
    }
}
