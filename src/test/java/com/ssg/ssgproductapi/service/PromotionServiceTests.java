package com.ssg.ssgproductapi.service;

import com.ssg.ssgproductapi.domain.Promotion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class PromotionServiceTests {

    @Autowired
    private PromotionService promotionService;

    @Test
    void Should_GetPromotionsByUserId() {
        System.out.println("************************************************************");
        List<Promotion> promotionList = promotionService.getMyPromotions(1L);
        for (Promotion promotion : promotionList) {
            System.out.println(promotion.getId());
        }
    }
}
