package com.estudos.orderplatform.javalabs;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;


public class ExercicioUmDois {

         static final String ID_SEARCHED = "1234";

        private Order findOrderById(List<Order> ordersList, String id) {
            return ordersList.stream()
                .filter(o -> id.equalsIgnoreCase(o.id()))
                .findFirst()
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
        }

        private double filtersOrders(List<Order> ordersList) {

            double totalCompleted = ordersList.stream()
                .filter(o -> "COMPLETED".equalsIgnoreCase(o.status()))
                .mapToDouble(Order::total)
                .sum();

            System.out.println("Valor total da soma das ordens: " + totalCompleted);

            Order orderFound = findOrderById(ordersList, ID_SEARCHED);
            System.out.println("Order found: " + orderFound);

            return totalCompleted;        
            
        }

    public static void main(String[] args) {
        Predicate<Order> isSuspiciousTransaction = order -> "PENDING".equalsIgnoreCase(order.status()) && order.total() > 5000.0;

        List<Order> orders = Arrays.asList(
            new Order("1234", 10000.0, "PENDING"),
            new Order("1234567890", 1000.0, "COMPLETED"),
            new Order("1234567890", 1000.0, "CANCELLED"),            
            new Order("1234567190", 1000.0, "COMPLETED"),
            new Order("1234567890", 1000.0, "CANCELLED"),
            new Order("1234567890", 1000.0, "PENDING"),
            new Order("1234567890", 1000.0, "COMPLETED"),
            new Order("1234567890", 1000.0, "CANCELLED"),
            new Order("1234567890", 1000.0, "PENDING"),
            new Order("1234567890", 1000.0, "COMPLETED"),
            new Order("1234567890", 1000.0, "CANCELLED"),
            new Order("1234567890", 15000.0, "PENDING"),
            new Order("1234567890", 1000.0, "COMPLETED"),
            new Order("1234567890", 1000.0, "CANCELLED"),
            new Order("1234567890", 1000.0, "PENDING"),
            new Order("1234567890", 1000.0, "COMPLETED"),
            new Order("1234567890", 1000.0, "CANCELLED"),
            new Order("1234567890", 1000.0, "PENDING"),
            new Order("1234567890", 1000.0, "COMPLETED"),
            new Order("1234567890", 1000.0, "CANCELLED"),
            new Order("1234567890", 1000.0, "PENDING"),
            new Order("1234567890", 1000.0, "COMPLETED"),
            new Order("1234567890", 1000.0, "CANCELLED"),
            new Order("1234567890", 1000.0, "PENDING"),
            new Order("1234567890", 1000.0, "COMPLETED"),
            new Order("1234567890", 1000.0, "CANCELLED"),
            new Order("1234567890", 1000.0, "PENDING"));

         Consumer<Order> processSuspidiousOrder = order ->
            System.out.println("Suspicious transaction: Order: " + order.id()
            + " with total: " + order.total()
            + " and status: " + order.status());

            orders.stream()
            .filter(isSuspiciousTransaction)
            .forEach(processSuspidiousOrder);

            ExercicioUmDois t = new ExercicioUmDois(); 
            t.filtersOrders(orders);

    }
}