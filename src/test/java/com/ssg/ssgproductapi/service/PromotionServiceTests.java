package com.ssg.ssgproductapi.service;

import com.ssg.ssgproductapi.domain.Promotion;
import com.ssg.ssgproductapi.dto.PromotionRespDTO;
import com.ssg.ssgproductapi.exception.custom.InvalidArgsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PromotionServiceTests {

    @Autowired
    private PromotionService promotionService;

    @DisplayName("유효한 기간의 프로모션인지(현재 적용할 수 있는)")
    @Test
    void Should_ReturnFalseWhenInvalidPromotion() {
        // commandLineRunner로 생성해논 데이터
        assertFalse(promotionService.isValidPromotion(9L));
        assertTrue(promotionService.isValidPromotion(3L));
    }

    @DisplayName("종료일이 시작일 보다 빠를 때")
    @Test
    void Should_ThrowExceptionWhenEndAtIsBeforeThenStart() {
        String start = "2022-06-12 12:12:12";
        String end = "2022-06-11 12:12:12";
        assertThrows(InvalidArgsException.class, () ->
        promotionService.registerPromotion(1L, "프로모션", "FW세일",
                "FIXED", 30, start, end)
        );
    }

    @DisplayName("정률제의 값이 100보다 클 때")
    @Test
    void Should_ThrowExceptionWhenFIXEDRateIsGreaterThen100() {
        String start = "2022-06-11 12:12:12";
        String end = "2022-06-12 12:12:12";
        assertThrows(InvalidArgsException.class, () ->
        promotionService.registerPromotion(1L, "프로모션", "FW세일",
                "FIXED", 100, start, end)
        );
    }

    @DisplayName("정률제의 값이 0보다 작을 때")
    @Test
    void Should_ThrowExceptionWhenFIXEDRateIsLessEqualThen0() {
        String start = "2022-06-11 12:12:12";
        String end = "2022-06-12 12:12:12";
        assertThrows(InvalidArgsException.class, () ->
                promotionService.registerPromotion(1L, "프로모션", "FW세일",
                        "FIXED", 0, start, end)
                );

        assertThrows(InvalidArgsException.class, () ->
                promotionService.registerPromotion(1L, "프로모션", "FW세일",
                        "FIXED", -3, start, end)
        );
    }

    @DisplayName("정액제의 값이 0보다 작을 때")
    @Test
    void Should_ThrowExceptionWhenFLATDRateIsLessEqualThen0() {
        String start = "2022-06-11 12:12:12";
        String end = "2022-06-12 12:12:12";
        assertThrows(InvalidArgsException.class, () ->
                promotionService.registerPromotion(1L, "프로모션", "FW세일",
                        "FLAT", 0, start, end)
        );

        assertThrows(InvalidArgsException.class, () ->
                promotionService.registerPromotion(1L, "프로모션", "FW세일",
                        "FLAT", -3000, start, end)
        );
    }

    @DisplayName("내가 등록한 프로모션 가져오기")
    @Test
    void Should_GetMyPromotions() {
        List<PromotionRespDTO.MyPromotion> promotions = promotionService.getMyPromotions(1L);
        assertEquals(promotions.get(0).getId(), 1L);
        assertEquals(promotions.get(1).getId(), 2L);
    }
}
