package io.github.fanoupro.hive.fixtures.violations.in_process_adapter_uses_another_module_domain_service.catalog.domain;

import io.github.fanoupro.hive.fixtures.ddd.DomainService;
import io.github.fanoupro.hive.fixtures.violations.in_process_adapter_uses_another_module_domain_service.catalog.domain.api.FindProduct;

@DomainService
public class ProductFinder implements FindProduct {
    @Override
    public void find(String sku) {
    }
}
