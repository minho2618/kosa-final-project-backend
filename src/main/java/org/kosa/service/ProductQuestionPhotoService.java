package org.kosa.service;

import lombok.RequiredArgsConstructor;
import org.kosa.dto.productQuestion.ProductQuestionRes;
import org.kosa.entity.ProductQuestion;
import org.kosa.entity.ProductQuestionPhoto;
import org.kosa.repository.ProductQuestionPhotoRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductQuestionPhotoService {

    private final ProductQuestionPhotoRepository productQuestionPhotoRepository;
    private final ProductQuestionService productQuestionService;
    private final FileStorageService fileStorageService;

    // 상품 문의 모든 사진을 순서대로 가져오기
    public List<ProductQuestionPhoto> findByProductQuestionOrderBySortOrder(Long questionId) {
        return productQuestionPhotoRepository.findByProductQuestion_QuestionIdOrderBySortOrder(questionId);
    }

    // 사진 한 장 가져오기
    public ProductQuestionPhoto findByProductQuestionPhotoId(Long photoId) {
        return productQuestionPhotoRepository.findByPhotoId(photoId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사진입니다: " + photoId));
    }

    // 여러 사진 저장
    public List<ProductQuestionPhoto> savePhotos(Long questionId, List<MultipartFile> files) {
        ProductQuestionRes productQuestionRes = productQuestionService.findByIdWithDetails(questionId);
        ProductQuestion productQuestion = ProductQuestion.builder()
                .questionId(productQuestionRes.getQuestionId())
                .build();

        List<ProductQuestionPhoto> photos = new ArrayList<>();
        int currentMaxOrder = getCurrentMaxSortOrder(questionId);

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String filename = fileStorageService.storeFile(file);
            String url = "/api/images/" + filename;

            ProductQuestionPhoto photo = ProductQuestionPhoto.builder()
                    .url(url)
                    .sortOrder(currentMaxOrder + i + 1)
                    .productQuestion(productQuestion)
                    .build();

            photos.add(photo);
        }

        return productQuestionPhotoRepository.saveAll(photos);
    }

    // 단일 사진 저장
    public ProductQuestionPhoto savePhoto(Long questionId, MultipartFile file, Integer sortOrder) {
        ProductQuestionRes productQuestionRes = productQuestionService.findByIdWithDetails(questionId);
        ProductQuestion productQuestion = ProductQuestion.builder()
                .questionId(productQuestionRes.getQuestionId())
                .build();

        String filename = fileStorageService.storeFile(file);
        String url = "/api/images/" + filename;

        int order = sortOrder != null ? sortOrder : getCurrentMaxSortOrder(questionId) + 1;

        ProductQuestionPhoto photo = ProductQuestionPhoto.builder()
                .url(url)
                .sortOrder(order)
                .productQuestion(productQuestion)
                .build();

        return productQuestionPhotoRepository.save(photo);
    }

    // 사진 삭제 (파일 + DB)
    public boolean deletePhoto(Long photoId) {
        ProductQuestionPhoto photo = findByProductQuestionPhotoId(photoId);

        // 물리 파일 삭제
        String filename = fileStorageService.extractFilenameFromUrl(photo.getUrl());
        boolean fileDeleted = fileStorageService.deleteFile(filename);

        // DB에서 삭제
        productQuestionPhotoRepository.deleteById(photoId);

        return fileDeleted;
    }

    // 질문의 모든 사진 삭제
    public void deletePhotosByQuestion(Long questionId) {
        List<ProductQuestionPhoto> photos = findByProductQuestionOrderBySortOrder(questionId);

        // 물리 파일들 삭제
        for (ProductQuestionPhoto photo : photos) {
            String filename = fileStorageService.extractFilenameFromUrl(photo.getUrl());
            fileStorageService.deleteFile(filename);
        }

        // DB에서 삭제
        productQuestionPhotoRepository.deleteByProductQuestion_QuestionId(questionId);
    }

    // 정렬 순서 업데이트
    public int updateSortOrder(Long photoId, int sortOrder) {
        return productQuestionPhotoRepository.updateSortOrder(photoId, sortOrder);
    }

    // 이미지 파일 로드
    public Resource loadImage(String filename) {
        return fileStorageService.loadFile(filename);
    }

    private int getCurrentMaxSortOrder(Long questionId) {
        List<ProductQuestionPhoto> photos = findByProductQuestionOrderBySortOrder(questionId);
        return photos.isEmpty() ? 0 : photos.get(photos.size() - 1).getSortOrder();
    }
}