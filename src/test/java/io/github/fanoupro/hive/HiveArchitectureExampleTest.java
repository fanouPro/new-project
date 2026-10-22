package io.github.fanoupro.hive;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * How a project declares its Hive and checks it with the ArchUnit JUnit 5 engine, here on the compliant fixture.
 */
@AnalyzeClasses(
        packages = {"io.github.fanoupro.hive.fixtures.valid", "io.github.fanoupro.hive.fixtures.ddd"},
        importOptions = ImportOption.DoNotIncludeJars.class)
class HiveArchitectureExampleTest {

    private static final String SHOP = "io.github.fanoupro.hive.fixtures.valid";

    private static final Hive HIVE = Hive.builder()
            .module(SHOP + ".ordering")
            .module(SHOP + ".catalog")
            .sharedKernel(SHOP + ".money")
            .annotations("io.github.fanoupro.hive.fixtures.ddd")
            .technicalModule(SHOP + ".hypermedia")
            .compositionRoot(SHOP + ".app")
            .basePackage(SHOP)
            .domainServiceAnnotation("io.github.fanoupro.hive.fixtures.ddd.DomainService")
            .stubAnnotation("io.github.fanoupro.hive.fixtures.ddd.Stub")
            .requireFunctionalApiPorts()
            .build();

    @ArchTest
    static final ArchRule the_shop_is_a_hive = HiveRules.of(HIVE).all();
}
