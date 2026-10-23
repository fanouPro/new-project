package io.github.fanoupro.ruche.exemples.valide.catalogue.domaine;

import io.github.fanoupro.ruche.exemples.valide.monnaie.Montant;

public record Produit(String reference, Montant prix) {
}
