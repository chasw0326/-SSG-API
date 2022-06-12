package com.ssg.ssgproductapi.controller;


import com.ssg.ssgproductapi.dto.PromotionReqDTO;
import com.ssg.ssgproductapi.dto.PromotionRespDTO;
import com.ssg.ssgproductapi.security.dto.AuthUserDTO;
import com.ssg.ssgproductapi.service.ApplyPromotionService;
import com.ssg.ssgproductapi.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 컨트롤러는 따로 깃허브 위키에 자세하게 문서화 하였습니다.
 */
@RequestMapping("/api/promotion")
@RestController
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;
    private final ApplyPromotionService applyPromotionService;


    @GetMapping
    public List<PromotionRespDTO.MyPromotion> getMyPromotions(@AuthenticationPrincipal AuthUserDTO authUser) {
        return promotionService.getMyPromotions(authUser.getId());
    }

    @PostMapping
    public void registerPromotion(@AuthenticationPrincipal AuthUserDTO authUser,
                                  @RequestBody @Valid PromotionReqDTO.Register registerDTO) {

        promotionService.registerPromotion(
                authUser.getId(),
                registerDTO.getName(),
                registerDTO.getDescription(),
                registerDTO.getPolicy(),
                registerDTO.getDiscountRate(),
                registerDTO.getStart(),
                registerDTO.getEnd()
        );
    }

    @PostMapping("/{promotionId}")
    public void applyPromotion(@AuthenticationPrincipal AuthUserDTO authUser,
                               @PathVariable Long promotionId,
                               @RequestBody PromotionReqDTO.Apply applyDTO) {

        applyPromotionService.applyPromotionToProduct(
                authUser.getId(),
                promotionId,
                applyDTO.getProductIds()
        );
    }

    @DeleteMapping("/{promotionId}")
    public void deletePromotion(@AuthenticationPrincipal AuthUserDTO authUser,
                               @PathVariable Long promotionId) {

        applyPromotionService.deletePromotion(
                authUser.getId(),
                promotionId
        );
    }

    @DeleteMapping("/{promotionId}/product/{productId}")
    public void deleteAppliedPromotion(@AuthenticationPrincipal AuthUserDTO authUser,
                                       @PathVariable Long promotionId,
                                       @PathVariable Long productId){

        applyPromotionService.deleteAppliedPromotion(
                authUser.getId(),
                productId,
                promotionId);
    }
}
