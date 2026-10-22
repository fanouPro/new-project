package io.github.fanoupro.hive.fixtures.alternative_conventions.booking.infrastructure.configurations;

import io.github.fanoupro.hive.fixtures.alternative_conventions.booking.domain.SpaceTrainsBooker;
import io.github.fanoupro.hive.fixtures.alternative_conventions.booking.domain.api.PrepareBooking;
import io.github.fanoupro.hive.fixtures.alternative_conventions.booking.domain.spi.GetTrainsToBook;
import io.github.fanoupro.hive.fixtures.alternative_conventions.booking.infrastructure.clients.BookingInProcAdapterToSearch;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = BookingInProcAdapterToSearch.class)
public class BookingConfiguration {

    @Bean
    PrepareBooking prepareBooking(GetTrainsToBook trainsToBook) {
        return new SpaceTrainsBooker(trainsToBook);
    }
}
