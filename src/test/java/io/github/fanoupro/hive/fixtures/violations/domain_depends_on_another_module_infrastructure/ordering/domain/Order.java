package io.github.fanoupro.hive.fixtures.violations.domain_depends_on_another_module_infrastructure.ordering.domain;

import io.github.fanoupro.hive.fixtures.violations.domain_depends_on_another_module_infrastructure.catalog.infrastructure.persistence.ProductEntity;

public class Order {
    private ProductEntity product;
}
