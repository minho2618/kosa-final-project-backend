package org.kosa.repository;

import org.kosa.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    // BUY-PROD-002 상품 -> seller_user_id 로 생산자 찾기
    // Optional<Seller> findByUserId(Long userId);

    // List<Seller> getSellerList();


    // BUY-PROD-002 상품 -> seller_user_id 로 생산자 찾기
    // Optional<Seller> findByUserId(Long userId);

    //SEL-MEM-002 판매자 회원정보수정  service에서 save()로 update

    //SEL-MEM-003 판매자 회원탈퇴 service에서

}


