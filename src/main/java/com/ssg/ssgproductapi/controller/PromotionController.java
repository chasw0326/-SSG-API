package com.ssg.ssgproductapi.controller;


import com.ssg.ssgproductapi.domain.Promotion;
import com.ssg.ssgproductapi.security.dto.AuthUserDTO;
import com.ssg.ssgproductapi.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/promotion")
@RestController
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    public List<Promotion> getMyPromotions(@AuthenticationPrincipal AuthUserDTO authUser) {
        return promotionService.getMyPromotions(authUser.getId());
    }
}
