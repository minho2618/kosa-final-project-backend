package org.kosa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.dto.review.ReviewReq;
import org.kosa.dto.reviewPhoto.ReviewPhotoRes;
import org.kosa.entity.Member;
import org.kosa.entity.Product;
import org.kosa.entity.Review;
import org.kosa.entity.ReviewPhoto;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.repository.MemberRepository;
import org.kosa.repository.ProductRepository;
import org.kosa.repository.ReviewPhotoRepository;
import org.kosa.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;

    // ================= 조회 =================

    @Transactional(readOnly = true)
    public Review get(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RecordNotFoundException("리뷰 없습니다: id=" + reviewId, "Not Found Review"));
    }

    @Transactional(readOnly = true)
    public List<Review> listByProduct(Long productId) {
        // 필요 시 ReviewRepository에 정렬 메서드 추가: findByProduct_ProductIdOrderByCreatedAtDesc(...)
        Product product = ensureProduct(productId);
        return reviewRepository.findAllByProduct(product);
    }

    // ================= 생성 =================

    @Transactional
    public Review create(ReviewReq req) {
        // 1) 연관 로딩
        Product product = ensureProduct(req.getProductId());
        Member member   = ensureMember(req.getMemberId());

        // 2) 본문 저장
        Review review = Review.builder()
                .product(product)
                .member(member)
                .rating(req.getRating())
                .content(req.getContent())
                .build(); // createdAt/updatedAt은 @Creation/@UpdateTimestamp 권장
        Review saved = reviewRepository.save(review);

        // 3) 사진 저장 (URL 목록이 온 경우)
        if (req.getPhotoUrls() != null && !req.getPhotoUrls().isEmpty()) {
            List<ReviewPhoto> photos = new ArrayList<>();
            int sort = 1;
            for (String url : req.getPhotoUrls()) {
                if (url == null || url.isBlank()) continue;
                photos.add(ReviewPhoto.builder()
                        .url(url)
                        .sortOrder(sort++)
                        .review(saved) // FK 세팅
                        .build());
            }
            if (!photos.isEmpty()) {
                reviewPhotoRepository.saveAll(photos);
                normalizeSort(saved.getReviewId()); // 혹시 중간 삽입을 대비해 보정
            }
        }

        log.info("리뷰 등록 완료: id={}, productId={}, memberId={}",
                saved.getReviewId(), product.getProductId(), member.getMemberId());
        return saved;
    }

    // ================= 수정 =================

    @Transactional
    public Review update(Long reviewId, ReviewReq req) {
        Review entity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RecordNotFoundException("리뷰 없습니다: id=" + reviewId, "Not Found Review"));

        if (req.getRating() != null)  entity.setRating(req.getRating());
        if (req.getContent() != null) entity.setContent(req.getContent());

        // 사진을 함께 교체하고 싶다면(선택):
        if (req.getPhotoUrls() != null) {
            // 기존 전부 삭제 후 다시 삽입 (간단/안전)
            reviewPhotoRepository.deleteByReview_ReviewId(reviewId);

            List<ReviewPhoto> batch = new ArrayList<>();
            int sort = 1;
            for (String url : req.getPhotoUrls()) {
                if (url == null || url.isBlank()) continue;
                batch.add(ReviewPhoto.builder()
                        .url(url)
                        .sortOrder(sort++)
                        .review(entity)
                        .build());
            }
            if (!batch.isEmpty()) {
                // 혹시 sort 값이 들어온다면 정렬하고 1..n 재부여
                batch.sort(Comparator.comparingInt(ReviewPhoto::getSortOrder));
                for (int i = 0; i < batch.size(); i++) batch.get(i).setSortOrder(i + 1);
                reviewPhotoRepository.saveAll(batch);
            }
        }

        // @Transactional 덕분에 더티체킹으로 UPDATE 반영
        return entity;
    }

    // ================= 삭제 =================

    @Transactional
    public void delete(Long reviewId) {
        Review entity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RecordNotFoundException("리뷰 없습니다: id=" + reviewId, "Not Found Review"));
        reviewRepository.delete(entity); // photos는 cascade REMOVE / orphanRemoval로 같이 삭제
        log.info("리뷰 삭제: id={}", reviewId);
    }

    // ================= 내부 유틸 =================

    private Product ensureProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RecordNotFoundException("상품 없습니다: id=" + productId, "Not Found Product"));
    }

    private Member ensureMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RecordNotFoundException("회원 없습니다: id=" + memberId, "Not Found Member"));
    }

    /** 같은 리뷰의 사진 sortOrder를 1..n으로 보정 */
    private void normalizeSort(Long reviewId) {
        var list = reviewPhotoRepository.findByReview_ReviewIdOrderBySortOrderAsc(reviewId);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSortOrder(i + 1);
        }
        reviewPhotoRepository.saveAll(list);
    }
}
