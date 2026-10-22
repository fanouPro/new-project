package io.github.fanoupro.hive.fixtures.violations.in_process_adapter_uses_another_module_domain_service.ordering.infrastructure.inproc;

import io.github.fanoupro.hive.fixtures.violations.in_process_adapter_uses_another_module_domain_service.catalog.domain.ProductFinder;
import io.github.fanoupro.hive.fixtures.violations.in_process_adapter_uses_another_module_domain_service.ordering.domain.spi.Catalog;

public class CatalogAdapter implements Catalog {
    private ProductFinder productFinder;

    @Override
    public void check(String sku) {
        productFinder.find(sku);
    }
}
