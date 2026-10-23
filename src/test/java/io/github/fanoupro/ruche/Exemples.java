package io.github.fanoupro.ruche;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

final class Exemples {

    static final String EXEMPLES = "io.github.fanoupro.ruche.exemples";
    static final String ANNOTATIONS_PARTAGEES = EXEMPLES + ".ddd";

    private Exemples() {
    }

    /**
     * Comment un exemple est organisé, et donc déclaré. Ses paquetages sont nommés en français : la langue de la
     * vérification ne change que ses textes.
     */
    enum Disposition {
        /**
         * Disposition de l'implémentation de référence (gitlab.com/beyondxscratch/hive-pattern), avec les conventions
         * françaises : modules {@code commande} et {@code catalogue}, noyau partagé {@code monnaie}, module technique
         * {@code hypermedia}, racine de composition {@code appli}, et toutes les règles facultatives activées.
         */
        STANDARD {
            @Override
            Ruche ruche(String paquetageDeBase, Langue langue) {
                return Ruche.nouvelle()
                        .langue(langue)
                        .conventions(ConventionsRuche.francaises())
                        .module(paquetageDeBase + ".commande")
                        .module(paquetageDeBase + ".catalogue")
                        .noyauPartage(paquetageDeBase + ".monnaie")
                        .annotations(ANNOTATIONS_PARTAGEES, paquetageDeBase + ".ddd")
                        .moduleTechnique(paquetageDeBase + ".hypermedia")
                        .racineDeComposition(paquetageDeBase + ".appli")
                        .paquetageDeBase(paquetageDeBase)
                        .annotationServiceDuDomaine(ANNOTATIONS_PARTAGEES + ".ServiceDuDomaine")
                        .annotationBouchon(ANNOTATIONS_PARTAGEES + ".Bouchon")
                        .exigerPortsApiFonctionnels()
                        .construire();
            }

            @Override
            JavaClasses classes(String paquetageDeBase) {
                return new ClassFileImporter().importPackages(paquetageDeBase, ANNOTATIONS_PARTAGEES);
            }
        },
        /**
         * Disposition de l'exemple Mandalore Express (gitlab.com/beyondxscratch/mandalore-express-hive-pattern) :
         * modules {@code reservation} et {@code recherche}, dont le paquetage parent est la racine de composition,
         * paquetages au pluriel, et adaptateurs intraprocessus rangés dans {@code clients}.
         */
        ALTERNATIVE {
            @Override
            Ruche ruche(String paquetageDeBase, Langue langue) {
                return Ruche.nouvelle()
                        .langue(langue)
                        .module(paquetageDeBase + ".reservation")
                        .module(paquetageDeBase + ".recherche")
                        .racineDeComposition(paquetageDeBase)
                        .paquetageDeBase(paquetageDeBase)
                        .conventions(ConventionsRuche.francaises()
                                .avecConfiguration("infrastructure.configurations")
                                .avecAdaptateursPrimaires("infrastructure.controleurs")
                                .avecAdaptateursIntraprocessus("infrastructure.clients"))
                        .construire();
            }

            @Override
            JavaClasses classes(String paquetageDeBase) {
                return new ClassFileImporter().importPackages(paquetageDeBase);
            }
        };

        abstract Ruche ruche(String paquetageDeBase, Langue langue);

        Ruche ruche(String paquetageDeBase) {
            return ruche(paquetageDeBase, Langue.FRANCAIS);
        }

        abstract JavaClasses classes(String paquetageDeBase);
    }

    static String paquetageDe(String exemple) {
        return EXEMPLES + "." + exemple;
    }
}
