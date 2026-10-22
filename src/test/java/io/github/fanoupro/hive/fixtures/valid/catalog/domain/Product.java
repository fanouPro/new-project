package io.github.fanoupro.hive.fixtures.valid.catalog.domain;

import io.github.fanoupro.hive.fixtures.valid.money.Money;

public record Product(String sku, Money price) {
}
