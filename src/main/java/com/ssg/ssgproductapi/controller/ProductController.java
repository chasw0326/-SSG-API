package com.ssg.ssgproductapi.controller;


import com.ssg.ssgproductapi.dto.ProductReqDTO;
import com.ssg.ssgproductapi.dto.ProductRespDTO;
import com.ssg.ssgproductapi.security.dto.AuthUserDTO;
import com.ssg.ssgproductapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api/product")
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @GetMapping("/{productId}")
    public ProductRespDTO.Product getProductDTO(@AuthenticationPrincipal AuthUserDTO authUser,
                                                @PathVariable Long productId) {
        return productService.getProductDTO(
                authUser.getId(),
                productId
        );
    }

    @GetMapping("/normal")
    public List<ProductRespDTO.UserProduct> getNormalProducts(@AuthenticationPrincipal AuthUserDTO authUser,
                                                              @PageableDefault(
                                                                      size = 30,
                                                                      sort = "id",
                                                                      direction = Sort.Direction.DESC) Pageable pageable) {
        return productService.getNormalProducts(
                authUser.getId(),
                pageable
        );
    }

    @GetMapping("/enterprise")
    public List<ProductRespDTO.UserProduct> getEnterpriseProducts(@AuthenticationPrincipal AuthUserDTO authUser,
                                                                  @PageableDefault(
                                                                          size = 30,
                                                                          sort = "id",
                                                                          direction = Sort.Direction.DESC) Pageable pageable) {
        return productService.getEnterpriseProducts(
                authUser.getId(),
                pageable
        );
    }

    @PostMapping
    public void registerProduct(@AuthenticationPrincipal AuthUserDTO authUserDTO,
                                @RequestBody @Valid ProductReqDTO.Register registerDTO){

        productService.registerProduct(
                authUserDTO.getId(),
                registerDTO.getName(),
                registerDTO.getDescription(),
                registerDTO.getFullPrice(),
                registerDTO.getAuthority(),
                registerDTO.getStart(),
                registerDTO.getEnd()
        );
    }

    @PutMapping("/{productId}")
    public void updateProduct(@AuthenticationPrincipal AuthUserDTO authUser,
                              @PathVariable Long productId,
                              @RequestBody @Valid ProductReqDTO.Update updateDTO) {

        productService.updateProduct(
                authUser.getId(),
                productId,
                updateDTO.getAuthority(),
                updateDTO.getDescription(),
                updateDTO.getName(),
                updateDTO.getPrice(),
                updateDTO.getStart(),
                updateDTO.getEnd()
        );
    }

}
