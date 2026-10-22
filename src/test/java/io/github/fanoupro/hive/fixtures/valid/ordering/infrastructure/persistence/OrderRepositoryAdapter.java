package io.github.fanoupro.hive.fixtures.valid.ordering.infrastructure.persistence;

import io.github.fanoupro.hive.fixtures.valid.ordering.domain.Order;
import io.github.fanoupro.hive.fixtures.valid.ordering.domain.spi.Orders;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrderRepositoryAdapter implements Orders {

    private final Map<UUID, OrderEntity> table = new HashMap<>();

    @Override
    public Order save(Order order) {
        table.put(order.id(), new OrderEntity(order));
        return table.get(order.id()).toDomain();
    }
}
