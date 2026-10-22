package io.github.fanoupro.hive.fixtures.violations.infrastructure_uses_a_domain_service.ordering.domain;

import io.github.fanoupro.hive.fixtures.ddd.DomainService;
import io.github.fanoupro.hive.fixtures.violations.infrastructure_uses_a_domain_service.ordering.domain.api.PlaceOrder;

@DomainService
public class OrderPlacer implements PlaceOrder {
    @Override
    public void place(String sku) {
    }
}
