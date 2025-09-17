package org.kosa.controller;

package org.kosa.service;

import lombok.RequiredArgsConstructor;
import org.kosa.dto.productQuestion.ProductQuestionReq;
import org.kosa.entity.*;
import org.kosa.enums.ProductQuestionsStatus;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.repository.ProductQuestionRepository;
import org.kosa.service.ProductQuestionAnswerService;
import org.kosa.service.ProductQuestionPhotoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductQuestionController {

    private final ProductQuestionRepository productQuestionRepository;

    private final ProductQuestionPhotoService productQuestionPhotoService;
    private final ProductQuestionAnswerService productQuestionAnswerService;


    @Transactional
    public Long createProductQuestion(ProductQuestionReq productQuestionReq) {
        // ToDo: 상품 존재하는 지 확인
        // Product product =

        ProductQuestion productQuestion = productQuestionReq.toEntity();
        ProductQuestion savedProductQuestion = productQuestionRepository.save(productQuestion);
        return savedProductQuestion.getQuestionId();
    }

    @Transactional(readOnly = true)
    public List<ProductQuestion> findByProduct(Product product) {
        // ToDo: 상품 존재하는 지 확인
        // Product product =

        return productQuestionRepository.findByProduct(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductQuestion> findByProduct(Product product, Pageable pageable) {
        return productQuestionRepository.findByProduct(product, pageable);
    }

@Transactional(readOnly = true)
    public List<ProductQuestion> findByMember(Member member) {
        return productQuestionRepository.findByMember(member);
    }


    @Transactional(readOnly = true)
    public List<ProductQuestion> findByStatus(ProductQuestionsStatus status) {
        return productQuestionRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<ProductQuestion> findByUpdatedAtAfter(LocalDateTime date) {
        return productQuestionRepository.findByUpdatedAtAfter(date);
    }

    @Transactional(readOnly = true)
    public List<ProductQuestion> findPendingQuestions(Product product, ProductQuestionsStatus status) {
        return productQuestionRepository.findPendingQuestions(product, status);
    }

    @Transactional
    public int updateStatusByIds(List<Long> ids, ProductQuestionsStatus status) {
        return productQuestionRepository.updateStatusByIds(ids, status);
    }

    @Transactional(readOnly = true)
    public ProductQuestion findByIdWithDetails(Long id) {
        return productQuestionRepository.findByIdWithDetails(id);
    }

    @Transactional
    public void updateProductQuestion(Long questionId, ProductQuestionReq productQuestionReq) {
        ProductQuestion productQuestion = productQuestionRepository.findById(questionId)
                .orElseThrow(() -> new RecordNotFoundException("해당하는 상품문의가 존재하지 않습니다.", "NO PRODUCT-QUESTION"));

        productQuestion.setContent(productQuestionReq.getContent());
        productQuestion.setStatus(productQuestionReq.getStatus());

        productQuestionRepository.save(productQuestion);
    }

    @Transactional
    public void deleteProductQuestion(Long questionId) {
        // 1. 답변 제거(있다면)
        ProductQuestionAnswer answer = productQuestionAnswerService.findByProductQuestionId(questionId);
        if (answer != null) {
            productQuestionAnswerService.deleteProductQuestionAnswer(answer.getAnswerId());
        }

        // 2. 사진 제거(있다면)
        List<ProductQuestionPhoto> productQuestionPhotoList =
                productQuestionPhotoService.findByProductQuestionOrderBySortOrder(this.findByIdWithDetails(questionId));
        productQuestionPhotoList
                .forEach((p) -> {
                    productQuestionPhotoService.deleteProductQuestionPhoto(p.getPhotoId());
                });

        productQuestionRepository.deleteById(questionId);
    }
}
