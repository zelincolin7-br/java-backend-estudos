package com.estudos.orderplatform.javalabs.week1.day1;

import com.estudos.orderplatform.javalabs.week1.model.LabProduct;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Semana 1 — Dia 1: Lambda e interfaces funcionais.
 * Implemente os métodos marcados com TODO.
 * Rode: mvn test -Dtest=LambdaLabDay1Test
 */
public class LambdaLabDay1 {

    /**
     * TODO 1: Crie um Predicate&lt;LabProduct&gt; que retorna true quando o produto está ativo.
     * Use o predicate em um loop for para filtrar a lista.
     */
    public List<LabProduct> filterActiveProducts(List<LabProduct> products) {
       // List<LabProduct> result = new ArrayList<>();
        Predicate<LabProduct> isActive = p -> p.active();
        return products.stream().filter(isActive).toList();

        //for (LabProduct product : products) {
       //     if (isActive.test(product)) {
       //         result.add(product);
       //     }
        //}
      //  return result;
    }

    /**
     * TODO 2: Use LabProduct::name como Function e um loop for para montar a lista de nomes.
     */
    public List<String> extractProductNames(List<LabProduct> products) {

        return products.stream().map(LabProduct::name).toList();

       
       /*  List<String> result = new ArrayList<>();
        Function<LabProduct, String> toName = LabProduct::name;
        for (LabProduct product  : products) {
            result.add(toName.apply(product));
        }

        return result;*/
    }

    /**
     * TODO 3: Crie um Comparator com lambda que ordena por preço decrescente (maior primeiro).
     * Versão com Stream — mesma regra de comparação, outra forma de aplicar.
     */
    public List<LabProduct> sortByPriceDescending(List<LabProduct> products) {
        Comparator<LabProduct> byPrice = (p1, p2) -> Double.compare(p2.price(), p1.price());

        return products.stream()
                .sorted(byPrice)
                .toList();
    }

    /**
     * TODO 4 (amanhã): Produtos com preço &gt;= minPrice.
     * Receba um Predicate&lt;LabProduct&gt; já montado ou monte dentro do método.
     */
    public List<LabProduct> filterByMinPrice(List<LabProduct> products, double minPrice) {
        throw new UnsupportedOperationException("TODO 4: implemente amanhã");
    }

    /**
     * TODO 5 (amanhã): Substitua a classe anônima por lambda.
     * O Runnable deve incrementar o contador passado (use array int[1] ou AtomicInteger).
     */
    public int runAndCount(Runnable action, int times) {
        throw new UnsupportedOperationException("TODO 5: implemente amanhã");
    }
}
