package com.estudos.orderplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.estudos.orderplatform.domain.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}