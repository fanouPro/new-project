package io.github.fanoupro.hive.fixtures.violations.in_process_adapter_uses_another_module_stub.ordering.infrastructure.inproc;

import io.github.fanoupro.hive.fixtures.violations.in_process_adapter_uses_another_module_stub.catalog.domain.stubs.InMemoryProducts;
import io.github.fanoupro.hive.fixtures.violations.in_process_adapter_uses_another_module_stub.ordering.domain.spi.Catalog;

public class CatalogAdapter implements Catalog {
    private final InMemoryProducts products = new InMemoryProducts();

    @Override
    public void check(String sku) {
        products.find(sku);
    }
}
