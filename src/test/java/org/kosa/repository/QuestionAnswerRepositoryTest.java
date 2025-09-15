package org.kosa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.entity.Question;
import org.kosa.entity.QuestionAnswer;
import org.kosa.entity.Member;
import org.kosa.enums.QuestionStatus;
import org.kosa.enums.MemberRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class QuestionAnswerRepositoryTest {

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Question testQuestion;

    @BeforeEach
    void setUp() {
        Member member = new Member();
        member.setUsername("questioner");
        member.setEmail("questioner@example.com");
        member.setRole(MemberRole.ROLE_CUSTOMER);
        Member savedUser = memberRepository.save(member);

        Question question = new Question();
        question.setMember(savedUser);
        question.setTitle("질문 있습니다!");
        question.setContent("이것이 궁금합니다.");
        question.setStatus(QuestionStatus.PENDING);
        testQuestion = questionRepository.save(question);
    }

    @Test
    @DisplayName("질문 답변 저장 및 조회 테스트")
    void saveAndFindQuestionAnswer() {
        // given
        QuestionAnswer newAnswer = new QuestionAnswer();
        newAnswer.setQuestion(testQuestion);
        newAnswer.setContent("이것은 답변입니다.");

        // when
        QuestionAnswer savedAnswer = questionAnswerRepository.save(newAnswer);
        QuestionAnswer foundAnswer = questionAnswerRepository.findById(savedAnswer.getAnswerId()).orElse(null);

        // then
        assertThat(foundAnswer).isNotNull();
        assertThat(foundAnswer.getContent()).isEqualTo("이것은 답변입니다.");
        assertThat(foundAnswer.getQuestion().getTitle()).isEqualTo("질문 있습니다!");
        System.out.println("Found Answer: " + foundAnswer);
    }

    @Test
    @DisplayName("질문 ID로 답변 조회 테스트")
    void findByQuestionId() {
        // given
        QuestionAnswer newAnswer1 = new QuestionAnswer();
        newAnswer1.setQuestion(testQuestion);
        newAnswer1.setContent("첫 번째 답변입니다.");
        questionAnswerRepository.save(newAnswer1);

        // when
        List<QuestionAnswer> answers = questionAnswerRepository.findByQuestionId(testQuestion.getQuestionId());

        // then
        assertThat(answers).isNotNull();
        assertThat(answers.size()).isEqualTo(1);
        assertThat(answers.get(0).getContent()).isEqualTo("첫 번째 답변입니다.");
        answers.forEach(System.out::println);
    }
}
