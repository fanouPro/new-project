package io.github.fanoupro.hive.fixtures.alternative_conventions.search.domain;

import io.github.fanoupro.hive.fixtures.alternative_conventions.search.domain.api.GetSearch;
import io.github.fanoupro.hive.fixtures.alternative_conventions.search.domain.spi.Searches;

import java.util.UUID;

public class SpaceTrainsFinder implements GetSearch {

    private final Searches searches;

    public SpaceTrainsFinder(Searches searches) {
        this.searches = searches;
    }

    @Override
    public Search identifiedBy(UUID searchId) {
        return searches.find(searchId).orElseThrow();
    }
}
