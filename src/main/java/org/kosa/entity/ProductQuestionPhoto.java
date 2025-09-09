package org.kosa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Slf4j
@Entity
public class ProductQuestionPhoto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;

    @Column(length = 512)
    private String url;

    private int sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private ProductQuestion productQuestion;
}
