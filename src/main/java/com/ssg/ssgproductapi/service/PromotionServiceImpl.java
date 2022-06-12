package com.ssg.ssgproductapi.service;


import com.google.common.base.Preconditions;
import com.ssg.ssgproductapi.domain.DiscountPolicy;
import com.ssg.ssgproductapi.domain.Product;
import com.ssg.ssgproductapi.domain.Promotion;
import com.ssg.ssgproductapi.domain.User;
import com.ssg.ssgproductapi.dto.PromotionRespDTO;
import com.ssg.ssgproductapi.exception.custom.ForbiddenException;
import com.ssg.ssgproductapi.exception.custom.InvalidArgsException;
import com.ssg.ssgproductapi.exception.custom.NotFoundException;
import com.ssg.ssgproductapi.repository.ApplyPromotionRepository;
import com.ssg.ssgproductapi.repository.ProductRepository;
import com.ssg.ssgproductapi.repository.PromotionRepository;
import com.ssg.ssgproductapi.util.ValidateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final UserService userService;
    private final PromotionRepository promotionRepository;
    private final ApplyPromotionRepository applyPromotionRepository;
    private final ValidateUtil validateUtil;

    @Override
    @Transactional
    public Promotion getPromotion(Long promotionId) {
        return promotionRepository.findById(promotionId)
                .orElseThrow(() -> new NotFoundException("can not find promotion by promotionId: " + promotionId));
    }

    @Override
    @Transactional
    public List<PromotionRespDTO.MyPromotion> getMyPromotions(Long userId) {
        List<Promotion> promotions = promotionRepository.findAllByUserId(userId);
        List<PromotionRespDTO.MyPromotion> myPromotions = new ArrayList<>();
        for (Promotion promotion : promotions) {
            List<Long> productIdsWithPromotionsApplied = applyPromotionRepository.getProductIdsByPromotionId(promotion.getId());
            myPromotions.add(PromotionRespDTO.MyPromotion.builder()
                    .id(promotion.getId())
                    .name(promotion.getName())
                    .description(promotion.getDescription())
                    .policy(promotion.getPolicy())
                    .discountRate(promotion.getDiscountRate())
                    .startedAt(promotion.getStartedAt())
                    .endAt(promotion.getEndAt())
                    .productIds(productIdsWithPromotionsApplied)
                    .build());
        }

        return myPromotions;
    }

    @Override
    @Transactional
    public void registerPromotion(Long userId, String name, String description, String policy, Integer discountRate, String start, String end) {
        Preconditions.checkNotNull(userId, "userId은 필수값 입니다.");
        Preconditions.checkNotNull(name, "name은 필수값 입니다.");
        Preconditions.checkNotNull(description, "description은 필수값 입니다.");
        Preconditions.checkNotNull(policy, "policy은 필수값 입니다.");
        Preconditions.checkNotNull(discountRate, "discountRate은 필수값 입니다.");
        Preconditions.checkNotNull(start, "시작일은 필수값 입니다.");
        Preconditions.checkNotNull(end, "종료일은 필수값 입니다.");

        User user = userService.getUser(userId);
        LocalDateTime startedAt = LocalDateTime.parse(start,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endAt = LocalDateTime.parse(end,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (endAt.isBefore(startedAt)) {
            throw new InvalidArgsException("종료시점이 시작시점보다 빠릅니다.");
        }

        DiscountPolicy discountPolicy = policy.equals(DiscountPolicy.FIXED.toString()) ? DiscountPolicy.FIXED : DiscountPolicy.FLAT;
        if (discountPolicy.equals(DiscountPolicy.FIXED)) {
            if (discountRate <= 0 || discountRate >= 100) {
                throw new InvalidArgsException("정률제의 할인율 범위는 1부터 99까지 가능합니다.");
            }
        }
        if (discountPolicy.equals(DiscountPolicy.FLAT)) {
            if (discountRate <= 0) {
                throw new InvalidArgsException("정률제의 할인율 범위는 1부터 99까지 가능합니다.");
            }
        }
        Promotion promotion = Promotion.builder()
                .user(user)
                .name(name)
                .description(description)
                .policy(discountPolicy)
                .discountRate(discountRate)
                .startedAt(startedAt)
                .endAt(endAt)
                .build();

        validateUtil.validate(promotion);
        promotionRepository.save(promotion);
    }

//    @Override
//    @Transactional
//    public void updatePromotion(Long userId, Long promotionId, String name, String description, String policy, Integer discountRate, String start, String end) {
//
//        Promotion promotion = this.getPromotion(promotionId);
//        if (!promotion.getUser().getId().equals(userId)) {
//            throw new ForbiddenException("수정 권한이 없습니다.");
//        }
//        final String updatedName = StringUtils.isNotBlank(name) ? name : promotion.getName();
//        final String updatedDescription = StringUtils.isNotBlank(description) ? description : promotion.getDescription();
//        // Policy가 비어있지 않으면 FIXED인지 FLAT인지 체크
//        final DiscountPolicy updatedPolicy = ObjectUtils.isNotEmpty(policy) ?
//                policy.equals(DiscountPolicy.FIXED.toString()) ? DiscountPolicy.FIXED : DiscountPolicy.FLAT
//                : promotion.getPolicy();
//        final int updatedDiscountedRate = ObjectUtils.isNotEmpty(discountRate) ? discountRate : promotion.getDiscountRate();
//
//        LocalDateTime startedAt = promotion.getStartedAt();
//        LocalDateTime endAt = promotion.getEndAt();
//
//        if (StringUtils.isNotBlank(start)) {
//            startedAt = LocalDateTime.parse(start,
//                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        }
//        if (StringUtils.isNotBlank(end)) {
//            endAt = LocalDateTime.parse(end,
//                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        }
//
//        if (endAt.isBefore(startedAt)) {
//            throw new InvalidArgsException("종료시점이 시작시점보다 빠릅니다.");
//        }
//
//        promotion.updatePromotion(
//                updatedName,
//                updatedDescription,
//                updatedPolicy,
//                updatedDiscountedRate,
//                startedAt,
//                endAt
//        );
//        validateUtil.validate(promotion);
//    }

    @Override
    @Transactional
    public boolean isValidPromotion(Long promotionId) {
        Promotion promotion = this.getPromotion(promotionId);
        LocalDateTime startedAt = promotion.getStartedAt();
        LocalDateTime endAt = promotion.getEndAt();
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(startedAt) && !now.isAfter(endAt);
    }

}
