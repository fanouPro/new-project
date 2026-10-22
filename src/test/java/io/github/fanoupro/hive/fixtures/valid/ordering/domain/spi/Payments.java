package io.github.fanoupro.hive.fixtures.valid.ordering.domain.spi;

import io.github.fanoupro.hive.fixtures.valid.money.Money;

import java.util.UUID;

public interface Payments {
    void charge(UUID orderId, Money amount);
}
