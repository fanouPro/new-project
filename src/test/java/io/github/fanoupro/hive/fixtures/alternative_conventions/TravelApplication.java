package io.github.fanoupro.hive.fixtures.alternative_conventions;

import io.github.fanoupro.hive.fixtures.alternative_conventions.booking.infrastructure.configurations.BookingConfiguration;
import io.github.fanoupro.hive.fixtures.alternative_conventions.search.infrastructure.configurations.SearchConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({BookingConfiguration.class, SearchConfiguration.class})
public class TravelApplication {
}
