package org.kosa.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.dto.questionAnswer.QuestionAnswerReq;
import org.kosa.dto.questionAnswer.QuestionAnswerRes;
import org.kosa.entity.Question;
import org.kosa.entity.QuestionAnswer;
import org.kosa.enums.QuestionStatus;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.repository.QuestionAnswerRepository;
import org.kosa.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionAnswerService {
    private final QuestionAnswerRepository questionAnswerRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public QuestionAnswerRes createReply(QuestionAnswerReq req){
        Question question = questionRepository.findByQuestionId(req.getQuestionId()).orElseThrow(()->
                new RecordNotFoundException("찾을 수 없는 질문입니다.", "Not Found Question")
                );
        question.setStatus(QuestionStatus.ANSWERED);
        QuestionAnswer rQuestionAnswer = QuestionAnswerReq.toQuestionAnswer(req, question);
        QuestionAnswer cQuestionAnswer = questionAnswerRepository.save(rQuestionAnswer);
        return QuestionAnswerRes.toQuestionAnswerRes(cQuestionAnswer);
    }

    @Transactional
    public QuestionAnswerRes updateReply(Long answerId, QuestionAnswerReq req){
        QuestionAnswer uQuestionAnswer = questionAnswerRepository.findByAnswerId(answerId).orElseThrow(()->
                        new RecordNotFoundException("찾을 수 없는 답변입니다.", "Not Found Answer")
                );
        uQuestionAnswer.setContent(req.getContent());
        return QuestionAnswerRes.toQuestionAnswerRes(uQuestionAnswer);
    }

    @Transactional
    public void deleteReply(Long answerId){
        questionAnswerRepository.deleteById(answerId);
    }

    public List<QuestionAnswerRes> findByQuestionId(Long questionId){
        List<QuestionAnswer> list = questionAnswerRepository.findByQuestionId(questionId);
        return list.stream()
                .map(QuestionAnswerRes::toQuestionAnswerRes)
                .collect(Collectors.toList());
    }

    public QuestionAnswerRes findByAnswerId(Long answerId){
        QuestionAnswer questionAnswer = questionAnswerRepository.findByAnswerId(answerId).orElseThrow(()->
            new RecordNotFoundException("찾을 수 없는 답변입니다.", "Not Found Answer")
        );
        return QuestionAnswerRes.toQuestionAnswerRes(questionAnswer);
    }
}
