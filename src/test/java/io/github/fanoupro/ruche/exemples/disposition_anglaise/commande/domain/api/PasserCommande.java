package io.github.fanoupro.ruche.exemples.disposition_anglaise.commande.domain.api;

import io.github.fanoupro.ruche.exemples.disposition_anglaise.commande.domain.Commande;

public interface PasserCommande {
    Commande passer(String reference);
}
