package io.github.fanoupro.hive.fixtures.violations.in_process_adapter_uses_another_module_stub.catalog.domain.stubs;

import io.github.fanoupro.hive.fixtures.ddd.Stub;
import io.github.fanoupro.hive.fixtures.violations.in_process_adapter_uses_another_module_stub.catalog.domain.spi.Products;

@Stub
public class InMemoryProducts implements Products {
    @Override
    public void find(String sku) {
    }
}
