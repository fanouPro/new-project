package io.github.fanoupro.hive.fixtures.valid.ordering.infrastructure.configuration;

import io.github.fanoupro.hive.fixtures.valid.ordering.domain.OrderPlacer;
import io.github.fanoupro.hive.fixtures.valid.ordering.domain.api.PlaceOrder;
import io.github.fanoupro.hive.fixtures.valid.ordering.domain.spi.Catalog;
import io.github.fanoupro.hive.fixtures.valid.ordering.domain.spi.Orders;
import io.github.fanoupro.hive.fixtures.valid.ordering.domain.spi.Payments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderingConfiguration {

    @Bean
    PlaceOrder placeOrder(Orders orders, Catalog catalog, Payments payments) {
        return new OrderPlacer(orders, catalog, payments);
    }
}
