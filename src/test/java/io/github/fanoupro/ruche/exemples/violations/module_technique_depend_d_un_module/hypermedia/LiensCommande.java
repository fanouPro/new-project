package io.github.fanoupro.ruche.exemples.violations.module_technique_depend_d_un_module.hypermedia;

import io.github.fanoupro.ruche.exemples.violations.module_technique_depend_d_un_module.commande.domaine.Commande;

public class LiensCommande {

    public String lienVers(Commande commande) {
        return "/commandes/" + commande.hashCode();
    }
}
