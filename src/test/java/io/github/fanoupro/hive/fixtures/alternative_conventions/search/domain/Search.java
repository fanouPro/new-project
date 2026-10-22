package io.github.fanoupro.hive.fixtures.alternative_conventions.search.domain;

import java.util.List;
import java.util.UUID;

public record Search(UUID id, List<String> selectedTrains) {
}
