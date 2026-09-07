package com.estudos.orderplatform.javalabs.week1.day1;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.estudos.orderplatform.javalabs.week1.model.LabProduct;


class LambdaLabDay1Test {

    private LambdaLabDay1 lab;
    private List<LabProduct> products;

    @BeforeEach
    void setUp() {
        lab = new LambdaLabDay1();
        products = List.of(
                new LabProduct(1L, "Notebook", 3500.0, true),
                new LabProduct(2L, "Mouse", 89.90, true),
                new LabProduct(3L, "Teclado", 450.0, false),
                new LabProduct(4L, "Monitor", 1200.0, true)
        );
    }

    @Test
    @DisplayName("TODO 1 - filterActiveProducts")
    void filterActiveProducts() {
        List<LabProduct> result = lab.filterActiveProducts(products);

        assertThat(result)
                .hasSize(3)
                .extracting(LabProduct::name)
                .containsExactly("Notebook", "Mouse", "Monitor");
    }

    @Test
    @DisplayName("TODO 2 - extractProductNames")
    void extractProductNames() {
        List<String> names = lab.extractProductNames(products);

        assertThat(names).containsExactly("Notebook", "Mouse", "Teclado", "Monitor");
    }

    @Test
    @DisplayName("TODO 3 - sortByPriceDescending")
    void sortByPriceDescending() {
        List<LabProduct> sorted = lab.sortByPriceDescending(products);

        assertThat(sorted)
                .extracting(LabProduct::name)
                .containsExactly("Notebook", "Monitor", "Teclado", "Mouse");
    }

/*
    @Test
    @DisplayName("TODO 4 - filterByMinPrice (amanhã)")
    void filterByMinPrice() {
        List<LabProduct> result = lab.filterByMinPrice(products, 500.0);

        assertThat(result)
                .extracting(LabProduct::name)
                .containsExactly("Notebook", "Monitor");
    }

    @Test
    @DisplayName("TODO 5 - runAndCount (amanhã)")
    void runAndCount() {
        int[] counter = {0};
        Runnable increment = () -> counter[0]++;

        int executions = lab.runAndCount(increment, 5);

        assertThat(executions).isEqualTo(5);
        assertThat(counter[0]).isEqualTo(5);
    }*/
}
