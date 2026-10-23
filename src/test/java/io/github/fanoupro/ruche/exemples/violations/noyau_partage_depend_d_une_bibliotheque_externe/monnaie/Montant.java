package io.github.fanoupro.ruche.exemples.violations.noyau_partage_depend_d_une_bibliotheque_externe.monnaie;

import jakarta.persistence.Embeddable;

@Embeddable
public record Montant(long centimes) {
}
