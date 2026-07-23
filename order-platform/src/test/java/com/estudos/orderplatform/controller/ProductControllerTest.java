package com.estudos.orderplatform.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.estudos.orderplatform.dto.ProductRequestDto;
import com.estudos.orderplatform.dto.ProductResponseDto;
import com.estudos.orderplatform.exception.ResourceNotFoundException;
import com.estudos.orderplatform.service.ProductService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@WebMvcTest(ProductController.class)
public class ProductControllerTest {

  @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    // Método utilitário que busca um nó/chave específico dentro do arquivo JSON de fixtures
    private String readJsonFixture(String fileName, String nodeKey) throws IOException {
        File file = new File("src/test/resources/json/" + fileName);
        JsonNode rootNode = objectMapper.readTree(file);
        JsonNode targetNode = rootNode.get(nodeKey);
        
        if (targetNode == null) {
            throw new IllegalArgumentException("Chave '" + nodeKey + "' não encontrada no arquivo " + fileName);
        }
        
        return objectMapper.writeValueAsString(targetNode);
    }

    @Test
    @DisplayName("Deve criar um produto e retornar status 201 Created extraindo o nó (validProduct)")
    void createProduct_ShouldReturnCreated_WhenValidData() throws Exception {

        // Arrange
        String requestJson = readJsonFixture("product-fixtures.json", "validProduct");
        ProductResponseDto responseDto = new ProductResponseDto(1L, "PROD-001", "Teclado RGB", 250.0);

        when(productService.save(any(ProductRequestDto.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.sku").value("PROD-001"))
                .andExpect(jsonPath("$.name").value("Teclado RGB"))
                .andExpect(jsonPath("$.price").value(250.0));        

    }

    @Test
    @DisplayName("Deve retornar 200 ok com a página de produtos ao buscar todos")
    void getAllProduct_ShouldReturnOk() throws Exception {

        //Arrange
        ProductResponseDto productDto =new ProductResponseDto(1L, "PROD-001", "Monitor", 100.0);
        Page<ProductResponseDto> page = new PageImpl<>(List.of(productDto));

        when(productService.findAll(any(Pageable.class))).thenReturn(page);

        // Act e Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Monitor"));

    }

    @Test
    @DisplayName("Deve retornar 200 OK e o produto ao buscar por ID existente")
    void getProductById_ShoudReturnOK_WhenIdExists() throws Exception {
        // Arrange
        Long existingId = 1L;

        ProductResponseDto responseDto = new ProductResponseDto(1L, "PROD-001", "Monitor", 1200.0);

        when(productService.findById(existingId)).thenReturn(responseDto);

        //Act & Act

        mockMvc.perform(get("/api/products/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.name").value("Monitor"));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando buscar por ID inexistente")
    void getProductById_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        Long nonExistId = 99L;

        when(productService.findById(nonExistId))
                .thenThrow(new ResourceNotFoundException("Produto não encontrado com o ID: " + nonExistId));

        // Act & Assert
        mockMvc.perform(get("/api/products/{id}", nonExistId))
                .andExpect(status().isNotFound());

    }




}