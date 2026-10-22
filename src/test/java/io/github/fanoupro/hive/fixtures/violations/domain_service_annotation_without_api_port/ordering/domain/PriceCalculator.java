package io.github.fanoupro.hive.fixtures.violations.domain_service_annotation_without_api_port.ordering.domain;

import io.github.fanoupro.hive.fixtures.ddd.DomainService;

@DomainService
public class PriceCalculator {
    public long total(long unitPrice, int quantity) {
        return unitPrice * quantity;
    }
}
