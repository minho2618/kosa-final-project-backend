package org.kosa.service;

import lombok.RequiredArgsConstructor;
import org.kosa.dto.member.MemberRes;
import org.kosa.dto.product.ProductRes;
import org.kosa.dto.productQuestion.ProductQuestionReq;
import org.kosa.dto.productQuestion.ProductQuestionRes;
import org.kosa.dto.productQuestionAnswer.ProductQuestionAnswerRes;
import org.kosa.entity.*;
import org.kosa.enums.ProductQuestionsStatus;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.repository.ProductQuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductQuestionService {

    private final ProductQuestionRepository productQuestionRepository;
    private final ProductQuestionPhotoService productQuestionPhotoService;
    private final ProductQuestionAnswerService productQuestionAnswerService;
    private final ProductService productService;
    private final MemberService memberService;


    @Transactional
    public Long createProductQuestion(ProductQuestionReq productQuestionReq) throws RecordNotFoundException {
        ProductRes productRes = productService.getProductDetail(productQuestionReq.getProductId());
        if (productRes == null) {
            throw new RecordNotFoundException("해당 상품이 존재하지 않습니다.", "NO PRODUCT");
        }

        ProductQuestion productQuestion = ProductQuestionReq.toProductQuestion(productQuestionReq);
        productQuestion.setStatus(ProductQuestionsStatus.OPEN);
        ProductQuestion savedProductQuestion = productQuestionRepository.save(productQuestion);
        return savedProductQuestion.getQuestionId();
    }

    public List<ProductQuestionRes> findByProduct(Long productId) {
        ProductRes productRes = productService.getProductDetail(productId);
        if (productRes == null) {
            throw new RecordNotFoundException("해당 상품이 존재하지 않습니다.", "NO PRODUCT");
        }
        Product product = Product.builder()
                .productId(productRes.getProductId())
                .build();

        return productQuestionRepository.findByProduct(product)
                .stream()
                .map(ProductQuestionRes::toProductQuestionRes)
                .collect(Collectors.toList());
    }

    public Page<ProductQuestion> findByProduct(Product product, Pageable pageable) {
        return productQuestionRepository.findByProduct(product, pageable);
    }

    public List<ProductQuestionRes> findByMember(Long memberId) {
        MemberRes memberRes = memberService.getMemberInfo(memberId);
        Member member = Member.builder()
                .memberId(memberRes.getMemberId())
                .build();

        List<ProductQuestion> pqList = productQuestionRepository.findByMember(member);

        return pqList.stream()
                .map(ProductQuestionRes::toProductQuestionRes)
                .collect(Collectors.toList());
    }

    public List<ProductQuestion> findByStatus(ProductQuestionsStatus status) {
        return productQuestionRepository.findByStatus(status);
    }

    public List<ProductQuestion> findByUpdatedAtAfter(LocalDateTime date) {
        return productQuestionRepository.findByUpdatedAtAfter(date);
    }

    public List<ProductQuestion> findPendingQuestions(Product product, ProductQuestionsStatus status) {
        return productQuestionRepository.findPendingQuestions(product, status);
    }

    /*@Transactional
    public int updateStatusByIds(List<Long> ids, ProductQuestionsStatus status) {
        return productQuestionRepository.updateStatusByIds(ids, status);
    }*/

    public ProductQuestionRes findByIdWithDetails(Long id) {
        ProductQuestion productQuestion = productQuestionRepository.findByIdWithDetails(id);
        return ProductQuestionRes.toProductQuestionRes(productQuestion);
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
        ProductQuestionAnswerRes answer = productQuestionAnswerService.findByProductQuestionId(questionId);
        if (answer != null) {
            productQuestionAnswerService.deleteProductQuestionAnswer(answer.getAnswerId());
        }
        
        // 2. 사진 제거(있다면)
        List<ProductQuestionPhoto> productQuestionPhotoList =
                productQuestionPhotoService.findByProductQuestionOrderBySortOrder(questionId);
        productQuestionPhotoList
                .forEach((p) -> {
                    productQuestionPhotoService.deleteProductQuestionPhoto(p.getPhotoId());
                });

        productQuestionRepository.deleteById(questionId);
    }
}