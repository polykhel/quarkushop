package com.polykhel.quarkushop.service;

import com.polykhel.quarkushop.domain.Category;
import com.polykhel.quarkushop.dto.CategoryDto;
import com.polykhel.quarkushop.dto.ProductDto;
import com.polykhel.quarkushop.repository.CategoryRepository;
import com.polykhel.quarkushop.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@Transactional
public class CategoryService {
    CategoryRepository categoryRepository;
    ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public static CategoryDto mapToDto(Category category, Long productsCount) {
        return new CategoryDto(category.getId(), category.getName(), category.getDescription(), productsCount);
    }

    public List<CategoryDto> findAll() {
        log.debug("Request to get all Categories");
        return this.categoryRepository.findAll().stream().map(category -> mapToDto(category, productRepository.countAllByCategoryId(category.getId()))).collect(Collectors.toList());
    }

    public CategoryDto findById(Long id) {
        log.debug("Request to get Category : {}", id);
        return this.categoryRepository.findById(id).map(category -> mapToDto(category, productRepository.countAllByCategoryId(category.getId()))).orElse(null);
    }

    public CategoryDto create(CategoryDto categoryDto) {
        log.debug("Request to create Category : {}", categoryDto);
        return mapToDto(this.categoryRepository.save(new Category(categoryDto.getName(), categoryDto.getDescription())), 0L);
    }

    public void delete(Long id) {
        log.debug("Request to delete Category : {}", id);
        log.debug("Deleting all products for the Category : {}", id);
        this.productRepository.deleteAllByCategoryId(id);
        log.debug("Deleting Category : {}", id);
        this.categoryRepository.deleteById(id);
    }

    public List<ProductDto> findProductsByCategoryId(Long id) {
        return this.productRepository.findAllByCategoryId(id).stream().map(ProductService::mapToDto).collect(Collectors.toList());
    }
}
