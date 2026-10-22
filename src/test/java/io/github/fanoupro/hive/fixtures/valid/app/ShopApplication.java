package io.github.fanoupro.hive.fixtures.valid.app;

import io.github.fanoupro.hive.fixtures.valid.catalog.infrastructure.configuration.CatalogConfiguration;
import io.github.fanoupro.hive.fixtures.valid.ordering.infrastructure.configuration.OrderingConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({OrderingConfiguration.class, CatalogConfiguration.class})
public class ShopApplication {
}
