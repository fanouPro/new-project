package io.github.fanoupro.hive.fixtures.violations.in_process_adapter_uses_another_module_spi.ordering.infrastructure.inproc;

import io.github.fanoupro.hive.fixtures.violations.in_process_adapter_uses_another_module_spi.catalog.domain.spi.Products;
import io.github.fanoupro.hive.fixtures.violations.in_process_adapter_uses_another_module_spi.ordering.domain.spi.Catalog;

public class CatalogAdapter implements Catalog {
    private Products products;

    @Override
    public void check(String sku) {
        products.find(sku);
    }
}
