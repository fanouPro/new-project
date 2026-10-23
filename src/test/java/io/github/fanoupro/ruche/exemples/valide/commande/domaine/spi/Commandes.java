package io.github.fanoupro.ruche.exemples.valide.commande.domaine.spi;

import io.github.fanoupro.ruche.exemples.valide.commande.domaine.Commande;

public interface Commandes {
    Commande enregistrer(Commande commande);
}
