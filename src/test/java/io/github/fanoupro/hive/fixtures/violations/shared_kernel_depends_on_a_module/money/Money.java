package io.github.fanoupro.hive.fixtures.violations.shared_kernel_depends_on_a_module.money;

import io.github.fanoupro.hive.fixtures.violations.shared_kernel_depends_on_a_module.ordering.domain.Order;

public record Money(long cents) {
    public static Money totalOf(Order order) {
        return new Money(0);
    }
}
