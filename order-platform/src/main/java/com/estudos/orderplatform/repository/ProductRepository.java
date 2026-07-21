package com.estudos.orderplatform.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.estudos.orderplatform.domain.Product;  
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}