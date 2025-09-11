package org.kosa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.entity.Product;
import org.kosa.entity.Review;
import org.kosa.entity.Seller;
import org.kosa.entity.Users;
import org.kosa.enums.ProductCategory;
import org.kosa.enums.SellerRole;
import org.kosa.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ProductRepository productRepository;

    private Users testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // given user
        Users user = new Users();
        user.setUsername("reviewer");
        user.setEmail("reviewer@example.com");
        user.setPassword("password");
        user.setName("리뷰어");
        user.setRole(UserRole.ROLE_CUSTOMER);
        testUser = usersRepository.save(user);

        // given seller
        Users sellerUser = new Users();
        sellerUser.setUsername("review_seller");
        sellerUser.setEmail("review_seller@example.com");
        sellerUser.setPassword("password");
        sellerUser.setName("판매자");
        sellerUser.setRole(UserRole.ROLE_SELLER);
        Users savedSellerUser = usersRepository.save(sellerUser);

        Seller seller = new Seller();
        seller.setUsers(savedSellerUser);
        seller.setUserId(savedSellerUser.getUserId());
        seller.setSellerName("리뷰 농장");
        seller.setSellerRegNo("111-22-33333");
        seller.setRole(SellerRole.authenticated);
        Seller savedSeller = sellerRepository.save(seller);

        // given product
        Product product = new Product();
        product.setName("맛있는 사과");
        product.setPrice(new BigDecimal("10000"));
        product.setCategory(ProductCategory.과일);
        product.setSeller(savedSeller);
        product.setIsActive(true);
        testProduct = productRepository.save(product);
    }

    @Test
    @DisplayName("리뷰 저장 및 조회 테스트")
    void saveAndFindReview() {
        // given
        Review newReview = new Review();
        newReview.setUsers(testUser);
        newReview.setProduct(testProduct);
        newReview.setRating(5L);
        newReview.setContent("정말 맛있어요!");
        newReview.setCreatedAt(LocalDateTime.now());

        // when
        Review savedReview = reviewRepository.save(newReview);
        Review foundReview = reviewRepository.findById(savedReview.getReviewId()).orElse(null);

        // then
        assertThat(foundReview).isNotNull();
        assertThat(foundReview.getRating()).isEqualTo(5L);
        assertThat(foundReview.getContent()).isEqualTo("정말 맛있어요!");
        assertThat(foundReview.getUsers().getUsername()).isEqualTo("reviewer");
        assertThat(foundReview.getProduct().getName()).isEqualTo("맛있는 사과");
        System.out.println("Found Review: " + foundReview);
    }
}
