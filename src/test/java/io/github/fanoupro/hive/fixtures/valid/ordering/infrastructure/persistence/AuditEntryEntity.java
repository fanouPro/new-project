package io.github.fanoupro.hive.fixtures.valid.ordering.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit", schema = "ordering")
public class AuditEntryEntity {

    @Id
    private Long id;
}
