package com.estudos.orderplatform.javalabs.week1.day1;

import com.estudos.orderplatform.javalabs.week1.model.LabProduct;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Exemplos de referência — leia antes de fazer o lab.
 * Não precisa alterar este arquivo.
 */
public final class LambdaExamples {

    private LambdaExamples() {
    }

    public static void main(String[] args) {
        List<LabProduct> products = List.of(
                new LabProduct(1L, "Notebook", 3500.0, true),
                new LabProduct(2L, "Mouse", 89.90, true),
                new LabProduct(3L, "Teclado", 450.0, false)
        );

        Predicate<LabProduct> isActive = product -> product.active();
        Function<LabProduct, String> toName = LabProduct::name;
        Consumer<LabProduct> printProduct = p -> System.out.println(p.name() + " - R$ " + p.price());
        Supplier<Double> defaultMinPrice = () -> 100.0;

        System.out.println("=== Ativos ===");
        products.stream().filter(isActive).forEach(printProduct);

        System.out.println("=== Nomes ===");
        products.stream().map(toName).forEach(System.out::println);

        System.out.println("=== Preço mínimo (Supplier) ===");
        System.out.println(defaultMinPrice.get());

        System.out.println("=== Ordenado por preço (Comparator + lambda) ===");
        products.stream()
                .sorted(Comparator.comparingDouble(LabProduct::price))
                .forEach(printProduct);
    }
}
