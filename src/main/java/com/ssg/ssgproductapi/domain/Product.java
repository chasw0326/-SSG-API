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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType authority;

    private LocalDateTime startedAt;

    private LocalDateTime endAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applied_promotion_id")
    private Promotion promotion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    public void updateFullPrice(int price) { this.fullPrice = price; }

    public void updateDiscountedPrice(int discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public void updatePromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public void updateProduct(String name, String description, UserType userType,
                              LocalDateTime startedAt, LocalDateTime endAt) {
        this.name = name;
        this.description = description;
        this.authority = userType;
        this.startedAt = startedAt;
        this.endAt = endAt;
    }

    @Builder
    private Product(String name, String description, int fullPrice, UserType authority,
                    LocalDateTime startedAt, LocalDateTime endAt, User user){
        this.name = name;
        this.description = description;
        this.discountedPrice = fullPrice;
        this.fullPrice = fullPrice;
        this.authority = authority;
        this.startedAt = startedAt;
        this.endAt = endAt;
        this.user = user;
    }
}
