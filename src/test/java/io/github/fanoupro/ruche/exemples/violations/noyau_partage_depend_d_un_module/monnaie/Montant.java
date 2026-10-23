package io.github.fanoupro.ruche.exemples.violations.noyau_partage_depend_d_un_module.monnaie;

import io.github.fanoupro.ruche.exemples.violations.noyau_partage_depend_d_un_module.commande.domaine.Commande;

public record Montant(long centimes) {

    public static Montant totalDe(Commande commande) {
        return new Montant(0);
    }
}
