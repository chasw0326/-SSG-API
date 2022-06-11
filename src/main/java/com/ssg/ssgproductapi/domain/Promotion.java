package com.ssg.ssgproductapi.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "promotions")
public class Promotion extends AuditingCreateUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, nullable = false)
    @Length(max = 30, message = "프로모션명은 30자 이하여야 합니다.")
    @NotBlank(message = "프로모션명은 필수 값 입니다.")
    private String name;

    @Column(length = 10, nullable = false)
    @Length(max = 30, message = "프로모션 설명은 30자 이하여야 합니다.")
    @NotBlank(message = "프로모션 설명은 필수 값입니다.")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountPolicy policy;

    @Column(nullable = false)
    @NotNull(message = "할인율은 필수 값입니다.")
    private int discountRate;

    @Column(length = 30, nullable = false)
    private LocalDateTime startedAt;

    @Column(length = 30, nullable = false)
    private LocalDateTime endAt;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    public void updatePromotion(String name, String description, DiscountPolicy policy, int discountRate, LocalDateTime startedAt, LocalDateTime endAt) {
        this.name = name;
        this.description = description;
        this.policy = policy;
        this.discountRate = discountRate;
        this.startedAt = startedAt;
        this.endAt = endAt;
    }

    @Builder
    private Promotion(User user, String name, String description, DiscountPolicy policy, int discountRate, LocalDateTime startedAt, LocalDateTime endAt) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.policy = policy;
        this.discountRate = discountRate;
        this.startedAt = startedAt;
        this.endAt = endAt;
    }
}
