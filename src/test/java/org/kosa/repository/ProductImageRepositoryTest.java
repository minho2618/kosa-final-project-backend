package org.kosa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.config.TestConfig;
import org.kosa.entity.Product;
import org.kosa.entity.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestConfig
// @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ProductImageRepositoryTest {

    @Autowired
    private ProductImageRepository productImageRepository;
    
    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        productImageRepository.deleteAll();
        productRepository.deleteAll();
        
        testProduct = Product.builder()
                .name("Test Product")
                .description("Test Product Description")
                .price(java.math.BigDecimal.valueOf(99.99))
                .isActive(true)
                .build();
        
        testProduct = productRepository.save(testProduct);
    }

    @Test
    @DisplayName("신규 상품 이미지 저장 테스트")
    void testSaveProductImage() {
        // Given
        ProductImage productImage = ProductImage.builder()
                .product(testProduct)
                .url("/images/test.jpg")
                .altText("test")
                .sortOrder(0)
                .build();

        // When
        ProductImage savedImage = productImageRepository.save(productImage);

        // Then
        assertThat(savedImage.getImageId()).isNotNull();
        assertThat(savedImage.getProduct()).isEqualTo(testProduct);
        assertThat(savedImage.getUrl()).isEqualTo("/images/test.jpg");
        assertThat(savedImage.getSortOrder()).isEqualTo(0);
    }

    @Test
    @DisplayName("상품 ID로 이미지 전체 조회 테스트")
    void testFindByProduct_ProductId() {
        // Given
        ProductImage image1 = ProductImage.builder()
                .product(testProduct)
                .url("/images/image1.jpg")
                .altText("image1.jpg")
                .sortOrder(1)
                .build();
        
        ProductImage image2 = ProductImage.builder()
                .product(testProduct)
                .url("/images/image2.jpg")
                .altText("image2.jpg")
                .sortOrder(2)
                .build();

        productImageRepository.save(image1);
        productImageRepository.save(image2);

        // When
        List<ProductImage> images = productImageRepository.findByProduct_ProductId(testProduct.getProductId());

        // Then
        assertThat(images).hasSize(2);
        assertThat(images)
                .extracting("url")
                .containsExactlyInAnyOrder("/images/image1.jpg", "/images/image2.jpg");
    }

    @Test
    @DisplayName("상품 ID로 정렬 순으로 이미지 조회 테스트")
    void testFindByProduct_ProductIdOrderBySortOrderAsc() {
        // Given
        ProductImage image3 = ProductImage.builder()
                .product(testProduct)
                .url("/images/image3.jpg")
                .altText("image3.jpg")
                .sortOrder(3)
                .build();
        
        ProductImage image1 = ProductImage.builder()
                .product(testProduct)
                .url("/images/image1.jpg")
                .altText("image1.jpg")
                .sortOrder(1)
                .build();
        
        ProductImage image2 = ProductImage.builder()
                .product(testProduct)
                .url("/images/image2.jpg")
                .altText("image2.jpg")
                .sortOrder(2)
                .build();

        productImageRepository.save(image3);
        productImageRepository.save(image1);
        productImageRepository.save(image2);

        // When
        List<ProductImage> images = productImageRepository.findByProduct_ProductIdOrderBySortOrderAsc(testProduct.getProductId());

        // Then
        assertThat(images).hasSize(3);
        assertThat(images.get(0).getSortOrder()).isEqualTo(1);
        assertThat(images.get(1).getSortOrder()).isEqualTo(2);
        assertThat(images.get(2).getSortOrder()).isEqualTo(3);
    }

    @Test
    @DisplayName("대표 이미지 조회 테스트")
    void testFindFirstByProduct_ProductIdOrderBySortOrderAsc() {
        // Given
        ProductImage image2 = ProductImage.builder()
                .product(testProduct)
                .url("/images/image2.jpg")
                .altText("image2.jpg")
                .sortOrder(2)
                .build();
        
        ProductImage image1 = ProductImage.builder()
                .product(testProduct)
                .url("/images/image1.jpg")
                .altText("image1.jpg")
                .sortOrder(1)
                .build();

        productImageRepository.save(image2);
        productImageRepository.save(image1);

        // When
        Optional<ProductImage> firstImage = productImageRepository.findFirstByProduct_ProductIdOrderBySortOrderAsc(testProduct.getProductId());

        // Then
        assertThat(firstImage).isPresent();
        assertThat(firstImage.get().getSortOrder()).isEqualTo(1);
        assertThat(firstImage.get().getUrl()).isEqualTo("/images/image1.jpg");
    }

    @Test
    @DisplayName("상품 ID로 이미지 일괄 삭제 테스트")
    void testDeleteByProductId() {
        // Given
        ProductImage image1 = ProductImage.builder()
                .product(testProduct)
                .url("/images/image1.jpg")
                .altText("image1.jpg")
                .sortOrder(1)
                .build();
        
        ProductImage image2 = ProductImage.builder()
                .product(testProduct)
                .url("/images/image2.jpg")
                .altText("image2.jpg")
                .sortOrder(2)
                .build();

        productImageRepository.save(image1);
        productImageRepository.save(image2);

        // When
        int deletedCount = productImageRepository.deleteByProductId(testProduct.getProductId());

        // Then
        assertThat(deletedCount).isEqualTo(2);
        List<ProductImage> remainingImages = productImageRepository.findByProduct_ProductId(testProduct.getProductId());
        assertThat(remainingImages).isEmpty();
    }
}