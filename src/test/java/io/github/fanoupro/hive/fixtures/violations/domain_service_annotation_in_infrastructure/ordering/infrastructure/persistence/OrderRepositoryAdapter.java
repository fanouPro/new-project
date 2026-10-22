package io.github.fanoupro.hive.fixtures.violations.domain_service_annotation_in_infrastructure.ordering.infrastructure.persistence;

import io.github.fanoupro.hive.fixtures.ddd.DomainService;
import io.github.fanoupro.hive.fixtures.violations.domain_service_annotation_in_infrastructure.ordering.domain.spi.Orders;

@DomainService
public class OrderRepositoryAdapter implements Orders {
    @Override
    public void save(String id) {
    }
}
