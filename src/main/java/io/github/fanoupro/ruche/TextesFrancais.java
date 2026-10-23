package io.github.fanoupro.ruche;

import io.github.fanoupro.ruche.TopologieRuche.Couche;
import io.github.fanoupro.ruche.TopologieRuche.TypeDeZone;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

final class TextesFrancais implements Textes {

    @Override
    public String regle(String identifiant, String description, String justification) {
        return "[" + identifiant + "] " + description + ", car " + justification;
    }

    @Override
    public String toutesLesRegles(int nombre) {
        return "les classes respectent le pattern Hive (" + nombre + " règles)";
    }

    @Override
    public String neSAppliquePas(String identifiant) {
        return identifiant + " ne s'applique pas : déclarez d'abord dans la ruche ce qu'elle vérifie";
    }

    @Override
    public String bibliothequePure() {
        return "bibliothèque pure";
    }

    @Override
    public String paquetageExterne(String paquetage) {
        return "paquetage externe '" + paquetage + "'";
    }

    @Override
    public String zone(TypeDeZone type, String paquetage) {
        String libelle = switch (type) {
            case MODULE -> "module";
            case NOYAU_PARTAGE -> "noyau partagé";
            case ANNOTATIONS -> "annotations";
            case MODULE_TECHNIQUE -> "module technique";
            case RACINE_DE_COMPOSITION -> "racine de composition";
        };
        return libelle + " '" + paquetage + "'";
    }

    @Override
    public String couche(Couche couche, String module) {
        String libelle = switch (couche) {
            case API -> "API";
            case SPI -> "SPI";
            case DOMAINE -> "domaine";
            case INFRASTRUCTURE -> "infrastructure";
            case HORS_DU_DOMAINE_ET_DE_L_INFRASTRUCTURE -> "hors du domaine et de l'infrastructure";
        };
        return libelle + " du module '" + module + "'";
    }

    @Override
    public String dependanceInterdite(String origine, String cible, String description) {
        return origine + " -> " + cible + " : " + description;
    }

    @Override
    public String classe(String classe, String violation, String emplacement) {
        return "La classe <" + classe + "> " + violation + " dans " + emplacement;
    }

    @Override
    public String portHorsDeSaCouche(String classe, String zoneDeLaClasse, String port, String zoneDuPort,
                                     String emplacement) {
        return "La classe <" + classe + "> (" + zoneDeLaClasse + ") implémente <" + port + "> (" + zoneDuPort
                + ") dans " + emplacement;
    }

    @Override
    public String nEstPasUneInterface() {
        return "n'est pas une interface";
    }

    @Override
    public String methodesAbstraites(long nombre) {
        return nombre == 0 ? "ne déclare aucune méthode abstraite" : "déclare " + nombre + " méthodes abstraites";
    }

    @Override
    public String appelleUneApiSansPortSpi() {
        return "appelle un port API d'un autre module sans implémenter de port SPI de son propre module";
    }

    @Override
    public String horsDuDomaineEtDeLInfrastructure(String zone, String module) {
        return "n'appartient ni au domaine ni à l'infrastructure du module '" + module + "'";
    }

    @Override
    public String horsDesZonesDeclarees() {
        return "n'appartient à aucune zone déclarée";
    }

    @Override
    public String nonAnnotee(String annotation) {
        return "n'est pas annotée avec <" + annotation + ">";
    }

    @Override
    public String aucunPortApi(String zone, String module) {
        return "n'implémente aucun port API du module '" + module + "'";
    }

    @Override
    public String aucunPortSpi(String zone, String module) {
        return "n'implémente aucun port SPI du module '" + module + "'";
    }

    @Override
    public String horsDesDomaines(String zone) {
        return "n'appartient à aucun domaine (" + zone + ")";
    }

    @Override
    public String sansConfiguration(String module, String paquetage) {
        return "Le module '" + module + "' n'a aucune classe dans son paquetage de configuration <" + paquetage + ">";
    }

    @Override
    public String tablePartagee(String table, Map<String, List<String>> classesParModule) {
        return "La table '" + table + "' est mappée par " + classesParModule.entrySet().stream()
                .map(entree -> "le module '" + entree.getKey() + "' " + entree.getValue())
                .collect(joining(" et "));
    }
}
