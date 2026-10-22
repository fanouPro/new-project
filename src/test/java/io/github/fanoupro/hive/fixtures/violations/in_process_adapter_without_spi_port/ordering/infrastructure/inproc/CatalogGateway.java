package io.github.fanoupro.hive.fixtures.violations.in_process_adapter_without_spi_port.ordering.infrastructure.inproc;

import io.github.fanoupro.hive.fixtures.violations.in_process_adapter_without_spi_port.catalog.domain.api.FindProduct;

public class CatalogGateway {
    private FindProduct findProduct;

    public void check(String sku) {
        findProduct.find(sku);
    }
}
