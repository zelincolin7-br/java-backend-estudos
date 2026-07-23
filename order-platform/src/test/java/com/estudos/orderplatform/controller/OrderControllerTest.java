package com.estudos.orderplatform.controller;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
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

import com.estudos.orderplatform.domain.OrderStatus;
import com.estudos.orderplatform.dto.OrderItemResponseDto;
import com.estudos.orderplatform.dto.OrderRequestDto;
import com.estudos.orderplatform.dto.OrderResponseDto;
import com.estudos.orderplatform.exception.ResourceNotFoundException;
import com.estudos.orderplatform.service.OrderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

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
    @DisplayName("Deve criar um pedido e retornar status 201 Created extraindo 'validOrder'")
    void createOrder_ShouldReturnCreated_WhenValidData() throws Exception {
        // Arrange
        String requestJson = readJsonFixture("order-fixtures.json", "validOrder");

        // OrderItemResponseDto com os 6 campos: (id, productId, productName, price, quantity, subTotal)
        OrderItemResponseDto itemDto = new OrderItemResponseDto(
                1L,
                10L,
                "Teclado RGB",
                250.0,
                2,
                500.0
        );

        // OrderResponseDto com a ordem e tipos exatos do record: (id, createAt [Instant], status, total, items)
        OrderResponseDto responseDto = new OrderResponseDto(
                1L,
                Instant.now(),
                OrderStatus.PENDING,
                500.0,
                List.of(itemDto)
        );

        when(orderService.save(any(OrderRequestDto.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.total").value(500.0))
                .andExpect(jsonPath("$.items[0].productName").value("Teclado RGB"));
    }

    @Test
    @DisplayName("Deve retornar 200 OK com a página de pedidos ao buscar todos")
    void getAllOrders_ShouldReturnOk() throws Exception {
        // Arrange
        OrderResponseDto orderDto = new OrderResponseDto(
                1L,
                Instant.now(),
                OrderStatus.PENDING,
                250.0,
                List.of()
        );
        Page<OrderResponseDto> page = new PageImpl<>(List.of(orderDto));

        when(orderService.findAll(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("Deve retornar 200 OK e o pedido ao buscar por ID existente")
    void getOrderById_ShouldReturnOk_WhenIdExists() throws Exception {
        // Arrange
        Long existingId = 1L;
        OrderResponseDto responseDto = new OrderResponseDto(
                existingId,
                Instant.now(),
                OrderStatus.PENDING,
                250.0,
                List.of()
        );

        when(orderService.findById(existingId)).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(get("/api/orders/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando buscar por ID inexistente")
    void getOrderById_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        // Arrange
        Long nonExistingId = 99L;
        when(orderService.findById(nonExistingId))
                .thenThrow(new ResourceNotFoundException("Pedido não encontrado com o ID: " + nonExistingId));

        // Act & Assert
        mockMvc.perform(get("/api/orders/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }
}