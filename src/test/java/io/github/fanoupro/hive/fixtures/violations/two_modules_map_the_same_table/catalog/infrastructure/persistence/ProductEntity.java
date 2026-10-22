package io.github.fanoupro.hive.fixtures.violations.two_modules_map_the_same_table.catalog.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class ProductEntity {
}
