package com.polykhel.quarkushop.service;

import com.polykhel.quarkushop.domain.Product;
import com.polykhel.quarkushop.domain.enums.ProductStatus;
import com.polykhel.quarkushop.dto.ProductDto;
import com.polykhel.quarkushop.repository.CategoryRepository;
import com.polykhel.quarkushop.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@Transactional
public class ProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public static ProductDto mapToDto(Product product) {
        return new ProductDto(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getStatus().name(), product.getSalesCounter(), product.getReviews().stream().map(ReviewService::mapToDto).collect(Collectors.toSet()), product.getCategory().getId());
    }

    public List<ProductDto> findAll() {
        log.debug("Request to get all Products");
        return this.productRepository.findAll().stream().map(ProductService::mapToDto).collect(Collectors.toList());
    }

    public ProductDto findById(Long id) {
        log.debug("Request to get Product : {}", id);
        return this.productRepository.findById(id).map(ProductService::mapToDto).orElse(null);
    }

    public Long countAll() {
        return this.productRepository.count();
    }

    public Long countByCategoryId(Long id) {
        return this.productRepository.countAllByCategoryId(id);
    }

    public ProductDto create(ProductDto productDto) {
        log.debug("Request to create Product : {}", productDto);
        return mapToDto(this.productRepository.save(new Product(productDto.getName(), productDto.getDescription(), productDto.getPrice(), ProductStatus.valueOf(productDto.getStatus()), productDto.getSalesCounter(), Collections.emptySet(), categoryRepository.findById(productDto.getCategoryId()).orElse(null))));
    }

    public void delete(Long id) {
        log.debug("Request to delete Product : {}", id);
        this.productRepository.deleteById(id);
    }

    public List<ProductDto> findByCategoryId(Long id) {
        return this.productRepository.findByCategoryId(id).stream().map(ProductService::mapToDto).collect(Collectors.toList());
    }
}
