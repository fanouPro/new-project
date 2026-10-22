package io.github.fanoupro.hive.fixtures.valid.catalog.infrastructure.persistence;

import io.github.fanoupro.hive.fixtures.valid.catalog.domain.Product;
import io.github.fanoupro.hive.fixtures.valid.catalog.domain.spi.Products;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProductRepositoryAdapter implements Products {

    private final Map<String, ProductEntity> table = new HashMap<>();

    @Override
    public Optional<Product> withSku(String sku) {
        return Optional.ofNullable(table.get(sku)).map(ProductEntity::toDomain);
    }
}
