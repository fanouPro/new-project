package io.github.fanoupro.hive.fixtures.valid.ordering.domain.spi;

import io.github.fanoupro.hive.fixtures.valid.ordering.domain.Order;

public interface Orders {
    Order save(Order order);
}
