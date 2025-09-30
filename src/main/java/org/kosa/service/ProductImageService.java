package org.kosa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.dto.productImage.ProductImageReq;
import org.kosa.dto.productImage.ProductImageRes;
import org.kosa.entity.Product;
import org.kosa.entity.ProductImage;
import org.kosa.entity.ProductQuestion;
import org.kosa.entity.ProductQuestionPhoto;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.repository.ProductImageRepository;
import org.kosa.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final FileStorageService fileStorageService;
    // ================= 조회 =================

    /** 상품별 이미지 목록(정렬 순) */
    @Transactional(readOnly = true)
    public List<ProductImageRes> listByProduct(Long productId) {
        ensureProduct(productId);
        return productImageRepository.findByProduct_ProductIdOrderBySortOrderAsc(productId)
                .stream()
                .map(ProductImageRes::toProductImageRes)
                .toList();
    }

    /** 대표 이미지(정렬 1순위) */
    @Transactional(readOnly = true)
    public ProductImageRes getCover(Long productId) {
        ensureProduct(productId);
        ProductImage image = productImageRepository
                .findFirstByProduct_ProductIdOrderBySortOrderAsc(productId)
                .orElseThrow(() -> new RecordNotFoundException("대표 이미지 없음: productId=", "Not Found ProductImage"));
        return ProductImageRes.toProductImageRes(image);
    }

    // ================= 생성 =================

    public List<ProductImage> findByProduct_ProductIdOrderBySortOrderAsc(Long productId) {
        return productImageRepository.findByProduct_ProductIdOrderBySortOrderAsc(productId);
    }
    private int getCurrentMaxSortOrder(Long questionId) {
        List<ProductImage> photos = findByProduct_ProductIdOrderBySortOrderAsc(questionId);
        return photos.isEmpty() ? 0 : photos.get(photos.size() - 1).getSortOrder();
    }
    /** 단건 추가 (sortOrder 미지정 시 다음 순번) */
    @Transactional
    public List<ProductImage> add(Long productId, List<MultipartFile> files) {
        Product product = ensureProduct(productId);
        List<ProductImage> photos = new ArrayList<>();
        int currentMaxOrder = getCurrentMaxSortOrder(productId);
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String filename = fileStorageService.storeFile(file);
            String url = "/api/images/" + filename;

            ProductImage photo = ProductImage.builder()
                    .url(url)
                    .sortOrder(currentMaxOrder + i + 1)
                    .product(Product.builder().productId(productId).build())
                    .build();

            photos.add(photo);
        }

        return  productImageRepository.saveAll(photos);
    }

    /** 전체 교체(기존 삭제 후 일괄 저장) */
    @Transactional
    public List<ProductImageRes> replaceAll(Long productId, List<ProductImageReq> reqs) {
        Product product = ensureProduct(productId);

        // 기존 싹 지우고
        productImageRepository.deleteByProductId(productId);

        // 새로 채워 넣는다
        List<ProductImage> batch = new ArrayList<>();
        int sort = 1;
        for (ProductImageReq req : reqs) {
            Integer s = req.getSortOrder();
            batch.add(ProductImage.builder()
                    .url(req.getUrl())
                    .altText(req.getAltText())
                    .sortOrder(s == null || s <= 0 ? sort : s)
                    .product(product)
                    .build());
            sort++;
        }

        // sortOrder 기준 정렬 후 1..n 보정
        batch.sort(Comparator.comparingInt(ProductImage::getSortOrder));
        for (int i = 0; i < batch.size(); i++) batch.get(i).setSortOrder(i + 1);

        List<ProductImage> saved = productImageRepository.saveAll(batch);
        return saved.stream().map(ProductImageRes::toProductImageRes).toList();
    }

    // ================= 수정 =================

    /** URL/ALT/정렬 변경(정렬 변경 시 전체 보정) */
    @Transactional
    public ProductImageRes update(Long imageId, ProductImageReq req) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RecordNotFoundException("이미지 없음", "Not Found Image"));

        if (req.getUrl() != null) image.setUrl(req.getUrl());
        if (req.getAltText() != null) image.setAltText(req.getAltText());
        if (req.getSortOrder() > 0) {
            image.setSortOrder(req.getSortOrder());
            normalizeSort(image.getProduct().getProductId());
        }
        return ProductImageRes.toProductImageRes(image);
    }

    /** 지정 위치로 이동(1-base) */
    @Transactional
    public void move(Long imageId, int newPos) {
        ProductImage target = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RecordNotFoundException("이미지 없음","Not Found Image"));

        Long productId = target.getProduct().getProductId();
        List<ProductImage> list = productImageRepository.findByProduct_ProductIdOrderBySortOrderAsc(productId);

        // 대상 제거
        list.removeIf(pi -> pi.getImageId().equals(imageId));

        // 경계 보정
        newPos = Math.max(1, Math.min(newPos, list.size() + 1));

        // 새 순서로 삽입 후 1..n 재부여
        List<ProductImage> reordered = new ArrayList<>();
        int idx = 1;
        boolean inserted = false;
        for (ProductImage pi : list) {
            if (idx == newPos && !inserted) {
                target.setSortOrder(idx++);
                reordered.add(target);
                inserted = true;
            }
            pi.setSortOrder(idx++);
            reordered.add(pi);
        }
        if (!inserted) { // 맨 끝
            target.setSortOrder(idx);
            reordered.add(target);
        }
        productImageRepository.saveAll(reordered);
    }

    // ================= 삭제 =================

    @Transactional
    public void delete(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RecordNotFoundException("이미지 없음", "Not Found Image"));
        Long productId = image.getProduct().getProductId();

        productImageRepository.delete(image);
        normalizeSort(productId);
        log.info("이미지 삭제: imageId={}, productId={}", imageId, productId);
    }

    // ================= 내부 유틸 =================

    private Product ensureProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RecordNotFoundException("상품 없음","Not Found Ensure"));
    }

    /** 다음 정렬 번호 계산 */
    private int nextSortOrder(Long productId) {
        return productImageRepository.findByProduct_ProductId(productId)
                .stream()
                .mapToInt(ProductImage::getSortOrder)
                .max()
                .orElse(0) + 1;
    }

    /** 같은 상품 내 sortOrder를 1..n으로 보정 */
    private void normalizeSort(Long productId) {
        List<ProductImage> list = productImageRepository.findByProduct_ProductIdOrderBySortOrderAsc(productId);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSortOrder(i + 1);
        }
        productImageRepository.saveAll(list);
    }
}
