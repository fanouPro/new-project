package io.github.fanoupro.hive.fixtures.alternative_conventions.search.infrastructure.controllers;

import io.github.fanoupro.hive.fixtures.alternative_conventions.search.domain.api.GetSearch;

import java.util.List;
import java.util.UUID;

public class SearchController {

    private final GetSearch getSearch;

    public SearchController(GetSearch getSearch) {
        this.getSearch = getSearch;
    }

    public List<String> selectedTrains(UUID searchId) {
        return getSearch.identifiedBy(searchId).selectedTrains();
    }
}
