package io.github.fanoupro.hive.fixtures.violations.technical_module_depends_on_a_module.hypermedia;

import io.github.fanoupro.hive.fixtures.violations.technical_module_depends_on_a_module.ordering.domain.Order;

public class OrderLinks {
    public String self(Order order) {
        return "/orders/" + order.hashCode();
    }
}
