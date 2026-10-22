package io.github.fanoupro.hive.fixtures.violations.domain_service_not_annotated.ordering.domain;

import io.github.fanoupro.hive.fixtures.violations.domain_service_not_annotated.ordering.domain.api.PlaceOrder;

public class OrderPlacer implements PlaceOrder {
    @Override
    public void place(String sku) {
    }
}
