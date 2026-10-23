package io.github.fanoupro.ruche.exemples.valide.commande.domaine.api;

import io.github.fanoupro.ruche.exemples.valide.commande.domaine.Commande;

@FunctionalInterface
public interface PasserCommande {
    Commande passer(String reference);
}
