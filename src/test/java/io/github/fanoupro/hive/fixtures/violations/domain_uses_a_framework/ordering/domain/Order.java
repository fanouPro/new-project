package io.github.fanoupro.hive.fixtures.violations.domain_uses_a_framework.ordering.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Order {
    @Id
    private Long id;
}
