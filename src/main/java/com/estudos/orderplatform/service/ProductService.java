package com.estudos.orderplatform.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.estudos.orderplatform.domain.Product;
import com.estudos.orderplatform.dto.ProductRequestDto;
import com.estudos.orderplatform.dto.ProductResponseDto;
import com.estudos.orderplatform.exception.ResourceNotFoundException;
import com.estudos.orderplatform.repository.ProductRepository;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDto save(ProductRequestDto requestDto) {
        log.info("Persistindo produto sku={}, name={}", requestDto.sku(), requestDto.name());
        Product product = new Product(
            requestDto.sku(),
            requestDto.name(),
            requestDto.price()
        );

        Product savedProduct = productRepository.save(product);
        log.info("Produto persistido. id={}", savedProduct.getId());
        return new ProductResponseDto(savedProduct);
    }
    
    public Page<ProductResponseDto> findAll(Pageable pageable){
        return productRepository.findAll(pageable)
            .map(ProductResponseDto::new);
    }

    public ProductResponseDto findById(Long id) {

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " +id));

        return new ProductResponseDto(product);

    }

    public ProductResponseDto update(Long id, ProductRequestDto requestDto) {

        Product product = productRepository.findById(id)
        
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

        product.setSku(requestDto.sku());
        product.setName(requestDto.name());
        product.setPrice(requestDto.price());

        Product updateProduct = productRepository.save(product);


        return new ProductResponseDto(updateProduct);
    }

}