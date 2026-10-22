package io.github.fanoupro.hive.fixtures.valid.ordering.domain.api;

import io.github.fanoupro.hive.fixtures.valid.ordering.domain.Order;

@FunctionalInterface
public interface PlaceOrder {
    Order place(String sku);
}
