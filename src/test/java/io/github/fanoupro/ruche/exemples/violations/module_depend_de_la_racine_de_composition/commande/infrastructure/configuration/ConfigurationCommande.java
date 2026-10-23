package io.github.fanoupro.ruche.exemples.violations.module_depend_de_la_racine_de_composition.commande.infrastructure.configuration;

import io.github.fanoupro.ruche.exemples.violations.module_depend_de_la_racine_de_composition.appli.ApplicationBoutique;

public class ConfigurationCommande {

    private final String application = ApplicationBoutique.nom();
}
