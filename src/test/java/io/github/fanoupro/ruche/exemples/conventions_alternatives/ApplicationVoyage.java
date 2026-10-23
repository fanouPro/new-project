package io.github.fanoupro.ruche.exemples.conventions_alternatives;

import io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.infrastructure.configurations.ConfigurationRecherche;
import io.github.fanoupro.ruche.exemples.conventions_alternatives.reservation.infrastructure.configurations.ConfigurationReservation;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ConfigurationReservation.class, ConfigurationRecherche.class})
public class ApplicationVoyage {
}
