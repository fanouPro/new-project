package io.github.fanoupro.hive.fixtures.valid.ordering.infrastructure.inproc;

import io.github.fanoupro.hive.fixtures.valid.catalog.domain.Product;
import io.github.fanoupro.hive.fixtures.valid.catalog.domain.api.FindProduct;
import io.github.fanoupro.hive.fixtures.valid.money.Money;
import io.github.fanoupro.hive.fixtures.valid.ordering.domain.spi.Catalog;

import java.util.Optional;

public class CatalogAdapter implements Catalog {

    private final FindProduct findProduct;

    public CatalogAdapter(FindProduct findProduct) {
        this.findProduct = findProduct;
    }

    @Override
    public Optional<Money> priceOf(String sku) {
        return findProduct.bySku(sku).map(Product::price);
    }
}
