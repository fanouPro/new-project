package io.github.fanoupro.ruche.exemples.violations.port_api_expose_plusieurs_fonctionnalites.commande.domaine.api;

public interface GererCommandes {

    void passer(String reference);

    void annuler(String idCommande);
}
