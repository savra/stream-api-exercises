package space.gavinklfong.demo.streamapi;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import space.gavinklfong.demo.streamapi.models.Customer;
import space.gavinklfong.demo.streamapi.models.Order;
import space.gavinklfong.demo.streamapi.models.Product;
import space.gavinklfong.demo.streamapi.repos.CustomerRepo;
import space.gavinklfong.demo.streamapi.repos.OrderRepo;
import space.gavinklfong.demo.streamapi.repos.ProductRepo;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@DataJpaTest
public class StreamApiTest {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ProductRepo productRepo;

    @Test
    @DisplayName("Obtain a list of product with category = \"Books\" and price > 100")
    public void exercise1() {
        long startTime = System.currentTimeMillis();
        java.util.List<Product> result = productRepo.findAll()
                .stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("Book")
                        && product.getPrice() > 100)
                .collect(java.util.stream.Collectors.toList());

        long endTime = System.currentTimeMillis();

        log.info(String.format("exercise 1 - execution time: %1$d ms", (endTime - startTime)));
        result.forEach(p -> log.info(p.toString()));
    }

    @Test
    @DisplayName("Obtain a list of product with category = \"Books\" and price > 100 (using Predicate chaining for filter)")
    public void exercise1a() {
        java.util.function.Predicate<Product> predicate1 = product -> product.getCategory().equalsIgnoreCase("Books");
        java.util.function.Predicate<Product> predicate2 = product -> product.getPrice() > 100;

        long startTime = System.currentTimeMillis();
        java.util.List<Product> result = productRepo.findAll()
                .stream()
                .filter(predicate1.and(predicate2))
                .collect(Collectors.toList());
        long endTime = System.currentTimeMillis();

        log.info(String.format("exercise 1a - execution time: %1$d ms", (endTime - startTime)));
        result.forEach(p -> log.info(p.toString()));
    }


    @Test
    @DisplayName("Obtain a list of product with category = \"Books\" and price > 100 (using BiPredicate for filter)")
    public void exercise1b() {
        java.util.function.BiPredicate<Product, String> filter = ((product, category) ->
                product.getCategory().equalsIgnoreCase(category));

        long startTime = System.currentTimeMillis();
        java.util.List<Product> result = productRepo.findAll()
                .stream().filter(product -> filter.test(product, product.getCategory())
                        && product.getPrice() > 100).collect(java.util.stream.Collectors.toList());

        long endTime = System.currentTimeMillis();

        log.info(String.format("exercise 1b - execution time: %1$d ms", (endTime - startTime)));
        result.forEach(p -> log.info(p.toString()));
    }

    @Test
    @DisplayName("Obtain a list of order with product category = \"Baby\"")
    public void exercise2() {
        long startTime = System.currentTimeMillis();
        List<Order> result = orderRepo.findAll()
                .stream()
                .filter(o ->
                        o.getProducts()
                                .stream()
                                .anyMatch(p -> p.getCategory().equalsIgnoreCase("Baby"))
                )
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();

        log.info(String.format("exercise 2 - execution time: %1$d ms", (endTime - startTime)));
        result.forEach(o -> log.info(o.toString()));

    }

    @Test
    @DisplayName("23")
    public void exercise22() {
        long startTime = System.currentTimeMillis();

        java.util.List<Order> result = productRepo.findAll()
                .stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("Baby"))
                .flatMap(product -> product.getOrders().stream())
                .collect(Collectors.toList());
        ;

        long endTime = System.currentTimeMillis();

        log.info(String.format("exercise 2 - execution time: %1$d ms", (endTime - startTime)));
        result.forEach(o -> log.info(o.toString()));

    }

    @Test
    @DisplayName("Obtain a list of product with category = “Toys” and then apply 10% discount\"")
    public void exercise3() {
        long startTime = System.currentTimeMillis();

        List<Product> result = productRepo.findAll()
                .stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("Toys"))
                .map(product -> product.withPrice(product.getPrice() * 0.9))
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 3 - execution time: %1$d ms", (endTime - startTime)));
        result.forEach(o -> log.info(o.toString()));

    }

    @Test
    @DisplayName("Obtain a list of products ordered by customer of tier 2 between 01-Feb-2021 and 01-Apr-2021")
    public void exercise45() {
        long startTime = System.currentTimeMillis();

        List<Product> result = orderRepo.findAll()
                .stream()
                .filter(o -> o.getCustomer().getTier() == 2)
                .filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021, 2, 1)) >= 0)
                .filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021, 4, 1)) <= 0)
                .flatMap(o -> o.getProducts().stream())
                .distinct()
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 4 - execution time: %1$d ms", (endTime - startTime)));
        result.forEach(o -> log.info(o.toString()));
    }

    @Test
    @DisplayName("Obtain a list of products ordered by customer of tier 2 between 01-Feb-2021 and 01-Apr-2021")
    public void exercise4() {
        long startTime = System.currentTimeMillis();

        var result = productRepo.findAll()
                .stream()
                .filter(product -> product.getOrders().stream().anyMatch(p -> p.getCustomer().getTier() == 2 &&
                        p.getOrderDate().compareTo(LocalDate.of(2021, 2, 1)) >= 0 &&
                        p.getOrderDate().compareTo(LocalDate.of(2021, 4, 1)) <= 0))
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 4 - execution time: %1$d ms", (endTime - startTime)));
        result.forEach(o -> log.info(o.toString()));
    }

    @Test
    @DisplayName("Get the 3 cheapest products of \"Books\" category")
    public void exercise5() {
        long startTime = System.currentTimeMillis();

        var result = productRepo.findAll().stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("Books"))
                .sorted(Comparator.comparingDouble(Product::getPrice))
                .limit(3)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 5 - execution time: %1$d ms", (endTime - startTime)));

    }

    @Test
    @DisplayName("Get the 3 most recent placed order")
    public void exercise6() {
        long startTime = System.currentTimeMillis();

        var result = orderRepo.findAll().stream()
                .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                .limit(3)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 6 - execution time: %1$d ms", (endTime - startTime)));
        result.forEach(o -> log.info(o.toString()));
    }

    @Test
    @DisplayName("Get a list of products which was ordered on 15-Mar-2021")
    public void exercise7() {
        long startTime = System.currentTimeMillis();

        List<Product> result = productRepo.findAll()
                .stream()
                .filter(product -> product.getOrders().stream().anyMatch(order -> order.getOrderDate().equals(LocalDate.of(2021, 3, 15))))
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 7 - execution time: %1$d ms", (endTime - startTime)));
        result.forEach(o -> log.info(o.toString()));
    }

    @Test
    @DisplayName("Calculate the total lump of all orders placed in Feb 2021")
    public void exercise8() {
        long startTime = System.currentTimeMillis();

        double sum = orderRepo.findAll()
                .stream()
                .filter(order -> order.getOrderDate().compareTo(LocalDate.of(2021, 2, 1)) >= 0
                        && order.getOrderDate().compareTo(order.getOrderDate().with(TemporalAdjusters.lastDayOfMonth())) <= 0)
                .flatMap(order -> order.getProducts().stream())
                .mapToDouble(Product::getPrice)
                .sum();


        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 8 - execution time: %1$d ms", (endTime - startTime)));
        log.info("Total lump sum = " + sum);
    }

    @Test
    @DisplayName("Calculate the total lump of all orders placed in Feb 2021 (using reduce with BiFunction)")
    public void exercise8a() {
        BiFunction<Double, Product, Double> accumulator = ((acc, product) -> acc + product.getPrice());

        long startTime = System.currentTimeMillis();

        double result = orderRepo.findAll()
                .stream()
                .filter(order -> order.getOrderDate().compareTo(LocalDate.of(2021, 2, 1)) >= 0
                        && order.getOrderDate().compareTo(order.getOrderDate().with(TemporalAdjusters.lastDayOfMonth())) <= 0)
                .flatMap(order -> order.getProducts().stream())
                .reduce(0D, accumulator, Double::sum);

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 8a - execution time: %1$d ms", (endTime - startTime)));
        log.info("Total lump sum = " + result);
    }

    @Test
    @DisplayName("Calculate the average price of all orders placed on 15-Mar-2021")
    public void exercise9() {
        long startTime = System.currentTimeMillis();

        double result = orderRepo.findAll()
                .stream()
                .filter(order -> order.getOrderDate().equals(LocalDate.of(2021, 3, 15)))
                .flatMap(order -> order.getProducts().stream())
                .mapToDouble(Product::getPrice)
                .average().getAsDouble();

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 9 - execution time: %1$d ms", (endTime - startTime)));
        log.info("Average = " + result);
    }

    @Test
    @DisplayName("Obtain statistics summary of all products belong to \"Books\" category")
    public void exercise10() {
        long startTime = System.currentTimeMillis();

        DoubleSummaryStatistics statistics = productRepo.findAll()
                .stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("Books"))
                .mapToDouble(Product::getPrice)
                .summaryStatistics();

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 10 - execution time: %1$d ms", (endTime - startTime)));
        log.info(String.format("count = %1$d, average = %2$f, max = %3$f, min = %4$f, sum = %5$f",
                statistics.getCount(), statistics.getAverage(), statistics.getMax(), statistics.getMin(), statistics.getSum()));

    }

    @Test
    @DisplayName("Obtain a mapping of order id and the order's product count")
    public void exercise11() {
        long startTime = System.currentTimeMillis();
        Map<Long, Integer> result = orderRepo.findAll()
                .stream()
                .collect(Collectors.toMap(Order::getId, order -> order.getProducts().size()));

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 11 - execution time: %1$d ms", (endTime - startTime)));
    }

    @Test
    @DisplayName("Obtain a data map of customer and list of orders")
    public void exercise12() {
        long startTime = System.currentTimeMillis();

        Map<Customer, List<Order>> collect = orderRepo
                .findAll()
                .stream()
                .collect(Collectors.groupingBy(Order::getCustomer));

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 12 - execution time: %1$d ms", (endTime - startTime)));
    }


    @Test
    @DisplayName("Obtain a data map of customer_id and list of order_id(s)")
    public void exercise12a() {
        long startTime = System.currentTimeMillis();

        Map<Long, List<Long>> result = orderRepo.findAll().stream()
                .collect(Collectors.groupingBy(order -> order.getCustomer().getId(),
                        Collectors.mapping(Order::getId, Collectors.toList())));

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 12a - execution time: %1$d ms", (endTime - startTime)));
    }

    @Test
    @DisplayName("Obtain a data map with order and its total price")
    public void exercise13() {
        long startTime = System.currentTimeMillis();

        Map<Order, Double> result = orderRepo.findAll()
                .stream()
                .collect(
                        Collectors.toMap(Function.identity(),
                                order -> order.getProducts()
                                        .stream()
                                        .mapToDouble(Product::getPrice)
                                        .sum())
                );

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 13 - execution time: %1$d ms", (endTime - startTime)));
        log.info(result.toString());
    }

    @Test
    @DisplayName("Obtain a data map with order and its total price (using reduce)")
    public void exercise13a() {
        long startTime = System.currentTimeMillis();

        Map<Order, Double> result = orderRepo.findAll()
                .stream()
                .collect(Collectors.toMap(Function.identity(),
                        order -> order.getProducts()
                                .stream()
                                .mapToDouble(Product::getPrice)
                                .reduce(0, Double::sum)));

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 13a - execution time: %1$d ms", (endTime - startTime)));
        log.info(result.toString());
    }

    @Test
    @DisplayName("Obtain a data map of product name by category")
    public void exercise14() {
        long startTime = System.currentTimeMillis();

        Map<String, List<String>> result = productRepo.findAll()
                .stream()
                .collect(
                        Collectors.groupingBy(Product::getCategory,
                                Collectors.mapping(Product::getName, Collectors.toList()))
                );

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 14 - execution time: %1$d ms", (endTime - startTime)));
        log.info(result.toString());
    }

    @Test
    @DisplayName("Get the most expensive product per category")
    void exercise15() {
        long startTime = System.currentTimeMillis();

        Map<String, Optional<Product>> result = productRepo.findAll()
                .stream()
                .collect(
                        Collectors.groupingBy(Product::getCategory,
                                Collectors.maxBy(Comparator.comparing(Product::getPrice)))
                );

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 15 - execution time: %1$d ms", (endTime - startTime)));
        log.info(result.toString());

    }

    @Test
    @DisplayName("Get the most expensive product (by name) per category")
    void exercise15a() {
        long startTime = System.currentTimeMillis();

        Map<String, Optional<String>> result = productRepo.findAll()
                .stream()
                .collect(Collectors.groupingBy(Product::getCategory,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(Product::getPrice)),
                                product -> product.map(Product::getName))
                ));


        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 15a - execution time: %1$d ms", (endTime - startTime)));
        log.info(result.toString());
    }
}
