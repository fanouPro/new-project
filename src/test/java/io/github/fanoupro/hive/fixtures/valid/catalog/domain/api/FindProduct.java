package io.github.fanoupro.hive.fixtures.valid.catalog.domain.api;

import io.github.fanoupro.hive.fixtures.valid.catalog.domain.Product;

import java.util.Optional;

@FunctionalInterface
public interface FindProduct {
    Optional<Product> bySku(String sku);
}
