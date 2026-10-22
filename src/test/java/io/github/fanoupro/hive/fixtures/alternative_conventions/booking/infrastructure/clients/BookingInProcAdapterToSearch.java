package io.github.fanoupro.hive.fixtures.alternative_conventions.booking.infrastructure.clients;

import io.github.fanoupro.hive.fixtures.alternative_conventions.booking.domain.spi.GetTrainsToBook;
import io.github.fanoupro.hive.fixtures.alternative_conventions.search.domain.Search;
import io.github.fanoupro.hive.fixtures.alternative_conventions.search.domain.api.GetSearch;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class BookingInProcAdapterToSearch implements GetTrainsToBook {

    private final GetSearch getSearch;

    public BookingInProcAdapterToSearch(GetSearch getSearch) {
        this.getSearch = getSearch;
    }

    @Override
    public List<String> from(UUID searchId) {
        Search search = getSearch.identifiedBy(searchId);
        return List.copyOf(search.selectedTrains());
    }
}
