package com.ssg.ssgproductapi.service;

import com.ssg.ssgproductapi.domain.Product;
import com.ssg.ssgproductapi.dto.ProductRespDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    ProductRespDTO.Product getProductDTO(Long userId, Long productId);

    List<ProductRespDTO.MyProduct> getMyProducts(Long userId, Pageable pageable);

    // 일반회원용 상품조회
    List<ProductRespDTO.UserProduct> getNormalProducts(Long userId, Pageable pageable);

    // 기업회원용 상품조회
    List<ProductRespDTO.UserProduct> getEnterpriseProducts(Long userId, Pageable pageable);

    Long registerProduct(Long userId, String name, String description, int fullPrice, String authority, String start, String end);

    Product getProduct(Long productId);

    Long updateProduct(Long userId, Long productId, String authority, String description, String name, Integer price, String start, String end);

    void deleteProduct(Long userId, Long productId);

}
