package com.ssg.ssgproductapi.service;

import com.ssg.ssgproductapi.domain.Product;
import com.ssg.ssgproductapi.dto.ProductDTO;

import java.util.List;

public interface ProductService {

    void registerProduct(Long userId, String name, String description, int fullPrice, String authority, String start, String end);

    Product getProduct(Long productId);

    List<ProductDTO.UserProduct> getProducts(Long userId);

    void deleteProduct(Long userId, Long productId);

    void updateProduct(Long userId, Long productId, String authority, String description, String name, Integer price, String start, String end);
}
