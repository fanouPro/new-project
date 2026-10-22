package io.github.fanoupro.hive.fixtures.valid.ordering.domain.stubs;

import io.github.fanoupro.hive.fixtures.ddd.Stub;
import io.github.fanoupro.hive.fixtures.valid.ordering.domain.Order;
import io.github.fanoupro.hive.fixtures.valid.ordering.domain.spi.Orders;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Stub
public class InMemoryOrders implements Orders {

    private final Map<UUID, Order> orders = new HashMap<>();

    @Override
    public Order save(Order order) {
        orders.put(order.id(), order);
        return order;
    }
}
