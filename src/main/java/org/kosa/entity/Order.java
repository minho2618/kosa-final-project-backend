package org.kosa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.kosa.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private OrderStatus status;

    @CreationTimestamp
    private LocalDateTime created_at;

    private String address;

    @OneToMany(mappedBy = "orderItemId", cascade = CascadeType.ALL)
    private List<OrderItem> orderItemList = new ArrayList<>();
}
