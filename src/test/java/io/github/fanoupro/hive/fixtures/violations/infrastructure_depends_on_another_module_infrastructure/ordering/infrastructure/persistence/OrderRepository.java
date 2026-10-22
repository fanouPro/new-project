package io.github.fanoupro.hive.fixtures.violations.infrastructure_depends_on_another_module_infrastructure.ordering.infrastructure.persistence;

import io.github.fanoupro.hive.fixtures.violations.infrastructure_depends_on_another_module_infrastructure.catalog.infrastructure.persistence.ProductTable;

public class OrderRepository {
    private ProductTable products;
}
