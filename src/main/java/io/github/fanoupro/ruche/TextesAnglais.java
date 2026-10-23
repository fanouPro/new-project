package io.github.fanoupro.ruche;

import io.github.fanoupro.ruche.TopologieRuche.Couche;
import io.github.fanoupro.ruche.TopologieRuche.TypeDeZone;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

final class TextesAnglais implements Textes {

    @Override
    public String regle(String identifiant, String description, String justification) {
        return "[" + identifiant + "] " + description + ", because " + justification;
    }

    @Override
    public String toutesLesRegles(int nombre) {
        return "classes follow the Hive pattern (" + nombre + " rules)";
    }

    @Override
    public String neSAppliquePas(String identifiant) {
        return identifiant + " does not apply: declare what it checks in the Hive first";
    }

    @Override
    public String bibliothequePure() {
        return "pure library";
    }

    @Override
    public String paquetageExterne(String paquetage) {
        return "external package '" + paquetage + "'";
    }

    @Override
    public String zone(TypeDeZone type, String paquetage) {
        String libelle = switch (type) {
            case MODULE -> "module";
            case NOYAU_PARTAGE -> "shared kernel";
            case ANNOTATIONS -> "annotations";
            case MODULE_TECHNIQUE -> "technical module";
            case RACINE_DE_COMPOSITION -> "composition root";
        };
        return libelle + " '" + paquetage + "'";
    }

    @Override
    public String couche(Couche couche, String module) {
        String libelle = switch (couche) {
            case API -> "API";
            case SPI -> "SPI";
            case DOMAINE -> "domain";
            case INFRASTRUCTURE -> "infrastructure";
            case HORS_DU_DOMAINE_ET_DE_L_INFRASTRUCTURE -> "outside of domain and infrastructure";
        };
        return "module '" + module + "' " + libelle;
    }

    @Override
    public String dependanceInterdite(String origine, String cible, String description) {
        return origine + " -> " + cible + ": " + description;
    }

    @Override
    public String classe(String classe, String violation, String emplacement) {
        return "Class <" + classe + "> " + violation + " in " + emplacement;
    }

    @Override
    public String portHorsDeSaCouche(String classe, String zoneDeLaClasse, String port, String zoneDuPort,
                                     String emplacement) {
        return "Class <" + classe + "> in " + zoneDeLaClasse + " implements <" + port + "> of " + zoneDuPort + " in "
                + emplacement;
    }

    @Override
    public String nEstPasUneInterface() {
        return "is not an interface";
    }

    @Override
    public String methodesAbstraites(long nombre) {
        return nombre == 0 ? "declares no abstract method" : "declares " + nombre + " abstract methods";
    }

    @Override
    public String appelleUneApiSansPortSpi() {
        return "calls an API port of another module but implements no SPI port of its own";
    }

    @Override
    public String horsDuDomaineEtDeLInfrastructure(String zone, String module) {
        return "is in " + zone;
    }

    @Override
    public String horsDesZonesDeclarees() {
        return "belongs to no declared zone";
    }

    @Override
    public String nonAnnotee(String annotation) {
        return "is not annotated with <" + annotation + ">";
    }

    @Override
    public String aucunPortApi(String zone, String module) {
        return "implements no API port of " + zone;
    }

    @Override
    public String aucunPortSpi(String zone, String module) {
        return "implements no SPI port of " + zone;
    }

    @Override
    public String horsDesDomaines(String zone) {
        return "is in " + zone + " instead of a domain";
    }

    @Override
    public String sansConfiguration(String module, String paquetage) {
        return "Module '" + module + "' has no class in its configuration package <" + paquetage + ">";
    }

    @Override
    public String tablePartagee(String table, Map<String, List<String>> classesParModule) {
        return "Table '" + table + "' is mapped by " + classesParModule.entrySet().stream()
                .map(entree -> "module '" + entree.getKey() + "' " + entree.getValue())
                .collect(joining(" and "));
    }
}
