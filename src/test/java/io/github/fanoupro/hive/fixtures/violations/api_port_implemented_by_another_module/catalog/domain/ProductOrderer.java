package io.github.fanoupro.hive.fixtures.violations.api_port_implemented_by_another_module.catalog.domain;

import io.github.fanoupro.hive.fixtures.violations.api_port_implemented_by_another_module.ordering.domain.api.PlaceOrder;

public class ProductOrderer implements PlaceOrder {
    @Override
    public void place(String sku) {
    }
}
