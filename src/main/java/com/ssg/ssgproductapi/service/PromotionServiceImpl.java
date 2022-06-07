package com.ssg.ssgproductapi.service;

import com.ssg.ssgproductapi.domain.DiscountPolicy;
import com.ssg.ssgproductapi.domain.Promotion;
import com.ssg.ssgproductapi.domain.User;
import com.ssg.ssgproductapi.exception.custom.ForbiddenException;
import com.ssg.ssgproductapi.exception.custom.InvalidArgsException;
import com.ssg.ssgproductapi.exception.custom.NotFoundException;
import com.ssg.ssgproductapi.repository.PromotionRepository;
import com.ssg.ssgproductapi.repository.AppliedPromotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Log4j2
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl {

    private final UserService userService;
    private final PromotionRepository promotionRepository;

    @Transactional
    public Promotion getPromotion(Long promotionId) {
        return promotionRepository.findById(promotionId)
                .orElseThrow(() -> new NotFoundException("can not find user by promotionId: " + promotionId));
    }

    @Transactional
    public void registerPromotion(String name, String description, DiscountPolicy policy, int discountRate, String start, String end) {
        LocalDateTime startedAt = LocalDateTime.parse(start,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endAt = LocalDateTime.parse(end,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (endAt.isBefore(startedAt)) {
            throw new InvalidArgsException("종료시점이 시작시점보다 빠릅니다.");
        }
        Promotion promotion = Promotion.builder()
                .name(name)
                .description(description)
                .policy(policy)
                .discountRate(discountRate)
                .startedAt(startedAt)
                .endAt(endAt)
                .build();

        promotionRepository.save(promotion);
    }

    @Transactional
    public void updatePromotion(Long userId, Long promotionId, String name, String description, DiscountPolicy policy, int discountRate, String start, String end) {
        // FIXME: 권한 체크
        User user = userService.getUser(userId);
        Promotion promotion = this.getPromotion(promotionId);
        if (!promotion.getUser().getId().equals(userId)) {
            throw new ForbiddenException("수정 권한이 없습니다.");
        }

        final String updatedName = StringUtils.isNotBlank(name) ? name : promotion.getName();
        final String updatedDescription = StringUtils.isNotBlank(description) ? description : promotion.getDescription();
        final DiscountPolicy updatedPolicy = ObjectUtils.isNotEmpty(policy) ? policy : promotion.getPolicy();
        final int updatedDiscountedRate = ObjectUtils.isNotEmpty(discountRate) ? discountRate : promotion.getDiscountRate();

        LocalDateTime startedAt = promotion.getStartedAt();
        LocalDateTime endAt = promotion.getEndAt();
        if (StringUtils.isNotBlank(start)) {
            startedAt = LocalDateTime.parse(start,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (StringUtils.isNotBlank(end)) {
            endAt = LocalDateTime.parse(end,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        promotion.updatePromotion(
                updatedName,
                updatedDescription,
                updatedPolicy,
                updatedDiscountedRate,
                startedAt,
                endAt
        );
    }
}
