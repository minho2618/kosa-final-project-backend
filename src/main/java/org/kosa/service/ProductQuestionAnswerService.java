package org.kosa.service;

import lombok.RequiredArgsConstructor;
import org.kosa.dto.productQuestion.ProductQuestionRes;
import org.kosa.dto.productQuestionAnswer.ProductQuestionAnswerReq;
import org.kosa.dto.productQuestionAnswer.ProductQuestionAnswerRes;
import org.kosa.entity.Member;
import org.kosa.entity.ProductQuestion;
import org.kosa.entity.ProductQuestionAnswer;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.repository.ProductQuestionAnswerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductQuestionAnswerService {

    private final ProductQuestionAnswerRepository productQuestionAnswerRepository;

    @Transactional
    public Long createProductQuestionAnswer(ProductQuestionAnswerReq req) throws RecordNotFoundException {
        ProductQuestionAnswer productQuestionAnswer = ProductQuestionAnswerReq.toProductQuestionAnswer(req);

        ProductQuestionAnswer savedProductQuestionAnswer = productQuestionAnswerRepository.save(productQuestionAnswer);

        return savedProductQuestionAnswer.getAnswerId();
    }

    public ProductQuestionAnswer findByProductQuestion(ProductQuestion productQuestion) {
        return productQuestionAnswerRepository.findByProductQuestionId(productQuestion.getQuestionId());
    }

    public ProductQuestionAnswerRes findByProductQuestionId(Long productQuestionId) {
        ProductQuestionAnswer productQuestionAnswer = productQuestionAnswerRepository.findByProductQuestionId(productQuestionId);

        return ProductQuestionAnswerRes.toProductQuestionAnswerRes(productQuestionAnswer);
    }

    /*@Transactional(readOnly = true)
    public List<ProductQuestionAnswer> findByMember(Member member) {
        return productQuestionAnswerRepository.findByMember(member);
    }*/

    /*@Transactional(readOnly = true)
    public List<ProductQuestionAnswer> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return productQuestionAnswerRepository.findByCreatedAtBetween(start, end);
    }*/

    @Transactional
    public void updateProductQuestionAnswer(Long answerId, ProductQuestionAnswerReq productQuestionAnswerReq) {

        ProductQuestionAnswer productQuestionAnswer = productQuestionAnswerRepository.findById(answerId)
                .orElseThrow(() -> new RecordNotFoundException("해당하는 상품문의의 답변이 존재하지 않습니다.", "NO PRODUCT-QUESTION-ANSWER"));

        productQuestionAnswer.setContent(productQuestionAnswerReq.getContent());

        productQuestionAnswerRepository.save(productQuestionAnswer);
    }

    @Transactional
    public void deleteProductQuestionAnswer(Long answerId) {
        productQuestionAnswerRepository.deleteById(answerId);
    }
}