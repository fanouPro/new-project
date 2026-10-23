package io.github.fanoupro.ruche.exemples.violations.domaine_depend_d_une_bibliotheque_externe.commande.domaine;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Commande {

    @Id
    private Long id;
}
