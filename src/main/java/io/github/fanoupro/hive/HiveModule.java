package io.github.fanoupro.hive;

import static io.github.fanoupro.hive.Packages.requireValidPackage;
import static java.util.Objects.requireNonNull;

/**
 * A Hive module: a bounded context implemented as a hexagon (a domain and its infrastructure), designed to be
 * extractable as a standalone service.
 *
 * @param name        the name used in violation messages
 * @param packageName the root package of the module, e.g. {@code com.acme.shop.ordering}
 */
public record HiveModule(String name, String packageName) {

    public HiveModule {
        requireNonNull(name, "name");
        if (name.isBlank()) {
            throw new IllegalArgumentException("A Hive module needs a name");
        }
        requireValidPackage(packageName, "module package");
    }
}
