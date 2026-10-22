package io.github.fanoupro.hive.fixtures.violations.spi_port_implemented_in_domain.ordering.domain;

import io.github.fanoupro.hive.fixtures.violations.spi_port_implemented_in_domain.ordering.domain.spi.Orders;

public class InMemoryOrders implements Orders {
    @Override
    public void save(String id) {
    }
}
