package io.github.fanoupro.hive.fixtures.valid.catalog.domain;

import io.github.fanoupro.hive.fixtures.ddd.DomainService;
import io.github.fanoupro.hive.fixtures.valid.catalog.domain.api.FindProduct;
import io.github.fanoupro.hive.fixtures.valid.catalog.domain.spi.Products;

import java.util.Optional;

@DomainService
public class ProductFinder implements FindProduct {

    private final Products products;

    public ProductFinder(Products products) {
        this.products = products;
    }

    @Override
    public Optional<Product> bySku(String sku) {
        return products.withSku(sku);
    }
}
