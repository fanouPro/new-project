package io.github.fanoupro.hive.fixtures.alternative_conventions.search.domain.spi;

import io.github.fanoupro.hive.fixtures.alternative_conventions.search.domain.Search;

import java.util.Optional;
import java.util.UUID;

public interface Searches {
    Optional<Search> find(UUID searchId);
}
