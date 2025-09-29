package org.kosa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.config.TestConfig;
import org.kosa.entity.Member;
import org.kosa.entity.Question;
import org.kosa.enums.MemberRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestConfig
// @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;

    @BeforeEach
    void setUp() {
        questionRepository.deleteAll();
        memberRepository.deleteAll();
        
        testMember = Member.builder()
                .email("questioner@example.com")
                .password("password")
                .role(MemberRole.ROLE_CUSTOMER)
                .name("Test Questioner")
                .build();
        
        testMember = memberRepository.save(testMember);
    }

    @Test
    @DisplayName("신규 문의 저장 테스트")
    void testSaveQuestion() {
        // Given
        Question question = Question.builder()
                .member(testMember)
                .title("Test Question Title")
                .content("This is a test question content")
                .build();

        // When
        Question savedQuestion = questionRepository.save(question);

        // Then
        assertThat(savedQuestion.getQuestionId()).isNotNull();
        assertThat(savedQuestion.getTitle()).isEqualTo("Test Question Title");
        assertThat(savedQuestion.getContent()).isEqualTo("This is a test question content");
        assertThat(savedQuestion.getMember()).isEqualTo(testMember);
    }

    @Test
    @DisplayName("문의 ID로 조회 테스트")
    void testFindByQuestionId() {
        // Given
        Question question = Question.builder()
                .member(testMember)
                .title("Test Question")
                .content("Test content")
                .build();
        
        Question savedQuestion = questionRepository.save(question);

        // When
        Optional<Question> foundQuestion = questionRepository.findByQuestionId(savedQuestion.getQuestionId());

        // Then
        assertThat(foundQuestion).isPresent();
        assertThat(foundQuestion.get().getQuestionId()).isEqualTo(savedQuestion.getQuestionId());
        assertThat(foundQuestion.get().getTitle()).isEqualTo("Test Question");
    }

    @Test
    @DisplayName("모든 문의 조회 테스트")
    void testFindAllQuestion() {
        // Given
        Question question1 = Question.builder()
                .member(testMember)
                .title("Question 1")
                .content("Content 1")
                .build();
        
        Question question2 = Question.builder()
                .member(testMember)
                .title("Question 2")
                .content("Content 2")
                .build();

        questionRepository.save(question1);
        questionRepository.save(question2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Question> questions = questionRepository.findAllQuestion(pageable);

        // Then
        assertThat(questions).hasSize(2);
        assertThat(questions.getContent())
                .extracting("title")
                .containsExactlyInAnyOrder("Question 1", "Question 2");
    }

    @Test
    @DisplayName("회원 ID로 문의 조회 테스트")
    void testFindByMemberId() {
        // Given
        Question question = Question.builder()
                .member(testMember)
                .title("User Question")
                .content("User content")
                .build();

        questionRepository.save(question);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Question> questions = questionRepository.findByMemberId(testMember.getMemberId(), pageable);

        // Then
        assertThat(questions).hasSize(1);
        assertThat(questions.getContent().get(0).getMember().getMemberId()).isEqualTo(testMember.getMemberId());
    }

    @Test
    @DisplayName("제목으로 문의 검색 테스트")
    void testFindByTitle() {
        // Given
        Question question1 = Question.builder()
                .member(testMember)
                .title("How to use this product?")
                .content("Content 1")
                .build();
        
        Question question2 = Question.builder()
                .member(testMember)
                .title("Shipping information")
                .content("Content 2")
                .build();

        questionRepository.save(question1);
        questionRepository.save(question2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Question> questions = questionRepository.findByTitle("use", pageable);

        // Then
        assertThat(questions).hasSize(1);
        assertThat(questions.getContent().get(0).getTitle()).contains("use");
    }

    @Test
    @DisplayName("문의 업데이트 테스트")
    void testUpdateQuestion() {
        // Given
        Question question = Question.builder()
                .member(testMember)
                .title("Old Title")
                .content("Old content")
                .build();
        
        Question savedQuestion = questionRepository.save(question);

        // When
        savedQuestion.setTitle("Updated Title");
        savedQuestion.setContent("Updated content");
        Question updatedQuestion = questionRepository.save(savedQuestion);

        // Then
        assertThat(updatedQuestion.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedQuestion.getContent()).isEqualTo("Updated content");
    }
}