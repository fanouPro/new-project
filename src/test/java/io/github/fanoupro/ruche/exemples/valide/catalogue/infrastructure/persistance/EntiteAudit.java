package io.github.fanoupro.ruche.exemples.valide.catalogue.infrastructure.persistance;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit", schema = "catalogue")
public class EntiteAudit {

    @Id
    private Long id;
}
