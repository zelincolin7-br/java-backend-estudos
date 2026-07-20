package com.estudos.orderplatform.javalabs;

import java.util.function.Function;

    public class DesafioDois {

        public static void main(String[] args) {

        Function<Product, Product> applayTaxAndConvertToDto = prod -> 
            new Product(prod.sku(), prod.name(), prod.price() * 1.020 );
            

        Function<Product, ProductSummaryDto> toSummary = prod ->
            new ProductSummaryDto(prod.name(), String.format("R$ %.2f", prod.price()));

        Function<Product, ProductSummaryDto> pipeline = applayTaxAndConvertToDto.andThen(toSummary);

        Product product = new Product("SKU-1", "Notebook", 1000.0);
        System.out.println(pipeline.apply(product));
        }
    }