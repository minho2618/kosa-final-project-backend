package org.kosa.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.dto.question.QuestionReq;
import org.kosa.dto.question.QuestionRes;
import org.kosa.entity.Question;
import org.kosa.entity.QuestionAnswer;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.repository.QuestionAnswerRepository;
import org.kosa.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final QuestionAnswerRepository questionAnswerRepository;

    @Transactional
    public QuestionRes createQuestion(QuestionReq req){
        Question rQuestion = QuestionReq.toQuestion(req);
        Question cQuestion = questionRepository.save(rQuestion);
        return QuestionRes.toQuestionRes(cQuestion);
    }

    @Transactional
    public QuestionRes updateQuestion(Long questionId ,QuestionReq req){
        Question uQuestion = questionRepository.findByQuestionId(questionId).orElseThrow(()->
                new RecordNotFoundException("찾을 수 없는 질문입니다.", "Not Found Question")
        );
        uQuestion.setTitle(req.getTitle());
        uQuestion.setContent(req.getContent());
        return QuestionRes.toQuestionRes(uQuestion);
    }

    @Transactional
    public void deleteQuestion(Long questionId){
        questionAnswerRepository.deleteByQuestionId(questionId);
        questionRepository.deleteById(questionId);
    }

    public Page<QuestionRes> findAllQuestion(Pageable pageable){
        Page<Question> list = questionRepository.findAllQuestion(pageable);
        return list.map(QuestionRes::toQuestionRes);
    }

    public QuestionRes findByQuestionId(Long questionId) {
        Question question = questionRepository.findByQuestionId(questionId).orElseThrow(()->
                new RecordNotFoundException("찾을 수 없는 질문입니다.", "Not Found Question")
        );
        return QuestionRes.toQuestionRes(question);
    }

    public Page<QuestionRes> findByMemberId(Long memberId, Pageable pageable){
        Page<Question> list = questionRepository.findByMemberId(memberId, pageable);
        return list.map(QuestionRes::toQuestionRes);
    }

    public Page<QuestionRes> findByTitle(String title, Pageable pageable) {
        Page<Question> list = questionRepository.findByTitle(title, pageable);
        return list.map(QuestionRes::toQuestionRes);
    }
}
