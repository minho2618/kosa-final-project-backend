package org.kosa.controller;

import lombok.RequiredArgsConstructor;
import org.kosa.dto.productQuestionAnswer.ProductQuestionAnswerReq;
import org.kosa.dto.productQuestionAnswer.ProductQuestionAnswerRes;
import org.kosa.service.ProductQuestionAnswerService;
import org.kosa.service.ProductQuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product-question-answers")
@RequiredArgsConstructor
public class ProductQuestionAnswerController {

    private final ProductQuestionAnswerService productQuestionAnswerService;
    private final ProductQuestionService productQuestionService;

    @PostMapping("")
    public ResponseEntity<?> createProductQuestionAnswer(@RequestBody ProductQuestionAnswerReq req) {
        Long productQuestionAnswerId = productQuestionAnswerService.createProductQuestionAnswer(req);

        return ResponseEntity
                .status(201)
                .body(productQuestionAnswerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findByProductQuestion(@PathVariable Long id) {
        ProductQuestionAnswerRes res = productQuestionAnswerService.findByProductQuestionId(id);

        return ResponseEntity
                .status(202)
                .body(res);
    }

    /*@Transactional(readOnly = true)
    public ResponseEntity<?> findByProductQuestionId(Long productQuestionId) {


        return ResponseEntity
                .status(201)
                .body(productQuestionAnswerId);
    }*/

/*@Transactional(readOnly = true)
    public List<ProductQuestionAnswer> findByMember(Member member) {
        return productQuestionAnswerRepository.findByMember(member);
    }*//*


    */
/*@Transactional(readOnly = true)
    public List<ProductQuestionAnswer> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return productQuestionAnswerRepository.findByCreatedAtBetween(start, end);
    }*//*


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
    */
}
