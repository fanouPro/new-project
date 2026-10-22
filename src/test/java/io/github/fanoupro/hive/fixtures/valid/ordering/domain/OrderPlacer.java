package io.github.fanoupro.hive.fixtures.valid.ordering.domain;

import io.github.fanoupro.hive.fixtures.ddd.DomainService;
import io.github.fanoupro.hive.fixtures.valid.ordering.domain.api.PlaceOrder;
import io.github.fanoupro.hive.fixtures.valid.ordering.domain.spi.Catalog;
import io.github.fanoupro.hive.fixtures.valid.ordering.domain.spi.Orders;
import io.github.fanoupro.hive.fixtures.valid.ordering.domain.spi.Payments;

import java.util.UUID;

@DomainService
public class OrderPlacer implements PlaceOrder {

    private final Orders orders;
    private final Catalog catalog;
    private final Payments payments;

    public OrderPlacer(Orders orders, Catalog catalog, Payments payments) {
        this.orders = orders;
        this.catalog = catalog;
        this.payments = payments;
    }

    @Override
    public Order place(String sku) {
        var price = catalog.priceOf(sku).orElseThrow(() -> new UnknownProductException(sku));
        var order = orders.save(new Order(UUID.randomUUID(), sku, price));
        payments.charge(order.id(), order.total());
        return order;
    }
}
