package io.github.fanoupro.ruche.exemples.valide.commande.infrastructure.persistance;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit", schema = "commande")
public class EntiteAudit {

    @Id
    private Long id;
}
