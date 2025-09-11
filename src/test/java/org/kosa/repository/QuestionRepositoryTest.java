package org.kosa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.entity.Question;
import org.kosa.entity.Users;
import org.kosa.enums.QuestionStatus;
import org.kosa.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UsersRepository usersRepository;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = usersRepository.save(Users.builder().username("question_user").email("q_user@test.com").role(UserRole.ROLE_CUSTOMER).build());

        questionRepository.save(Question.builder()
                .users(testUser)
                .title("첫 번째 질문")
                .content("내용1")
                .status(QuestionStatus.PENDING)
                .build());

        questionRepository.save(Question.builder()
                .users(testUser)
                .title("두 번째 질문")
                .content("내용2")
                .status(QuestionStatus.ANSWERED)
                .build());
    }

    @Test
    @DisplayName("질문 ID로 조회")
    void findByQuestionId() {
        // given
        Question question = questionRepository.findByTitle("첫 번째 질문").get(0);

        // when
        Question foundQuestion = questionRepository.findByQuestionId(question.getQuestionId()).orElseThrow();

        // then
        assertThat(foundQuestion).isNotNull();
        assertThat(foundQuestion.getTitle()).isEqualTo("첫 번째 질문");
    }

    @Test
    @DisplayName("사용자 ID로 질문 목록 조회")
    void findByUserId() {
        // when
        List<Question> questions = questionRepository.findByUserId(testUser.getUserId());

        // then
        assertThat(questions).hasSize(2);
    }

    @Test
    @DisplayName("제목으로 질문 조회")
    void findByTitle() {
        // when
        List<Question> questions = questionRepository.findByTitle("두 번째 질문");

        // then
        assertThat(questions).hasSize(1);
        assertThat(questions.get(0).getContent()).isEqualTo("내용2");
    }
}
