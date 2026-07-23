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

import com.estudos.orderplatform.domain.Order;
import com.estudos.orderplatform.domain.OrderStatus;
import com.estudos.orderplatform.domain.Product;
import com.estudos.orderplatform.dto.OrderItemRequestDto;
import com.estudos.orderplatform.dto.OrderRequestDto;
import com.estudos.orderplatform.dto.OrderResponseDto;
import com.estudos.orderplatform.exception.ResourceNotFoundException;
import com.estudos.orderplatform.repository.OrderRepository;
import com.estudos.orderplatform.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock    
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName(" Deve criar um pedido com sucesso e calcular o valor total corretamente ")
    void createOrder_ShouldReturnOrderResponseDto_WhenProductsExist() {

        // Arrange
        Product product1 = new Product("PROD-001", "Teclado", 100.0) ;
        Product product2 = new Product("PROD-002", "Mouse", 50.0);

        OrderItemRequestDto item1Dto = new OrderItemRequestDto(1L, 2);
        OrderItemRequestDto item2Dto = new OrderItemRequestDto(2L, 1);

        OrderRequestDto orderRequestDto = new OrderRequestDto(List.of(item1Dto, item2Dto));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));

        // Mock do save para simular o pedido persistido com ID e calculo
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);

            return savedOrder;
        });

        // Act
        OrderResponseDto responseDto = orderService.save(orderRequestDto);

        // Assert
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(responseDto.total()).isEqualTo(250.0);
        assertThat(responseDto.items()).hasSize(2);

        verify(productRepository,  times(1)).findById(1L);
        verify(productRepository, times(1)).findById(2L);
        verify(orderRepository, times(1)).save(any(Order.class));        

    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar criar pedido com produto inexistente")
    void createOrder_ShouldThrowResourceNotFoundException_WhenProductDoesNotExist() {
        // Arrange
        OrderItemRequestDto itemDto = new OrderItemRequestDto(99L, 1);
        OrderRequestDto orderRequestDto = new OrderRequestDto(List.of(itemDto));

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
       assertThatThrownBy( () -> orderService.save(orderRequestDto))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Produto não encontrado com o ID: 99");

        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar pedido por ID quando ele existir")
    void findById_ShouldReturnOrderResponseDto_WhenIdExists() {

        // Arrange
        Long existingId = 1L;

        Order order = new Order();

        when(orderRepository.findById(existingId)).thenReturn(Optional.of(order));

        // Act
        OrderResponseDto responseDto = orderService.findById(existingId);

        // Assert

        assertThat(responseDto).isNotNull();
        verify(orderRepository, times(1)).findById(existingId);

    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID do pedido não existe")
    void findById_ShouldThrowResourceNotFoundException_WhenIdDoesNotExist() {

        // Arrange
        Long nonExistingId = 99L;

        when(orderRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & act
        assertThatThrownBy( () -> orderService.findById(nonExistingId))
            .isInstanceOf(ResourceNotFoundException.class);

        verify(orderRepository, times(1)).findById(nonExistingId);

    }


    @Test
    @DisplayName("Deve retornar uma página de pedidos")
    void findAll_ShouldReturnPageOfOrderResponseDto() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Order order = new Order();
        Page<Order> orderPage = new PageImpl<>(List.of(order));

        when(orderRepository.findAll(pageable)).thenReturn(orderPage);

        // Act
        Page<OrderResponseDto> result = orderService.findAll(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(orderRepository, times(1)).findAll(pageable);        


    }






}