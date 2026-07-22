package com.estudos.orderplatform.service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.estudos.orderplatform.domain.Product;
import com.estudos.orderplatform.dto.ProductRequestDto;
import com.estudos.orderplatform.dto.ProductResponseDto;
import com.estudos.orderplatform.exception.ResourceNotFoundException;
import com.estudos.orderplatform.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTeste {


    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Deve salvar um produto com sucesso")
    void save_ShouldReturnProductResponseDto_WhenSuccessful() {

        // Arrange (Preparação)
        ProductRequestDto requestDto = new ProductRequestDto("PROD-001", "Teclado Mecânico", 250.0);

        Product product = new Product("PROD-001", "Teclado Mecânico", 250.0);

        when(productRepository.save(any (Product.class))).thenReturn(product);

        // Act (Ação)
        ProductResponseDto responseDto = productService.save(requestDto);

        // Assert (Ve)
        assertThat(requestDto).isNotNull();
        assertThat(requestDto.sku()).isEqualTo("PROD-001");
        assertThat(requestDto.name()).isEqualTo("Teclado Mecânico");
        assertThat(responseDto.price()).isEqualTo(250.0);

        verify(productRepository, times(1)).save(any(Product.class));

    }

    @Test
    @DisplayName("Deve retornar produto por ID quando ele existir")
    void findById_ShoudReturnProductResponseDto_WhenIdExists() {

        //Arrange
        Long existingId = 1L;

        Product product = new Product("PROD-001", "Teclado Mecânico", 250.0);

        when(productRepository.findById(existingId)).thenReturn(Optional.of(product));

        // Act (Ação)

        ProductResponseDto productResponseDto = productService.findById(existingId);

        // Assert (Verificação)

        assertThat(productResponseDto).isNotNull();
        assertThat(productResponseDto.sku()).isEqualTo("PROD-001");
        assertThat(productResponseDto.name()).isEqualTo("Teclado Mecânico");
        assertThat(productResponseDto.price()).isEqualTo(250.0);

        verify(productRepository, times(1)).findById(existingId);

    }


    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID do produto não existir ")
    void findById_ShouldThrowResourceNotFoundException_WhenIdDoesNotExist() {

        //Arrange
        Long nonExistingId = 99L;

        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert

        assertThatThrownBy( () -> productService.findById(nonExistingId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Produto não encontrado com o ID: " + nonExistingId);

        verify(productRepository, times(1)).findById(nonExistingId);
    }

    @Test
    @DisplayName("Deve retornar uma página de produtos")
    void findAll_ShouldReturnPageOfProductResponseDto() {

        //Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Product product = new Product("PROD-001", "Teclado Mecânico", 250.0);
        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // Act
        Page<ProductResponseDto> result = productService.findAll(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).sku()).isEqualTo("PROD-001");

        verify(productRepository, times(1)).findAll(pageable);
    }


    @Test
    @DisplayName("Deve atualiza um produto com sucesso quando o ID existir")
    void save_ShouldReturnUpdatedProductResponseDto_WhenIdExists() {

        // Arrange
        Long existingId = 1L;

        ProductRequestDto updateDto = new ProductRequestDto("PROD-001-UPDATED", "Teclado RGB", 300.0);

        Product existingProduct = new Product("PROD-001-UPDATED", "Teclado Mecânico", 250.0);

        when(productRepository.findById(existingId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        // Assert
        ProductResponseDto result = productService.update(existingId, updateDto);

        // Assert

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Teclado RGB");
        assertThat(result.price()).isEqualTo(300.0);

        verify(productRepository, times(1)).findById(existingId);
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar produt inexistente")
    void save_ShouldThrowResourceNotFoundException_WhenIdDoesNotExist() {

        // Arrange
        Long notExistingId = 99L;

        ProductRequestDto notUpdateDto = new ProductRequestDto("PROD-001-UPDATED", "Teclado RGB", 300.0);

        when(productRepository.findById(notExistingId)).thenReturn(Optional.empty());

        // Act assert
        assertThatThrownBy( () -> productService.update(notExistingId, notUpdateDto))
            .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, times(1)).findById(notExistingId);
        verify(productRepository, never()).save(any());


    }



}