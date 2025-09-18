package org.kosa.service;

import lombok.RequiredArgsConstructor;
import org.kosa.entity.ProductQuestion;
import org.kosa.entity.ProductQuestionPhoto;
import org.kosa.repository.ProductQuestionPhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductQuestionPhotoService {

    private final ProductQuestionPhotoRepository productQuestionPhotoRepository;

    @Transactional
    public void saveProductQuestionPhotos(List<ProductQuestionPhoto> photos) {
        productQuestionPhotoRepository.saveAll(photos);
    }

    public List<ProductQuestionPhoto> findByProductQuestionOrderBySortOrder(Long questionId) {
        return productQuestionPhotoRepository.findByProductQuestionQuestionIdOrderBySortOrder(questionId);
    }

    /*@Transactional(readOnly = true)
    public ProductQuestionPhoto findByUrl(String url) {
        return productQuestionPhotoRepository.findByUrl(url);
    }*/

    /*@Transactional
    public void deleteByQuestionId(Long questionId) {
        // Assuming you will add `@Modifying @Query("DELETE FROM ProductQuestionPhoto p WHERE p.productQuestion.questionId = :questionId") void deleteByQuestionId(@Param("questionId") Long questionId);` to the repository
    }*/

    @Transactional
    public int updateSortOrder(Long photoId, int sortOrder) {
        return productQuestionPhotoRepository.updateSortOrder(photoId, sortOrder);
    }

    /*@Transactional(readOnly = true)
    public long countByProductQuestion(ProductQuestion question) {
        // Assuming you will add `long countByProductQuestion(ProductQuestion question);` to the repository
        return 0; // Placeholder
    }*/

    @Transactional
    public void deleteProductQuestionPhoto(Long photoId) {
        productQuestionPhotoRepository.deleteById(photoId);
    }
}