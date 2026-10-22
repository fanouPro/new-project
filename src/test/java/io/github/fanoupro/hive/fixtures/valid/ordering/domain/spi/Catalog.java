package io.github.fanoupro.hive.fixtures.valid.ordering.domain.spi;

import io.github.fanoupro.hive.fixtures.valid.money.Money;

import java.util.Optional;

public interface Catalog {
    Optional<Money> priceOf(String sku);
}
