package com.ssg.ssgproductapi.service;

import com.ssg.ssgproductapi.domain.Product;
import com.ssg.ssgproductapi.dto.ProductRespDTO;
import com.ssg.ssgproductapi.exception.custom.ForbiddenException;
import com.ssg.ssgproductapi.exception.custom.InvalidArgsException;
import com.ssg.ssgproductapi.exception.custom.NotFoundException;
import com.ssg.ssgproductapi.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ProductServiceTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("종료시점이 시작시점보다 빠른 경우 예외던짐")
    @Test
    void Should_ThrowException_WhenEndAtIsBeforeThenStartAt() {
        String start = "2022-01-01 12:12:12";
        String end = "2021-01-01 12:12:12";
        Throwable ex = assertThrows(InvalidArgsException.class, () ->
                productService.registerProduct(
                        1L, "갤럭시S22케이스", "실리콘케이스",
                        20000, "일반회원", start, end)
        );
        assertEquals("종료시점이 시작시점보다 빠릅니다.", ex.getMessage());

    }

    @DisplayName("정상적으로 상품등록")
    @Test
    void Should_RegisterProduct() {
        String start = "2022-01-01 12:12:12";
        String end = "2022-12-12 12:12:12";
        Long id = productService.registerProduct(1L, "웰치스", "제로칼로리 콜라맛",
                1000, "일반회원", start, end);

        Product product = productRepository.getById(id);
        assertEquals(id, product.getId());
        assertEquals("웰치스", product.getName());
    }

    @DisplayName("조회가능한 모든 상품 가져오기")
    @Test
    void Should_GetProducts() {
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id").descending());

        // 일반회원
        List<ProductRespDTO.UserProduct> productList = productService.getNormalProducts(1L, pageable);
        for (ProductRespDTO.UserProduct product : productList) {
            assertEquals(product.getUserType(), "일반회원");
        }

        // 기업회원
        List<String> userTypeTemp = new ArrayList<>();
        productList = productService.getNormalProducts(3L, pageable);
        for (ProductRespDTO.UserProduct product : productList) {
            assertEquals(product.getUserType(), "일반회원");
        }
    }

    @DisplayName("기업용 상품들만 가져오기")
    @Test
    void Should_GetEnterpriseProducts() {
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id").descending());
        List<ProductRespDTO.UserProduct> productList = productService.getEnterpriseProducts(3L, pageable);
        for (ProductRespDTO.UserProduct product : productList) {
            assertEquals(product.getUserType(), "기업회원");
        }
    }

    @DisplayName("삭제 테스트")
    @Test
    void Should_DeleteProduct() {
        productService.deleteProduct(2L, 1L);
        Throwable ex = assertThrows(NotFoundException.class, () ->
                productService.getProduct(1L)
        );
    }

    @DisplayName("다른사람이 삭제시도하면 예외")
    @Test
    void Should_ThrowException_WhenTryDeleteByStranger() {
        Throwable ex = assertThrows(ForbiddenException.class, () ->
                productService.deleteProduct(1L, 1L)
        );
    }

    @DisplayName("다른사람이 수정시도하면 예외")
    @Test
    void Should_ThrowException_WhenTryUpdateByStranger() {
        Throwable ex = assertThrows(ForbiddenException.class, () ->
                productService.updateProduct(1L, 1L, null, null, "새로운이름",
                        null, null, null)
        );
    }

    @DisplayName("설명만 수정")
    @Test
    void Should_UpdateOnlyDescription() {
        Product preProduct = productService.getProduct(1L);
        final String name = preProduct.getName();
        Long id = productService.updateProduct(2L, 1L, null, "새로운 설명", null,
                null, null, null);
        Product product = productRepository.getById(id);
        assertEquals(name, product.getName());
        assertEquals("새로운 설명", product.getDescription());
    }

    @DisplayName("권한만 수정")
    @Test
    void Should_UpdateOnlyAuthority() {
        Product preProduct = productService.getProduct(1L);
        final String name = preProduct.getName();
        Long id = productService.updateProduct(2L, 1L, "기업회원", null, null,
                null, null, null);
        Product product = productRepository.getById(id);
        assertEquals("기업회원", product.getAuthority().toString());
        assertEquals(name, product.getName());
    }

    @DisplayName("이름만 수정")
    @Test
    void Should_UpdateOnlyName() {
        Product preProduct = productService.getProduct(1L);
        final int price = preProduct.getFullPrice();
        Long id = productService.updateProduct(2L, 1L, null, null, "새로운이름",
                null, null, null);
        Product product = productRepository.getById(id);
        assertEquals("새로운이름", product.getName());
        assertEquals(price, product.getFullPrice());

    }

    @DisplayName("가격만 수정")
    @Test
    void Should_UpdateOnlyPrice() {
        Product preProduct = productService.getProduct(1L);
        final String name = preProduct.getName();
        Long id = productService.updateProduct(2L, 1L, null, null, null,
                3000, null, null);
        Product product = productRepository.getById(id);
        assertEquals(name, product.getName());
        assertEquals(3000, product.getFullPrice());
    }

    @DisplayName("시작시점만 수정")
    @Test
    void Should_UpdateOnlyStartAt() {
        Product preProduct = productService.getProduct(1L);
        final String name = preProduct.getName();
        Long id = productService.updateProduct(2L, 1L, null, null, null,
                null, "2000-01-01 12:12:12", null);
        Product product = productRepository.getById(id);
        assertEquals(name, product.getName());
        assertEquals(LocalDateTime.parse("2000-01-01 12:12:12",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), product.getStartedAt());

    }

    @DisplayName("종료시점만 수정")
    @Test
    void Should_UpdateOnlyEndAt() {
        Product preProduct = productService.getProduct(1L);
        final String name = preProduct.getName();
        Long id = productService.updateProduct(2L, 1L, null, null, null,
                null, null, "2999-01-01 12:12:12");
        Product product = productRepository.getById(id);
        assertEquals(name, product.getName());
        assertEquals(LocalDateTime.parse("2999-01-01 12:12:12",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), product.getEndAt());

    }

    @DisplayName("시작시점이 종료시점보다 빠르게 수정하면 예외")
    @Test
    void Should_ThrowException_WhenWrongPeriod() {
        Throwable ex = assertThrows(InvalidArgsException.class, () ->
                productService.updateProduct(2L, 1L, null, null, null,
                        null, "2022-06-12 12:12:12", "2022-06-11 12:12:12")
        );
    }

    @DisplayName("가격 수정했을때 더 할인율이 높은 프로모션이 적용되는지")
    @Test
    void Should_ChangeAppliedPromotion_WhenUpdatePrice() {
        // 2억짜리 정액제로 1억 정률제로 40% 프로모션이 적용되어있음
        // 현재는 1억 정액제가 가장 할인율이 높음
        // 4억으로 바꾸면 40% 프로모션이 할인율이 더 높아짐
        Product preProduct = productService.getProduct(5L);
        assertEquals( 7L, preProduct.getPromotion().getId());
        Long id = productService.updateProduct(3L, 9L, null, null, null,
                400000000, null, null);
        Product product = productRepository.getById(id);
        assertEquals(8L, product.getPromotion().getId());
    }
}
