package io.github.fanoupro.hive.fixtures.violations.driven_adapter_calls_its_own_api.ordering.infrastructure.persistence;

import io.github.fanoupro.hive.fixtures.violations.driven_adapter_calls_its_own_api.ordering.domain.api.PlaceOrder;
import io.github.fanoupro.hive.fixtures.violations.driven_adapter_calls_its_own_api.ordering.domain.spi.Orders;

public class OrderRepositoryAdapter implements Orders {
    private PlaceOrder placeOrder;

    @Override
    public void save(String id) {
        placeOrder.place(id);
    }
}
