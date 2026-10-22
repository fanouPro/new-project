package io.github.fanoupro.hive.fixtures.violations.module_depends_on_the_composition_root.ordering.infrastructure.configuration;

import io.github.fanoupro.hive.fixtures.violations.module_depends_on_the_composition_root.app.ShopApplication;

public class OrderingConfiguration {
    private final String application = ShopApplication.name();
}
