package com.estudos.orderplatform.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.estudos.orderplatform.dto.ProductRequestDto;
import com.estudos.orderplatform.dto.ProductResponseDto;
import com.estudos.orderplatform.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService){

        this.productService = productService;

    }


    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody @Valid ProductRequestDto requestDto ) {

        ProductResponseDto savedProduct = productService.save(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll()); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
                           
        return ResponseEntity.ok(productService.findById(id));
    }


    @PutMapping("/{id}")    
    public ResponseEntity<ProductResponseDto> updateProduct(
        @PathVariable Long id,
        @RequestBody @Valid ProductRequestDto requestDto) {

            return ResponseEntity.ok(productService.update(id, requestDto));
    }    

}