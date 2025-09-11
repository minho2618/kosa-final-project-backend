package org.kosa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.entity.*;
import org.kosa.enums.ProductCategory;
import org.kosa.enums.ProductQuestionsStatus;
import org.kosa.enums.SellerRole;
import org.kosa.enums.UserRole;
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
    @Autowired private UsersRepository usersRepository;

    private Users testUser1;
    private Users testUser2;
    private ProductQuestion testQuestion1;

    @BeforeEach
    void setUp() {
        testUser1 = usersRepository.save(Users.builder().username("ans_user1").role(UserRole.ROLE_CUSTOMER).build());
        testUser2 = usersRepository.save(Users.builder().username("ans_user2").role(UserRole.ROLE_CUSTOMER).build());
        Users sellerUser = usersRepository.save(Users.builder().username("ans_seller").role(UserRole.ROLE_SELLER).build());
        Seller seller = sellerRepository.save(Seller.builder().users(sellerUser).userId(sellerUser.getUserId()).sellerName("답변 농장").role(SellerRole.authenticated).build());
        Product product = productRepository.save(Product.builder().name("답변 상품").seller(seller).category(ProductCategory.기타).isActive(true).price(BigDecimal.TEN).build());

        testQuestion1 = questionRepository.save(ProductQuestion.builder().product(product).users(testUser1).content("답변 받을 질문").status(ProductQuestionsStatus.OPEN).build());
        ProductQuestion testQuestion2 = questionRepository.save(ProductQuestion.builder().product(product).users(testUser2).content("다른 질문").status(ProductQuestionsStatus.OPEN).build());

        answerRepository.save(ProductQuestionAnswer.builder().productQuestionId(testQuestion1.getQuestionId()).users(sellerUser).content("첫번째 답변").build());
        answerRepository.save(ProductQuestionAnswer.builder().productQuestionId(testQuestion2.getQuestionId()).users(sellerUser).content("두번째 답변").createdAt(LocalDateTime.now().minusDays(2)).build());
    }

    @Test
    @DisplayName("답변자로 답변 목록 조회")
    void findByUsers() {
        // when
        List<ProductQuestionAnswer> answers = answerRepository.findByUsers(testUser1);
        // then
        assertThat(answers).isEmpty();

        // when
        List<ProductQuestionAnswer> sellerAnswers = answerRepository.findByUsers(sellerRepository.findAll().get(0).getUsers());
        // then
        assertThat(sellerAnswers).hasSize(2);
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
