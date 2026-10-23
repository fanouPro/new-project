package io.github.fanoupro.ruche;

import com.tngtech.archunit.core.domain.JavaClasses;
import io.github.fanoupro.ruche.Exemples.Disposition;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

import static io.github.fanoupro.ruche.RegleRuche.*;

/**
 * Un exemple qui enfreint le pattern Hive, les règles qu'il doit enfreindre, et la classe ou l'élément que les
 * rapports doivent nommer.
 */
record Violation(String exemple, Disposition disposition, Set<RegleRuche> reglesEnfreintes, String fautif) {

    static Stream<Violation> toutes() {
        return Stream.of(
                standard("domaine_depend_de_son_infrastructure", "commande.domaine.Commande",
                        LE_DOMAINE_NE_DEPEND_PAS_DE_SON_INFRASTRUCTURE),
                standard("domaine_depend_d_une_bibliotheque_externe", "commande.domaine.Commande",
                        LE_DOMAINE_NE_DEPEND_QUE_DE_BIBLIOTHEQUES_PURES),
                standard("port_api_est_une_classe", "commande.domaine.api.PasserCommande",
                        LES_PORTS_API_SONT_DES_INTERFACES),
                standard("port_spi_est_une_classe", "commande.domaine.spi.Commandes",
                        LES_PORTS_SPI_SONT_DES_INTERFACES),
                standard("port_api_expose_plusieurs_fonctionnalites", "commande.domaine.api.GererCommandes",
                        LES_PORTS_API_SONT_FONCTIONNELS),
                standard("port_api_implemente_dans_l_infrastructure",
                        "commande.infrastructure.controleur.ControleurCommande",
                        LES_PORTS_API_SONT_IMPLEMENTES_PAR_LE_DOMAINE),
                standard("port_api_implemente_par_un_autre_module", "catalogue.domaine.PreneurDeCommande",
                        LES_PORTS_API_SONT_IMPLEMENTES_PAR_LE_DOMAINE, LES_DOMAINES_SONT_ISOLES),
                standard("port_spi_implemente_dans_le_domaine", "commande.domaine.CommandesEnMemoire",
                        LES_PORTS_SPI_SONT_IMPLEMENTES_PAR_DES_ADAPTATEURS),
                standard("port_spi_implemente_par_un_autre_module",
                        "catalogue.infrastructure.intraprocessus.AdaptateurCommandes",
                        LES_PORTS_SPI_SONT_IMPLEMENTES_PAR_DES_ADAPTATEURS,
                        LES_AUTRES_MODULES_SONT_ATTEINTS_PAR_LEUR_API),
                standard("adaptateur_primaire_utilise_un_port_spi",
                        "commande.infrastructure.controleur.ControleurCommande",
                        LES_ADAPTATEURS_PRIMAIRES_N_UTILISENT_QUE_LES_PORTS_API),
                standard("adaptateur_primaire_utilise_un_adaptateur_secondaire",
                        "commande.infrastructure.controleur.ControleurCommande",
                        LES_ADAPTATEURS_PRIMAIRES_N_UTILISENT_QUE_LES_PORTS_API),
                standard("infrastructure_utilise_un_service_du_domaine",
                        "commande.infrastructure.controleur.ControleurCommande",
                        L_INFRASTRUCTURE_ATTEINT_LES_SERVICES_DU_DOMAINE_PAR_LES_PORTS_API),
                standard("adaptateur_secondaire_appelle_sa_propre_api",
                        "commande.infrastructure.persistance.AdaptateurDepotCommandes",
                        LES_ADAPTATEURS_SECONDAIRES_NE_RAPPELLENT_PAS_LEUR_DOMAINE),

                standard("domaine_depend_du_domaine_d_un_autre_module", "commande.domaine.Commande",
                        LES_DOMAINES_SONT_ISOLES),
                standard("domaine_depend_de_l_infrastructure_d_un_autre_module", "commande.domaine.Commande",
                        LES_DOMAINES_SONT_ISOLES),
                standard("infrastructure_depend_de_l_infrastructure_d_un_autre_module",
                        "commande.infrastructure.persistance.DepotCommandes",
                        LES_INFRASTRUCTURES_SONT_ISOLEES),
                standard("adaptateur_intraprocessus_utilise_la_spi_d_un_autre_module",
                        "commande.infrastructure.intraprocessus.AdaptateurCatalogue",
                        LES_AUTRES_MODULES_SONT_ATTEINTS_PAR_LEUR_API),
                standard("adaptateur_intraprocessus_utilise_un_service_du_domaine_d_un_autre_module",
                        "commande.infrastructure.intraprocessus.AdaptateurCatalogue",
                        LES_AUTRES_MODULES_SONT_ATTEINTS_PAR_LEUR_API),
                standard("adaptateur_intraprocessus_utilise_un_bouchon_d_un_autre_module",
                        "commande.infrastructure.intraprocessus.AdaptateurCatalogue",
                        LES_AUTRES_MODULES_SONT_ATTEINTS_PAR_LEUR_API),
                standard("adaptateur_primaire_appelle_un_autre_module",
                        "commande.infrastructure.controleur.ControleurCommande",
                        SEULS_LES_ADAPTATEURS_INTRAPROCESSUS_ATTEIGNENT_LES_AUTRES_MODULES),
                standard("adaptateur_intraprocessus_sans_port_spi",
                        "commande.infrastructure.intraprocessus.PasserelleCatalogue",
                        LES_ADAPTATEURS_INTRAPROCESSUS_IMPLEMENTENT_UN_PORT_SPI),
                standard("deux_modules_mappent_la_meme_table",
                        "commande.infrastructure.persistance.EntiteLigneProduit",
                        LES_DONNEES_SONT_CLOISONNEES),
                standard("deux_modules_mappent_la_meme_table_implicite",
                        "catalogue.infrastructure.persistance.EntiteHorodatage",
                        LES_DONNEES_SONT_CLOISONNEES),

                standard("noyau_partage_depend_d_une_bibliotheque_externe", "monnaie.Montant",
                        LES_NOYAUX_PARTAGES_SONT_PURS),
                standard("noyau_partage_depend_d_un_module", "monnaie.Montant",
                        LES_NOYAUX_PARTAGES_SONT_PURS),
                standard("annotations_dependent_d_une_bibliotheque_externe", "ddd.ServiceDuDomaine",
                        LES_ANNOTATIONS_SONT_PURES),
                standard("module_technique_depend_d_un_module", "hypermedia.LiensCommande",
                        LES_MODULES_TECHNIQUES_IGNORENT_LE_METIER),
                standard("domaine_depend_d_un_module_technique", "commande.domaine.Commande",
                        LES_DOMAINES_NE_DEPENDENT_PAS_DES_MODULES_TECHNIQUES),
                standard("module_depend_de_la_racine_de_composition",
                        "commande.infrastructure.configuration.ConfigurationCommande",
                        RIEN_NE_DEPEND_DE_LA_RACINE_DE_COMPOSITION),
                standard("racine_de_composition_atteint_un_domaine", "appli.ApplicationBoutique",
                        LA_RACINE_DE_COMPOSITION_N_ATTEINT_PAS_LES_DOMAINES),

                standard("module_sans_configuration", "commande.infrastructure.configuration",
                        CHAQUE_MODULE_A_SA_PROPRE_CONFIGURATION),
                standard("classe_hors_du_domaine_et_de_l_infrastructure", "commande.application.FacadeCommande",
                        LES_MODULES_SE_DIVISENT_EN_DOMAINE_ET_INFRASTRUCTURE),
                standard("module_non_declare", "livraison.domaine.Expedition",
                        CHAQUE_CLASSE_APPARTIENT_A_UNE_ZONE_DECLAREE),
                alternative("module_non_declare_sous_une_racine_de_composition", "paiement.Paiement",
                        CHAQUE_CLASSE_APPARTIENT_A_UNE_ZONE_DECLAREE),

                standard("service_du_domaine_non_annote", "commande.domaine.PriseDeCommande",
                        LES_SERVICES_DU_DOMAINE_SONT_ANNOTES),
                standard("annotation_de_service_dans_l_infrastructure",
                        "commande.infrastructure.persistance.AdaptateurDepotCommandes",
                        L_ANNOTATION_DE_SERVICE_EST_RESERVEE_AUX_SERVICES_DU_DOMAINE),
                standard("annotation_de_service_sans_port_api", "commande.domaine.CalculateurDePrix",
                        L_ANNOTATION_DE_SERVICE_EST_RESERVEE_AUX_SERVICES_DU_DOMAINE),
                standard("bouchon_hors_du_domaine", "commande.infrastructure.persistance.CommandesEnMemoire",
                        LES_BOUCHONS_IMPLEMENTENT_UN_PORT_SPI_DU_DOMAINE),
                standard("bouchon_sans_port_spi", "commande.domaine.bouchons.HorlogeFixe",
                        LES_BOUCHONS_IMPLEMENTENT_UN_PORT_SPI_DU_DOMAINE));
    }

    private static Violation standard(String exemple, String fautif, RegleRuche regle, RegleRuche... autresRegles) {
        return new Violation("violations." + exemple, Disposition.STANDARD, EnumSet.of(regle, autresRegles), fautif);
    }

    private static Violation alternative(String exemple, String fautif, RegleRuche regle,
                                         RegleRuche... autresRegles) {
        return new Violation("violations." + exemple, Disposition.ALTERNATIVE, EnumSet.of(regle, autresRegles),
                fautif);
    }

    String paquetageDeBase() {
        return Exemples.paquetageDe(exemple);
    }

    Ruche ruche() {
        return disposition.ruche(paquetageDeBase());
    }

    Ruche ruche(Langue langue) {
        return disposition.ruche(paquetageDeBase(), langue);
    }

    JavaClasses classes() {
        return disposition.classes(paquetageDeBase());
    }

    String fautifQualifie() {
        return paquetageDeBase() + "." + fautif;
    }

    @Override
    public String toString() {
        return exemple.substring("violations.".length());
    }
}
