package io.github.fanoupro.hive.fixtures.alternative_conventions.search.infrastructure.configurations;

import io.github.fanoupro.hive.fixtures.alternative_conventions.search.domain.SpaceTrainsFinder;
import io.github.fanoupro.hive.fixtures.alternative_conventions.search.domain.api.GetSearch;
import io.github.fanoupro.hive.fixtures.alternative_conventions.search.infrastructure.persistence.InMemorySearches;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchConfiguration {

    @Bean
    GetSearch getSearch() {
        return new SpaceTrainsFinder(new InMemorySearches());
    }
}
