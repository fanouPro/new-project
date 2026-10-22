package io.github.fanoupro.hive.fixtures.violations.spi_port_implemented_by_another_module.catalog.infrastructure.inproc;

import io.github.fanoupro.hive.fixtures.violations.spi_port_implemented_by_another_module.ordering.domain.spi.Orders;

public class OrdersAdapter implements Orders {
    @Override
    public void save(String id) {
    }
}
