package org.kosa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.config.TestConfig;
import org.kosa.entity.Member;
import org.kosa.entity.Product;
import org.kosa.entity.Seller;
import org.kosa.enums.MemberRole;
import org.kosa.enums.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
// @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private SellerRepository sellerRepository;

    private Product testProduct;
    private Member testMember;
    private Seller testSeller;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        memberRepository.deleteAll();
        sellerRepository.deleteAll();

        testMember = Member.builder()
                .email("test@example.com")
                .password("encodedPassword123")
                .phoneNum("010-1234-5678")
                .role(MemberRole.ROLE_CUSTOMER)
                .address("Test Address 123")
                .name("Test User")
                .build();

        memberRepository.save(testMember);
        
        testSeller = Seller.builder()
                .member(testMember)
                .sellerRegNo("123-45-67890")
                .sellerName("Test Company")
                .sellerAddress("Test Address 123")
                .sellerIntro("Test")
                .postalCode("12345")
                .country("한국")
                .build();
        
        testSeller = sellerRepository.save(testSeller);

        testProduct = Product.builder()
                .name("Test Product")
                .description("This is a test product")
                .price(new BigDecimal("99.99"))
                .category(ProductCategory.채소)
                .discountValue(new BigDecimal("10.00"))
                .isActive(true)
                .seller(testSeller)
                .build();
    }

    @Test
    @DisplayName("신규 상품 저장 테스트")
    void testSaveProduct() {
        // Given
        Product savedProduct = productRepository.save(testProduct);

        // When & Then
        assertThat(savedProduct.getProductId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Test Product");
        assertThat(savedProduct.getPrice()).isEqualTo(new BigDecimal("99.99"));
        assertThat(savedProduct.getCategory()).isEqualTo(ProductCategory.채소);
    }

    @Test
    @DisplayName("카테고리별 상품 조회 테스트")
    void testFindProductByCategory() {
        // Given
        Product electronicsProduct = Product.builder()
                .name("Electronics Product")
                .description("Electronics Test Product")
                .price(new BigDecimal("199.99"))
                .category(ProductCategory.과일)
                .discountValue(new BigDecimal("20.00"))
                .isActive(true)
                .seller(testSeller)
                .build();
        
        Product clothingProduct = Product.builder()
                .name("Clothing Product")
                .description("Clothing Test Product")
                .price(new BigDecimal("49.99"))
                .category(ProductCategory.과일)
                .discountValue(new BigDecimal("5.00"))
                .isActive(true)
                .seller(testSeller)
                .build();

        productRepository.save(electronicsProduct);
        productRepository.save(clothingProduct);

        // When
        Optional<List<Product>> products = productRepository.findProductByCategory(ProductCategory.과일);

        // Then
        assertThat(products).isPresent();
        assertThat(products.get()).hasSize(1);
        assertThat(products.get().get(0).getName()).isEqualTo("Electronics Product");
    }

    @Test
    @DisplayName("상품명 검색 테스트")
    void testSearchProductByName() {
        // Given
        Product laptop = Product.builder()
                .name("Laptop Computer")
                .description("High-performance laptop")
                .price(new BigDecimal("1299.99"))
                .category(ProductCategory.곡물)
                .discountValue(new BigDecimal("100.00"))
                .isActive(true)
                .seller(testSeller)
                .build();
        
        Product phone = Product.builder()
                .name("Smartphone")
                .description("Latest smartphone")
                .price(new BigDecimal("799.99"))
                .category(ProductCategory.곡물)
                .discountValue(new BigDecimal("50.00"))
                .isActive(true)
                .seller(testSeller)
                .build();

        productRepository.save(laptop);
        productRepository.save(phone);

        // When
        Optional<List<Product>> searchResults = productRepository.searchProductByName("Laptop");

        // Then
        assertThat(searchResults).isPresent();
        assertThat(searchResults.get()).hasSize(1);
        assertThat(searchResults.get().get(0).getName()).contains("Laptop");
    }

    @Test
    @DisplayName("상품 ID로 상품 조회 테스트")
    void testFindProductByProductId() {
        // Given
        Product savedProduct = productRepository.save(testProduct);

        // When
        Optional<Product> foundProduct = productRepository.findProductByProductId(savedProduct.getProductId());

        // Then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getProductId()).isEqualTo(savedProduct.getProductId());
        assertThat(foundProduct.get().getName()).isEqualTo("Test Product");
    }

    @Test
    @DisplayName("상품 업데이트 테스트")
    void testUpdateProduct() {
        // Given
        Product savedProduct = productRepository.save(testProduct);

        // When
        savedProduct.setName("Updated Product Name");
        savedProduct.setPrice(new BigDecimal("149.99"));
        savedProduct.setIsActive(false);
        Product updatedProduct = productRepository.save(savedProduct);

        // Then
        assertThat(updatedProduct.getName()).isEqualTo("Updated Product Name");
        assertThat(updatedProduct.getPrice()).isEqualTo(new BigDecimal("149.99"));
        assertThat(updatedProduct.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("비활성 상품 포함 조회 테스트")
    void testFindAllWithInactiveProducts() {
        // Given
        Product activeProduct = Product.builder()
                .name("Active Product")
                .description("Active Test Product")
                .price(new BigDecimal("99.99"))
                .category(ProductCategory.곡물)
                .discountValue(new BigDecimal("10.00"))
                .isActive(true)
                .seller(testSeller)
                .build();
        
        Product inactiveProduct = Product.builder()
                .name("Inactive Product")
                .description("Inactive Test Product")
                .price(new BigDecimal("89.99"))
                .category(ProductCategory.곡물)
                .discountValue(new BigDecimal("5.00"))
                .isActive(false)
                .seller(testSeller)
                .build();

        productRepository.save(activeProduct);
        productRepository.save(inactiveProduct);

        // When
        List<Product> allProducts = productRepository.findAll();

        // Then
        assertThat(allProducts).hasSize(2);
        assertTrue(allProducts.stream().anyMatch(p -> p.getIsActive()));
        assertTrue(allProducts.stream().anyMatch(p -> !p.getIsActive()));
    }
}