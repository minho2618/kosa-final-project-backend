package org.kosa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.entity.Product;
import org.kosa.entity.Seller;
import org.kosa.entity.Member;
import org.kosa.enums.ProductCategory;
import org.kosa.enums.SellerRole;
import org.kosa.enums.MemberRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Seller testSeller;

    @BeforeEach
    void setUp() {
        Member sellerMember = new Member();
        sellerMember.setUsername("product_seller");
        sellerMember.setEmail("product_seller@example.com");
        sellerMember.setRole(MemberRole.ROLE_SELLER);
        Member savedSellerUser = memberRepository.save(sellerMember);

        Seller seller = new Seller();
        seller.setMember(savedSellerUser);
        seller.setSellerName("상품 농장");
        seller.setRole(SellerRole.authenticated);
        testSeller = sellerRepository.save(seller);

        Product product1 = new Product();
        product1.setName("유기농 사과");
        product1.setCategory(ProductCategory.과일);
        product1.setPrice(new BigDecimal("15000"));
        product1.setSeller(testSeller);
        product1.setIsActive(true);
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("신선한 당근");
        product2.setCategory(ProductCategory.채소);
        product2.setPrice(new BigDecimal("5000"));
        product2.setSeller(testSeller);
        product2.setIsActive(true);
        productRepository.save(product2);
    }

    @Test
    @DisplayName("카테고리로 상품 조회")
    void findProductByCategory() {
        // when
        List<Product> fruits = productRepository.findProductByCategory(ProductCategory.과일).orElseThrow();
        List<Product> vegetables = productRepository.findProductByCategory(ProductCategory.채소).orElseThrow();

        // then
        assertThat(fruits).hasSize(1);
        assertThat(fruits.get(0).getName()).isEqualTo("유기농 사과");
        assertThat(vegetables).hasSize(1);
        assertThat(vegetables.get(0).getName()).isEqualTo("신선한 당근");
    }

    @Test
    @DisplayName("이름으로 상품 검색")
    void searchProductByName() {
        // when
        List<Product> foundProducts = productRepository.searchProductByName("사과").orElseThrow();

        // then
        assertThat(foundProducts).hasSize(1);
        assertThat(foundProducts.get(0).getName()).isEqualTo("유기농 사과");
    }

    @Test
    @DisplayName("상품 ID로 특정 상품 조회")
    void findProductByProductId() {
        // given
        Product newProduct = new Product();
        newProduct.setName("테스트 포도");
        newProduct.setCategory(ProductCategory.과일);
        newProduct.setPrice(new BigDecimal("20000"));
        newProduct.setSeller(testSeller);
        newProduct.setIsActive(true);
        Product savedProduct = productRepository.save(newProduct);

        // when
        Product foundProduct = productRepository.findProductByProductId(savedProduct.getProductId()).orElseThrow();

        // then
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("테스트 포도");
    }
}
