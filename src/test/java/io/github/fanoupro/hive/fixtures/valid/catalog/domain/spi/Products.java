package io.github.fanoupro.hive.fixtures.valid.catalog.domain.spi;

import io.github.fanoupro.hive.fixtures.valid.catalog.domain.Product;

import java.util.Optional;

public interface Products {
    Optional<Product> withSku(String sku);
}
