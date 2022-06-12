package com.ssg.ssgproductapi;


import com.ssg.ssgproductapi.service.ApplyPromotionService;
import com.ssg.ssgproductapi.service.ProductService;
import com.ssg.ssgproductapi.service.PromotionService;
import com.ssg.ssgproductapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InsertTestDataCommandLineRunner implements CommandLineRunner {

    private final UserService userService;
    private final ProductService productService;
    private final PromotionService promotionService;
    private final ApplyPromotionService appliedPromotionService;


    @Override
    public void run(String... args) throws Exception {
        this.insertUsers();
        this.insertProducts();
        this.insertPromotions();
        this.insertAppliedPromotions();
    }

    private void insertUsers() {

        userService.signup("cha@ssg.com", "차선욱", "ssgSSG123!@#", "일반회원");
        userService.signup("lee@ssg.com", "이수경", "ssgSSG123!@#", "일반회원");
        userService.signup("choi@ssg.com", "최상면", "ssgSSG123!@#", "기업회원");
        userService.signup("kang@ssg.com", "강재석", "ssgSSG123!@#", "일반회원");
        userService.signup("kim@ssg.com", "김구현", "ssgSSG123!@#", "일반회원");
    }

    private void insertProducts() {

        productService.registerProduct(2L, "구찌", "핸드백1", 880000,
                "일반회원", "2022-12-01 00:00:00","2022-12-25 23:59:59");
        productService.registerProduct(2L, "토리버치", "핸드백2", 1200000,
                "일반회원", "2022-06-01 00:00:00","2022-06-30 23:59:59");
        productService.registerProduct(2L, "샤넬", "핸드백3", 2000000,
                "일반회원", "2022-06-10 00:00:00","2022-06-30 23:59:59");
        productService.registerProduct(2L, "디올", "핸드백4", 3500000,
                "일반회원", "2022-01-01 00:00:00","2022-12-31 23:59:59");


        productService.registerProduct(1L, "에르메스", "반지1", 1230000,
                "기업회원", "2022-12-01 00:00:00","2022-12-25 23:59:59");
        productService.registerProduct(1L, "불가리", "반지2", 1400000,
                "일반회원", "2022-06-01 00:00:00","2022-06-30 23:59:59");
        productService.registerProduct(1L, "생로랑", "반지3", 2500000,
                "일반회원", "2022-06-10 00:00:00","2022-06-30 23:59:59");
        productService.registerProduct(1L, "프라다", "반지4", 4000000,
                "일반회원", "2022-01-01 00:00:00","2022-12-31 23:59:59");


        productService.registerProduct(3L, "파텍필립", "시계1", 200000000,
                "기업회원", "2022-12-01 00:00:00","2022-12-25 23:59:59");
        productService.registerProduct(3L, "오메가", "시계2", 120000000,
                "일반회원", "2022-06-01 00:00:00","2022-06-30 23:59:59");
        productService.registerProduct(3L, "바쉐린콘스탄틴", "시계3", 30000000,
                "기업회원", "2022-06-10 00:00:00","2022-06-30 23:59:59");
        productService.registerProduct(3L, "롤렉스", "시계4", 40000000,
                "일반회원", "2022-01-01 00:00:00","2022-12-31 23:59:59");


        productService.registerProduct(4L, "투미", "백팩1", 880000,
                "기업회원", "2022-12-01 00:00:00","2022-12-25 23:59:59");
        productService.registerProduct(4L, "쌤소나이트", "백팩2", 1200000,
                "일반회원", "2022-06-01 00:00:00","2022-06-30 23:59:59");
        productService.registerProduct(4L, "만다라니덕", "백팩3", 2000000,
                "일반회원", "2022-06-10 00:00:00","2022-06-30 23:59:59");
        productService.registerProduct(4L, "노스페이스", "백팩4", 3500000,
                "일반회원", "2022-01-01 00:00:00","2022-12-31 23:59:59");

        productService.registerProduct(5L, "맥북프로", "M1Pro 16Inch", 4000000,
                "일반회원", "2022-01-01 00:00:00","2022-12-31 23:59:59");

        productService.registerProduct(3L, "IWC", "시계4", 310000000,
                "기업회원", "2022-06-10 00:00:00","2022-06-30 23:59:59");

        productService.registerProduct(3L, "까르띠에", "시계5", 150000000,
                "기업회원", "2022-06-10 00:00:00","2022-06-30 23:59:59");

    }

    private void insertPromotions() {
        //FLAT: 정액제, FIXED: 정률제
        //1
        promotionService.registerPromotion(1L, "프로모션1", "여름할인", "FLAT",
                100000000, "2022-06-01 00:00:00", "2022-06-30 00:00:00");
        //2
        promotionService.registerPromotion(1L, "프로모션2", "여름할인", "FLAT",
                20000, "2022-06-01 00:00:00", "2022-06-30 00:00:00");
        //3
        promotionService.registerPromotion(2L, "프로모션3", "여름할인", "FIXED",
                30, "2022-06-01 00:00:00", "2022-06-30 00:00:00");
        //4
        promotionService.registerPromotion(2L, "프로모션4", "여름할인", "FLAT",
                40000, "2022-06-01 00:00:00", "2022-06-30 00:00:00");
        //5
        promotionService.registerPromotion(3L, "프로모션5", "여름할인", "FIXED",
                20, "2022-06-01 00:00:00", "2022-06-30 00:00:00");
        //6
        promotionService.registerPromotion(3L, "프로모션6", "여름할인", "FIXED",
                30, "2022-06-01 00:00:00", "2022-06-30 00:00:00");
        //7
        promotionService.registerPromotion(4L, "프로모션7", "여름할인", "FLAT",
                600000, "2022-06-01 00:00:00", "2022-06-30 00:00:00");
        //8
        promotionService.registerPromotion(4L, "프로모션8", "여름할인", "FIXED",
                40, "2022-06-01 00:00:00", "2022-06-30 00:00:00");

        // 시간 체크용
        promotionService.registerPromotion(5L, "프로모션9", "미래할인1", "FLAT",
                5000, "2023-01-01 00:00:00", "2023-12-31 00:00:00");
        promotionService.registerPromotion(5L, "프로모션10", "미래할인2", "FIXED",
                80, "2023-01-01 00:00:00", "2023-12-31 00:00:00");

        promotionService.registerPromotion(4L, "프로모션11", "여름할인", "FIXED",
                40, "2022-06-01 00:00:00", "2022-06-30 00:00:00");
        promotionService.registerPromotion(4L, "프로모션12", "여름할인", "FLAT",
                500000000, "2022-06-01 00:00:00", "2022-06-30 00:00:00");
        promotionService.registerPromotion(4L, "프로모션13", "여름할인", "FLAT",
                400000000, "2022-06-01 00:00:00", "2022-06-30 00:00:00");

    }

    private void insertAppliedPromotions() {
        appliedPromotionService.applyPromotionToProduct(1L, 1L, new ArrayList<>(Arrays.asList(1L, 3L, 9L, 7L)));
        appliedPromotionService.applyPromotionToProduct(1L, 2L, new ArrayList<>(Arrays.asList(10L, 12L, 14L, 16L)));
        appliedPromotionService.applyPromotionToProduct(2L, 3L, new ArrayList<>(Arrays.asList(15L, 13L, 11L, 9L)));
        appliedPromotionService.applyPromotionToProduct(2L, 4L, new ArrayList<>(Arrays.asList(4L, 8L, 12L, 16L)));
        appliedPromotionService.applyPromotionToProduct(3L, 5L, new ArrayList<>(Arrays.asList(5L, 7L, 15L, 11L)));
        appliedPromotionService.applyPromotionToProduct(3L, 6L, new ArrayList<>(Arrays.asList(13L, 5L, 8L, 7L)));
        appliedPromotionService.applyPromotionToProduct(4L, 7L, new ArrayList<>(Arrays.asList(2L, 3L, 10L, 4L)));
        appliedPromotionService.applyPromotionToProduct(4L, 8L, new ArrayList<>(Arrays.asList(2L, 6L, 9L, 4L)));
        appliedPromotionService.applyPromotionToProduct(4L, 7L, new ArrayList<>(Arrays.asList(14L, 7L, 5L, 6L)));

        appliedPromotionService.applyPromotionToProduct(4L, 11L, new ArrayList<>(List.of(17L)));
        appliedPromotionService.applyPromotionToProduct(4L, 12L, new ArrayList<>(List.of(17L)));
        appliedPromotionService.applyPromotionToProduct(4L, 13L, new ArrayList<>(List.of(17L)));
    }
}