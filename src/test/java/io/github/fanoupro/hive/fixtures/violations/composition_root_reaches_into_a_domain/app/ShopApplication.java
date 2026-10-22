package io.github.fanoupro.hive.fixtures.violations.composition_root_reaches_into_a_domain.app;

import io.github.fanoupro.hive.fixtures.violations.composition_root_reaches_into_a_domain.ordering.domain.OrderPlacer;
import io.github.fanoupro.hive.fixtures.violations.composition_root_reaches_into_a_domain.ordering.domain.api.PlaceOrder;

public class ShopApplication {
    PlaceOrder placeOrder() {
        return new OrderPlacer();
    }
}
