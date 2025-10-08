package org.kosa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.kosa.dto.product.ProductReq;
import org.kosa.dto.product.ProductRes;
import org.kosa.dto.product.ProductCardRes;
import org.kosa.dto.seller.SellerRes;
import org.kosa.entity.Product;
import org.kosa.entity.Seller;
import org.kosa.enums.ProductCategory;
import org.kosa.enums.ProductStatus;
import org.kosa.enums.SellerRole;
import org.kosa.exception.InvalidInputException;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.repository.ProductRepository;
import org.kosa.repository.SellerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductService {

    private final ProductRepository productRepository;
    private final SellerService sellerService;

    // ========================= 조회(읽기 전용) =========================

    @Transactional(readOnly = true)                           // 읽기 전용 트랜잭션(더티체킹 최적화)
    public ProductRes getProductDetail(Long productId) throws InvalidInputException{
        Product product = productRepository.findById(productId)   // PK로 조회(Optional)
                .orElseThrow(() -> new InvalidInputException(
                        "상품을 찾을 수 없습니다. id=" + productId, "Not Found"));
        return ProductRes.toProductRes(product);      // Entity -> DTO
    }

    public Product getProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new InvalidInputException(
                "상품을 찾을 수 없습니다. id=" + productId, "Not Found"));;
        return product;
    }

    @Transactional(readOnly = true)
    public Page<ProductCardRes> getAllProducts(Pageable pageable) {
        return productRepository.findProductCards(pageable);
    }


    @Transactional(readOnly = true)
    public List<ProductRes> getProductsByIsActive(boolean active){
        return productRepository.findProductByIsActive(active).stream()
                .map(ProductRes::toProductRes)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductRes> getAllProducts() {                // 전체 목록 -> 간단 DTO 리스트
        return productRepository.findAll().stream()
                .map(ProductRes::toProductRes)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductRes> getAllProductsByStatus(ProductStatus status) {                // 전체 목록 -> 간단 DTO 리스트
        return productRepository.findProductByStatus(status).stream()
                .map(ProductRes::toProductRes)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)                           // 읽기 전용 트랜잭션
    public List<ProductCardRes> getProductsByCategory(ProductCategory category) throws RecordNotFoundException{
        // 사용자 정의 쿼리 메서드(Optional<List<Product>>) 가정
        List<Product> list = productRepository.findProductByCategory(category)
                .orElseThrow(() -> new RecordNotFoundException(
                        "해당 카테고리 상품이 없습니다." , "Not Found category"));

        return list.stream()
                .map(ProductCardRes::toProductCardRes)                            // 간단 DTO 매핑ａｓｄａｄ
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)            // 읽기 전용 트랜잭션
    public List<ProductRes> searchProductsByName(String keyword) {
        // 사용자 정의 @Query like 검색(Optional<List<Product>>) 가정
        List<Product> list = productRepository.searchProductByName(keyword)
                .orElseThrow(() -> new RecordNotFoundException(
                        "검색 결과가 없습니다. keyword=" + keyword, "Not Found"));

        return list.stream()
                .map(ProductRes::toProductRes)
                .collect(Collectors.toList());
    }

    // ========================= 생성/수정/삭제(CUD) =========================

    @Transactional                                            // 쓰기 트랜잭션(성공 시 커밋, 예외 시 롤백)
    public ProductRes createProduct(ProductReq req, Long memberId) {          // 상품 등록
        Seller seller = sellerService.toSellerByMemberId(memberId);
        Product entity = ProductReq.toProduct(req, seller);                           // 필수 필드/기본값은 DTO 단계에서 검증 권장
        entity.setIsActive(seller.getRole().equals(SellerRole.AUTHENTICATED));
        if (entity.getIsActive() == false)
            entity.setStatus(ProductStatus.PENDING);
        else
            entity.setStatus(ProductStatus.APPROVED);
        Product saved = productRepository.save(entity);            // INSERT & 영속화
        log.info("상품 등록 완료: id={}", saved.getProductId());     // 등록 결과 로깅
        return ProductRes.toProductRes(saved);                             // 응답 DTO 반환
    }

    @Transactional
    public ProductRes updateProduct(Long productId, ProductReq req) { // 일부/전역 수정
        Product entity = productRepository.findById(productId)         // 수정 대상 조회(영속 상태)
                .orElseThrow(() -> new RecordNotFoundException("상품 수정 실패: 대상 없음 id=" + productId, "Not Found"));

        // ===== 더티 체킹을 활용한 부분 수정 =====
        // 필요한 필드만 안전하게 업데이트 (null/빈값 가드 로직은 프로젝트 정책에 맞게)
        if (req.getName() != null)            entity.setName(req.getName());
        if (req.getDescription() != null)     entity.setDescription(req.getDescription());
        if (req.getPrice() != null)           entity.setPrice(req.getPrice());
        if (req.getCategory() != null)        entity.setCategory(req.getCategory());
        if (req.getDiscountValue() != null)   entity.setDiscountValue(req.getDiscountValue());
        if (req.getIsActive() != null)        entity.setIsActive(req.getIsActive());
        // 판매자/이미지 등 연관관계 변경은 별도 메서드로 분리하는 것을 권장

        // save 호출 없이도 트랜잭션 종료 시점에 UPDATE 실행(더티 체킹)
        log.info("상품 수정 완료: id={}", entity.getProductId());
        return ProductRes.toProductRes(entity);                                        // 변경 후 DTO 반환
    }

    @Transactional
    public void deleteProduct(Long productId) {                 // 상품 삭제
        Product entity = productRepository.findById(productId)      // 삭제 대상 조회
                .orElseThrow(() -> new RecordNotFoundException("상품 삭제 실패: 대상 없음 id=" + productId, "Not Found"));
        productRepository.delete(entity);                            // DELETE
        log.info("상품 삭제 완료: id={}", productId);
    }

    // ========================= 편의 기능(예: 활성화/비활성화, 가격/할인 변경) =========================

    @Transactional
    public ProductRes changeActive(Long productId, boolean active) { // 활성/비활성 토글
        Product entity = productRepository.findById(productId)
                .orElseThrow(() -> new RecordNotFoundException("상태 변경 실패: 대상 없음 id=" + productId, "Not Found"));
        entity.setIsActive(active);                                     // 상태 변경
        log.info("상품 활성상태 변경: id={}, active={}", productId, active);
        return ProductRes.toProductRes(entity);                                  // 변경된 상태 반환
    }

    @Transactional
    public ProductRes changePrice(Long productId, java.math.BigDecimal newPrice) { // 가격 변경
        Product entity = productRepository.findById(productId)
                .orElseThrow(() -> new RecordNotFoundException("가격 변경 실패: 대상 없음 id=" + productId, "Not Found"));
        entity.setPrice(newPrice);                                       // 값 검증은 상위/DTO에서
        log.info("상품 가격 변경: id={}, price={}", productId, newPrice);
        return ProductRes.toProductRes(entity);
    }

    @Transactional
    public ProductRes changeDiscount(Long productId, java.math.BigDecimal newDiscount) { // 할인 변경
        Product entity = productRepository.findById(productId)
                .orElseThrow(() -> new RecordNotFoundException("할인 변경 실패: 대상 없음 id=" + productId, "Not Found"));
        entity.setDiscountValue(newDiscount);
        log.info("상품 할인 변경: id={}, discount={}", productId, newDiscount);
        return ProductRes.toProductRes(entity);
    }
}
