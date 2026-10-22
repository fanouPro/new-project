package io.github.fanoupro.hive;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

final class Fixtures {

    static final String FIXTURES = "io.github.fanoupro.hive.fixtures";
    static final String SHARED_ANNOTATIONS = FIXTURES + ".ddd";

    private Fixtures() {
    }

    /**
     * How a fixture is laid out, and therefore declared.
     */
    enum Layout {
        /**
         * Layout of the reference implementation (gitlab.com/beyondxscratch/hive-pattern): modules {@code ordering}
         * and {@code catalog}, shared kernel {@code money}, technical module {@code hypermedia}, composition root
         * {@code app}, default conventions, and every optional rule enabled.
         */
        STANDARD {
            @Override
            Hive hive(String basePackage) {
                return Hive.builder()
                        .module(basePackage + ".ordering")
                        .module(basePackage + ".catalog")
                        .sharedKernel(basePackage + ".money")
                        .annotations(SHARED_ANNOTATIONS, basePackage + ".ddd")
                        .technicalModule(basePackage + ".hypermedia")
                        .compositionRoot(basePackage + ".app")
                        .basePackage(basePackage)
                        .domainServiceAnnotation(SHARED_ANNOTATIONS + ".DomainService")
                        .stubAnnotation(SHARED_ANNOTATIONS + ".Stub")
                        .requireFunctionalApiPorts()
                        .build();
            }

            @Override
            JavaClasses classes(String basePackage) {
                return new ClassFileImporter().importPackages(basePackage, SHARED_ANNOTATIONS);
            }
        },
        /**
         * Layout of the Mandalore Express sample (gitlab.com/beyondxscratch/mandalore-express-hive-pattern):
         * modules {@code booking} and {@code search}, the composition root being their parent package, plural
         * package names, and in-process adapters living in {@code clients}.
         */
        ALTERNATIVE {
            @Override
            Hive hive(String basePackage) {
                return Hive.builder()
                        .module(basePackage + ".booking")
                        .module(basePackage + ".search")
                        .compositionRoot(basePackage)
                        .basePackage(basePackage)
                        .conventions(HiveConventions.defaults()
                                .withConfiguration("infrastructure.configurations")
                                .withDrivingAdapters("infrastructure.controllers")
                                .withInProcessAdapters("infrastructure.clients"))
                        .build();
            }

            @Override
            JavaClasses classes(String basePackage) {
                return new ClassFileImporter().importPackages(basePackage);
            }
        };

        abstract Hive hive(String basePackage);

        abstract JavaClasses classes(String basePackage);
    }

    static String packageOf(String fixture) {
        return FIXTURES + "." + fixture;
    }
}
