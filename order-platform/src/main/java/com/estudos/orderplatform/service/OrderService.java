package com.estudos.orderplatform.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estudos.orderplatform.domain.Order;
import com.estudos.orderplatform.domain.OrderItem;
import com.estudos.orderplatform.domain.Product;
import com.estudos.orderplatform.dto.OrderItemRequestDto;
import com.estudos.orderplatform.dto.OrderRequestDto;
import com.estudos.orderplatform.dto.OrderResponseDto;
import com.estudos.orderplatform.exception.ResourceNotFoundException;
import com.estudos.orderplatform.repository.OrderRepository;
import com.estudos.orderplatform.repository.ProductRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponseDto save(OrderRequestDto requestDto) {
        Order order = new Order();

        for (OrderItemRequestDto itemDto : requestDto.items()) {
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + itemDto.productId()));

            OrderItem item = new OrderItem(product, order, itemDto.quantity(), product.getPrice());
            order.addItem(item);
        }

        Order savedOrder = orderRepository.save(order);
        return new OrderResponseDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(OrderResponseDto::new);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com o ID: " + id));
        return new OrderResponseDto(order);
    }
}