package io.github.fanoupro.ruche;

import io.github.fanoupro.ruche.TopologieRuche.Couche;
import io.github.fanoupro.ruche.TopologieRuche.TypeDeZone;

import java.util.List;
import java.util.Map;

/**
 * Les textes de la vérification dans une {@link Langue} : description des règles, zones et messages de violation.
 */
interface Textes {

    String regle(String identifiant, String description, String justification);

    String toutesLesRegles(int nombre);

    String neSAppliquePas(String identifiant);

    String bibliothequePure();

    String paquetageExterne(String paquetage);

    String zone(TypeDeZone type, String paquetage);

    String couche(Couche couche, String module);

    String dependanceInterdite(String origine, String cible, String description);

    String classe(String classe, String violation, String emplacement);

    String portHorsDeSaCouche(String classe, String zoneDeLaClasse, String port, String zoneDuPort,
                              String emplacement);

    String nEstPasUneInterface();

    String methodesAbstraites(long nombre);

    String appelleUneApiSansPortSpi();

    String horsDuDomaineEtDeLInfrastructure(String zone, String module);

    String horsDesZonesDeclarees();

    String nonAnnotee(String annotation);

    String aucunPortApi(String zone, String module);

    String aucunPortSpi(String zone, String module);

    String horsDesDomaines(String zone);

    String sansConfiguration(String module, String paquetage);

    String tablePartagee(String table, Map<String, List<String>> classesParModule);
}
