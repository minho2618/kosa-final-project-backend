package org.kosa.repository;

import org.kosa.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByMemberId(Long memberId);
    void deleteByMemberIdAndProductId(Long memberId, Long productId);
    void deleteByMemberId(Long memberId);
}
