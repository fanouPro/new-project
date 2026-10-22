package io.github.fanoupro.hive.fixtures.valid.ordering.infrastructure.controller;

import io.github.fanoupro.hive.fixtures.valid.ordering.domain.api.PlaceOrder;
import io.github.fanoupro.hive.fixtures.valid.ordering.infrastructure.controller.resources.OrderResource;
import org.springframework.stereotype.Controller;

@Controller
public class OrderController {

    private final PlaceOrder placeOrder;

    public OrderController(PlaceOrder placeOrder) {
        this.placeOrder = placeOrder;
    }

    public OrderResource order(String sku) {
        return OrderResource.of(placeOrder.place(sku));
    }
}
