package io.github.fanoupro.hive.fixtures.violations.api_port_implemented_in_infrastructure.ordering.infrastructure.controller;

import io.github.fanoupro.hive.fixtures.violations.api_port_implemented_in_infrastructure.ordering.domain.api.PlaceOrder;

public class OrderController implements PlaceOrder {
    @Override
    public void place(String sku) {
    }
}
