package io.github.fanoupro.ruche;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Comment un projet déclare sa ruche et la vérifie avec le moteur JUnit 5 d'ArchUnit, ici sur l'exemple conforme.
 */
@AnalyzeClasses(
        packages = {"io.github.fanoupro.ruche.exemples.valide", "io.github.fanoupro.ruche.exemples.ddd"},
        importOptions = ImportOption.DoNotIncludeJars.class)
class ExempleArchitectureRucheTest {

    private static final String BOUTIQUE = "io.github.fanoupro.ruche.exemples.valide";

    private static final Ruche RUCHE = Ruche.nouvelle()
            .module(BOUTIQUE + ".commande")
            .module(BOUTIQUE + ".catalogue")
            .noyauPartage(BOUTIQUE + ".monnaie")
            .annotations("io.github.fanoupro.ruche.exemples.ddd")
            .moduleTechnique(BOUTIQUE + ".hypermedia")
            .racineDeComposition(BOUTIQUE + ".appli")
            .paquetageDeBase(BOUTIQUE)
            .annotationServiceDuDomaine("io.github.fanoupro.ruche.exemples.ddd.ServiceDuDomaine")
            .annotationBouchon("io.github.fanoupro.ruche.exemples.ddd.Bouchon")
            .exigerPortsApiFonctionnels()
            .construire();

    @ArchTest
    static final ArchRule la_boutique_est_une_ruche = ReglesRuche.de(RUCHE).toutes();
}
