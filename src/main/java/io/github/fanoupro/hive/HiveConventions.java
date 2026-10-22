package io.github.fanoupro.hive;

import java.util.List;

import static io.github.fanoupro.hive.Packages.isWithin;
import static io.github.fanoupro.hive.Packages.requireValidPackage;
import static java.util.Objects.requireNonNull;

/**
 * Package layout of every Hive module, relative to the module root package.
 * <p>
 * The {@linkplain #defaults() defaults} follow the reference implementation of the Hive pattern
 * (<a href="https://gitlab.com/beyondxscratch/hive-pattern">gitlab.com/beyondxscratch/hive-pattern</a>):
 * <pre>
 * ordering
 * ├── domain                  the hexagon: business logic only
 * │   ├── api                 API ports, the features the module exposes
 * │   └── spi                 SPI ports, what the module expects from the outside world
 * └── infrastructure          the adapters
 *     ├── configuration       wiring of the module (e.g. its Spring auto-configuration)
 *     ├── controller          driving adapters
 *     └── inproc              in-process adapters calling the API ports of other modules
 * </pre>
 *
 * @param domain            the hexagon
 * @param api               API ports, inside the domain
 * @param spi               SPI ports, inside the domain
 * @param infrastructure    the adapters
 * @param configuration     wiring of the module, inside the infrastructure
 * @param drivingAdapters   driving (left side) adapters such as REST controllers or message listeners, inside the
 *                          infrastructure
 * @param inProcessAdapters SPI adapters calling the API ports of other modules in-process, inside the infrastructure
 */
public record HiveConventions(
        String domain,
        String api,
        String spi,
        String infrastructure,
        String configuration,
        List<String> drivingAdapters,
        List<String> inProcessAdapters) {

    public HiveConventions {
        requireValidPackage(domain, "domain package");
        requireValidPackage(infrastructure, "infrastructure package");
        if (isWithin(domain, infrastructure) || isWithin(infrastructure, domain)) {
            throw new IllegalArgumentException("The domain and the infrastructure must be separate packages");
        }
        requireWithin(api, domain, "API package");
        requireWithin(spi, domain, "SPI package");
        if (isWithin(api, spi) || isWithin(spi, api)) {
            throw new IllegalArgumentException("API and SPI ports must be separate packages");
        }
        requireWithin(configuration, infrastructure, "configuration package");
        drivingAdapters = List.copyOf(requireNonNull(drivingAdapters, "drivingAdapters"));
        inProcessAdapters = List.copyOf(requireNonNull(inProcessAdapters, "inProcessAdapters"));
        drivingAdapters.forEach(adapters -> requireWithin(adapters, infrastructure, "driving adapters package"));
        inProcessAdapters.forEach(adapters -> requireWithin(adapters, infrastructure, "in-process adapters package"));
    }

    public static HiveConventions defaults() {
        return new HiveConventions(
                "domain",
                "domain.api",
                "domain.spi",
                "infrastructure",
                "infrastructure.configuration",
                List.of("infrastructure.controller"),
                List.of("infrastructure.inproc"));
    }

    public HiveConventions withConfiguration(String configuration) {
        return new HiveConventions(domain, api, spi, infrastructure, configuration, drivingAdapters, inProcessAdapters);
    }

    public HiveConventions withDrivingAdapters(String... drivingAdapters) {
        return new HiveConventions(domain, api, spi, infrastructure, configuration, List.of(drivingAdapters),
                inProcessAdapters);
    }

    public HiveConventions withInProcessAdapters(String... inProcessAdapters) {
        return new HiveConventions(domain, api, spi, infrastructure, configuration, drivingAdapters,
                List.of(inProcessAdapters));
    }

    private static void requireWithin(String packageName, String parent, String what) {
        requireValidPackage(packageName, what);
        if (!Packages.isStrictlyWithin(packageName, parent)) {
            throw new IllegalArgumentException(
                    "The " + what + " '" + packageName + "' must be a sub-package of '" + parent + "'");
        }
    }
}
