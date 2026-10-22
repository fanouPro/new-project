package io.github.fanoupro.hive.fixtures.violations.composition_root_reaches_into_a_domain.ordering.domain;

import io.github.fanoupro.hive.fixtures.ddd.DomainService;
import io.github.fanoupro.hive.fixtures.violations.composition_root_reaches_into_a_domain.ordering.domain.api.PlaceOrder;

@DomainService
public class OrderPlacer implements PlaceOrder {
    @Override
    public void place(String sku) {
    }
}
