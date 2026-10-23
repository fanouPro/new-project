package io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.infrastructure.configurations;

import io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.domaine.MoteurDeRecherche;
import io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.domaine.api.ObtenirRecherche;
import io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.infrastructure.persistance.RecherchesEnMemoire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationRecherche {

    @Bean
    ObtenirRecherche obtenirRecherche() {
        return new MoteurDeRecherche(new RecherchesEnMemoire());
    }
}
