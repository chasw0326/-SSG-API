package com.ssg.ssgproductapi.service;


import com.ssg.ssgproductapi.domain.ApplyPromotion;
import com.ssg.ssgproductapi.domain.Product;
import com.ssg.ssgproductapi.exception.custom.ForbiddenException;
import com.ssg.ssgproductapi.repository.ApplyPromotionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ApplyPromotionServiceTests {

    @Autowired
    private ApplyPromotionService applyPromotionService;

    @Autowired
    private ApplyPromotionRepository applyPromotionRepository;

    @Autowired
    private ProductService productService;

    @DisplayName("상품에 적용되어있는 프로모션 삭제")
    @Test
    void Should_DeleteAppliedPromotion() {
        applyPromotionService.deleteAppliedPromotion(4L, 5L, 7L);
        ApplyPromotion applyPromotion = applyPromotionRepository.getByProduct_IdAndPromotion_Id(5L, 7L);
        assertNull(applyPromotion);
    }

    @DisplayName("권한없는 사람이 적용되어 있는 프로모션 삭제시 예외")
    @Test
    void Should_ThrowException_WhenDeleteAppliedPromotion_ByStranger() {
        Throwable ex = assertThrows(ForbiddenException.class, () ->
                applyPromotionService.deleteAppliedPromotion(5L, 5L, 5L)
        );
        assertEquals("권한이 없습니다.", ex.getMessage());
    }

    @DisplayName("프로모션 삭제시 다음으로 할인율 높은 프로모션 적용되는지")
    @Test
    void Should_ApplyNextPromotion_WhenDeleteAppliedPromotion() {
        // 현재 적용된 프로모션(7)을 삭제하면
        applyPromotionService.deleteAppliedPromotion(4L, 4L, 7L);

        // 다음으로 할인율 높은(8) 적용
        assertEquals(8L, productService.getProduct(4L).getPromotion().getId());
    }

    @DisplayName("프로모션 전체 삭제")
    @Test
    void Should_DeletePromotion() {
        // commandLineRunner로 product pk 11, 13, 15에
        // 가장 할인율높은 프로모션(3L) 적용되어 있음
        applyPromotionService.deletePromotion(2L, 3L);
        assertEquals(5L, productService.getProduct(11L).getPromotion().getId());
        assertEquals(6L, productService.getProduct(13L).getPromotion().getId());
        assertEquals(5L, productService.getProduct(15L).getPromotion().getId());

    }

    @DisplayName("적용된 프로모션 삭제 했을때 나머지 적용된 프로모션들의 할인율이 상품가격보다 높을 때")
    @Test
    void Should_NotApplyPromotion_WhenDiscountPriceIsLessEqualThenZero() {

        // 현재 적용된 프로모션
        applyPromotionService.deletePromotion(4L, 11L);
        Product product = productService.getProduct(17L);
        // 나머지 프로모션들이 적용되면 가격이 0보다 작아짐 -> 적용 안 됨
        assertNull(product.getPromotion());
        // 적용된 프로모션이 없으니 원가와, 할인가가 동일
        assertEquals(product.getFullPrice(), product.getDiscountedPrice());
    }

}
