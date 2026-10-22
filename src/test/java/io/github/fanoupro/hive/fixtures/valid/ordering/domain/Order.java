package io.github.fanoupro.hive.fixtures.valid.ordering.domain;

import io.github.fanoupro.hive.fixtures.valid.money.Money;

import java.util.UUID;

public record Order(UUID id, String sku, Money total) {
}
