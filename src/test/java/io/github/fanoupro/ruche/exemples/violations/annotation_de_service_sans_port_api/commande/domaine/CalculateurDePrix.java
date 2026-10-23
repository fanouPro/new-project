package io.github.fanoupro.ruche.exemples.violations.annotation_de_service_sans_port_api.commande.domaine;

import io.github.fanoupro.ruche.exemples.ddd.ServiceDuDomaine;

@ServiceDuDomaine
public class CalculateurDePrix {

    public long total(long prixUnitaire, int quantite) {
        return prixUnitaire * quantite;
    }
}
