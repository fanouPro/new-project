package io.github.fanoupro.hive.fixtures.alternative_conventions.search.infrastructure.persistence;

import io.github.fanoupro.hive.fixtures.alternative_conventions.search.domain.Search;
import io.github.fanoupro.hive.fixtures.alternative_conventions.search.domain.spi.Searches;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemorySearches implements Searches {

    private final Map<UUID, Search> searches = new HashMap<>();

    @Override
    public Optional<Search> find(UUID searchId) {
        return Optional.ofNullable(searches.get(searchId));
    }
}
