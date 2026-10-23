package io.github.fanoupro.ruche.exemples.valide.commande.domaine.spi;

import io.github.fanoupro.ruche.exemples.valide.monnaie.Montant;

import java.util.UUID;

public interface Paiements {
    void debiter(UUID idCommande, Montant montant);
}
