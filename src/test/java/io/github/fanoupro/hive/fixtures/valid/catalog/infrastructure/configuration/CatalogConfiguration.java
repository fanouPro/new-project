package io.github.fanoupro.hive.fixtures.valid.catalog.infrastructure.configuration;

import io.github.fanoupro.hive.fixtures.valid.catalog.domain.ProductFinder;
import io.github.fanoupro.hive.fixtures.valid.catalog.domain.api.FindProduct;
import io.github.fanoupro.hive.fixtures.valid.catalog.domain.spi.Products;
import io.github.fanoupro.hive.fixtures.valid.catalog.infrastructure.persistence.ProductRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatalogConfiguration {

    @Bean
    Products products() {
        return new ProductRepositoryAdapter();
    }

    @Bean
    FindProduct findProduct(Products products) {
        return new ProductFinder(products);
    }
}
