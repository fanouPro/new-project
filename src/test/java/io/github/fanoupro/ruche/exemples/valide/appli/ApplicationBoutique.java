package io.github.fanoupro.ruche.exemples.valide.appli;

import io.github.fanoupro.ruche.exemples.valide.catalogue.infrastructure.configuration.ConfigurationCatalogue;
import io.github.fanoupro.ruche.exemples.valide.commande.infrastructure.configuration.ConfigurationCommande;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ConfigurationCommande.class, ConfigurationCatalogue.class})
public class ApplicationBoutique {
}
