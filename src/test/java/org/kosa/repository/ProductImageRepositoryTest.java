package org.kosa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.entity.Product;
import org.kosa.entity.ProductImage;
import org.kosa.entity.Seller;
import org.kosa.entity.Users;
import org.kosa.enums.ProductCategory;
import org.kosa.enums.SellerRole;
import org.kosa.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ProductImageRepositoryTest {

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private UsersRepository usersRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        Users sellerUser = new Users();
        sellerUser.setUsername("image_seller");
        sellerUser.setEmail("image_seller@example.com");
        sellerUser.setRole(UserRole.ROLE_SELLER);
        Users savedSellerUser = usersRepository.save(sellerUser);

        Seller seller = new Seller();
        seller.setUsers(savedSellerUser);
        seller.setUserId(savedSellerUser.getUserId());
        seller.setSellerName("이미지 농장");
        seller.setRole(SellerRole.authenticated);
        Seller savedSeller = sellerRepository.save(seller);

        Product product = new Product();
        product.setName("이미지 사과");
        product.setPrice(new BigDecimal("12000"));
        product.setCategory(ProductCategory.과일);
        product.setSeller(savedSeller);
        product.setIsActive(true);
        testProduct = productRepository.save(product);

        ProductImage image1 = new ProductImage();
        image1.setProduct(testProduct);
        image1.setUrl("/img/image1.jpg");
        image1.setSortOrder(2);
        productImageRepository.save(image1);

        ProductImage image2 = new ProductImage();
        image2.setProduct(testProduct);
        image2.setUrl("/img/image2.jpg");
        image2.setSortOrder(1);
        productImageRepository.save(image2);
    }

    @Test
    @DisplayName("상품 ID로 모든 이미지 조회")
    void findByProduct_ProductId() {
        // when
        List<ProductImage> images = productImageRepository.findByProduct_ProductId(testProduct.getProductId());

        // then
        assertThat(images).hasSize(2);
    }

    @Test
    @DisplayName("상품 ID로 이미지 정렬하여 조회")
    void findByProduct_ProductIdOrderBySortOrderAsc() {
        // when
        List<ProductImage> sortedImages = productImageRepository.findByProduct_ProductIdOrderBySortOrderAsc(testProduct.getProductId());

        // then
        assertThat(sortedImages).hasSize(2);
        assertThat(sortedImages.get(0).getSortOrder()).isEqualTo(1);
        assertThat(sortedImages.get(0).getUrl()).isEqualTo("/img/image2.jpg");
        assertThat(sortedImages.get(1).getSortOrder()).isEqualTo(2);
    }

    @Test
    @DisplayName("상품의 첫 번째 이미지(대표 이미지) 조회")
    void findFirstByProduct_ProductIdOrderBySortOrderAsc() {
        // when
        ProductImage firstImage = productImageRepository.findFirstByProduct_ProductIdOrderBySortOrderAsc(testProduct.getProductId()).orElseThrow();

        // then
        assertThat(firstImage).isNotNull();
        assertThat(firstImage.getSortOrder()).isEqualTo(1);
        assertThat(firstImage.getUrl()).isEqualTo("/img/image2.jpg");
    }

    @Test
    @DisplayName("상품 ID로 모든 이미지 삭제")
    void deleteByProductId() {
        // when
        productImageRepository.deleteByProductId(testProduct.getProductId());
        List<ProductImage> imagesAfterDelete = productImageRepository.findByProduct_ProductId(testProduct.getProductId());

        // then
        assertThat(imagesAfterDelete).isEmpty();
    }
}
