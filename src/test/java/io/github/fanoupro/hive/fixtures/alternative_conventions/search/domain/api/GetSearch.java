package io.github.fanoupro.hive.fixtures.alternative_conventions.search.domain.api;

import io.github.fanoupro.hive.fixtures.alternative_conventions.search.domain.Search;

import java.util.UUID;

public interface GetSearch {
    Search identifiedBy(UUID searchId);
}
