package io.github.fanoupro.hive.fixtures.violations.stub_outside_the_domain.ordering.infrastructure.persistence;

import io.github.fanoupro.hive.fixtures.ddd.Stub;
import io.github.fanoupro.hive.fixtures.violations.stub_outside_the_domain.ordering.domain.spi.Orders;

@Stub
public class InMemoryOrders implements Orders {
    @Override
    public void save(String id) {
    }
}
