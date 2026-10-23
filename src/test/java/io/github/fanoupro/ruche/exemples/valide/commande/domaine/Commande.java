package io.github.fanoupro.ruche.exemples.valide.commande.domaine;

import io.github.fanoupro.ruche.exemples.valide.monnaie.Montant;

import java.util.UUID;

public record Commande(UUID id, String reference, Montant total) {
}
