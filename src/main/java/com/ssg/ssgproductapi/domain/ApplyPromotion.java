package com.ssg.ssgproductapi.domain;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "apply_promotions",
        uniqueConstraints = {@UniqueConstraint(
                name = "promotion_uk",
                columnNames = {"product_Id", "promotion_Id"}
        )})
public class ApplyPromotion extends AuditingCreateUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Promotion promotion;

    @Builder
    private ApplyPromotion(Product product, Promotion promotion) {
        this.product = product;
        this.promotion = promotion;
    }

}
