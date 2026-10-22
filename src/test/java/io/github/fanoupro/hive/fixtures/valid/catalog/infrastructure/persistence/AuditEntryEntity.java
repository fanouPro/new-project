package io.github.fanoupro.hive.fixtures.valid.catalog.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit", schema = "catalog")
public class AuditEntryEntity {

    @Id
    private Long id;
}
