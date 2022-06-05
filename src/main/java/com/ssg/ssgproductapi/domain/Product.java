package com.ssg.ssgproductapi.domain;


import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class Product extends AuditingCreateUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private int fullPrice;

    private int discountedPrice;

    private short access;

    private LocalDateTime startedAt;

    private LocalDateTime endAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applied_promotion_id")
    private Promotion promotion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Seller seller;

    @Builder
    private Product(String name, String description, int fullPrice, short access,
                    LocalDateTime startedAt, LocalDateTime endAt){
        this.name = name;
        this.description = description;
        this.fullPrice = fullPrice;
        this.access = access;
        this.startedAt = startedAt;
        this.endAt = endAt;
    }
}
