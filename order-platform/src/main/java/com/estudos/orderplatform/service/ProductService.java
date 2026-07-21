package com.estudos.orderplatform.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.estudos.orderplatform.domain.Product;
import com.estudos.orderplatform.dto.ProductRequestDto;
import com.estudos.orderplatform.dto.ProductResponseDto;
import com.estudos.orderplatform.exception.ResourceNotFoundException;
import com.estudos.orderplatform.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    // Injeção de dependência via construtor (Boa prática do Spring)
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDto save(ProductRequestDto requestDto) {
        // 1. Converte o DTO (record) para a entidade JPA
        Product product = new Product(
            requestDto.sku(),
            requestDto.name(),
            requestDto.price()
        );

        // 2. Salva no banco de dados através do repository
        Product savedProduct = productRepository.save(product);
        return new ProductResponseDto(savedProduct);
    }
    
    public List<ProductResponseDto> findAll(){
        return productRepository.findAll()
            .stream()
            .map(ProductResponseDto::new) 
            .toList();
    }

    public ProductResponseDto findById(Long id) {

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " +id));

        return new ProductResponseDto(product);

    }

    public ProductResponseDto update(Long id, ProductRequestDto requestDto) {

        Product product = productRepository.findById(id)
        
            .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));

        product.setSku(requestDto.sku());
        product.setName(requestDto.name());
        product.setPrice(requestDto.price());

        Product updateProduct = productRepository.save(product);


        return new ProductResponseDto(updateProduct);
    }

}