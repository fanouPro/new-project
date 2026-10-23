package io.github.fanoupro.ruche;

import com.tngtech.archunit.core.domain.JavaClasses;
import io.github.fanoupro.ruche.Exemples.Disposition;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static io.github.fanoupro.ruche.RegleRuche.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class ReglesRucheTest {

    @Test
    void une_ruche_ne_recoit_que_les_regles_que_ses_declarations_rendent_verifiables() {
        Ruche ruche = Ruche.nouvelle().module("com.acme.commande").construire();

        assertThat(ReglesRuche.de(ruche).applicables()).isEqualTo(EnumSet.complementOf(EnumSet.of(
                LES_PORTS_API_SONT_FONCTIONNELS,
                LES_NOYAUX_PARTAGES_SONT_PURS,
                LES_ANNOTATIONS_SONT_PURES,
                LES_MODULES_TECHNIQUES_IGNORENT_LE_METIER,
                LES_DOMAINES_NE_DEPENDENT_PAS_DES_MODULES_TECHNIQUES,
                RIEN_NE_DEPEND_DE_LA_RACINE_DE_COMPOSITION,
                LA_RACINE_DE_COMPOSITION_N_ATTEINT_PAS_LES_DOMAINES,
                CHAQUE_CLASSE_APPARTIENT_A_UNE_ZONE_DECLAREE,
                LES_SERVICES_DU_DOMAINE_SONT_ANNOTES,
                L_ANNOTATION_DE_SERVICE_EST_RESERVEE_AUX_SERVICES_DU_DOMAINE,
                LES_BOUCHONS_IMPLEMENTENT_UN_PORT_SPI_DU_DOMAINE)));
    }

    @Test
    void demander_une_regle_que_la_ruche_ne_peut_pas_verifier_echoue() {
        ReglesRuche regles = ReglesRuche.de(Ruche.nouvelle().module("com.acme.commande").construire());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> regles.regle(LES_BOUCHONS_IMPLEMENTENT_UN_PORT_SPI_DU_DOMAINE))
                .withMessageContaining("LES_BOUCHONS_IMPLEMENTENT_UN_PORT_SPI_DU_DOMAINE ne s'applique pas");
    }

    @Test
    void toutes_les_regles_passent_ensemble_sur_une_ruche_conforme() {
        String standard = Exemples.paquetageDe("valide");

        ReglesRuche.de(Disposition.STANDARD.ruche(standard)).toutes().check(Disposition.STANDARD.classes(standard));
    }

    @Test
    void une_regle_s_explique_par_son_identifiant_et_sa_justification() {
        ReglesRuche regles = ReglesRuche.de(Ruche.nouvelle().module("com.acme.commande").construire());

        assertThat(regles.regle(LES_DOMAINES_SONT_ISOLES).getDescription())
                .isEqualTo("[LES_DOMAINES_SONT_ISOLES] le domaine d'un module ne dépend jamais d'un autre module, "
                        + "car chaque module doit rester extractible en service autonome");
    }

    @Test
    void toutes_les_regles_ensemble_etiquettent_chaque_violation_avec_la_regle_enfreinte() {
        Violation violation = violation("port_api_implemente_par_un_autre_module");
        String preneurDeCommande = "<" + violation.paquetageDeBase() + ".catalogue.domaine.PreneurDeCommande>";
        String passerCommande = "<" + violation.paquetageDeBase() + ".commande.domaine.api.PasserCommande>";

        assertThat(ReglesRuche.de(violation.ruche()).toutes().evaluate(violation.classes()).getFailureReport()
                .toString())
                .contains("Rule 'les classes respectent le pattern Hive (28 règles)' was violated (2 times)")
                .contains("[LES_PORTS_API_SONT_IMPLEMENTES_PAR_LE_DOMAINE] La classe " + preneurDeCommande
                        + " (domaine du module 'catalogue') implémente " + passerCommande
                        + " (API du module 'commande')")
                .contains("[LES_DOMAINES_SONT_ISOLES] domaine du module 'catalogue' -> API du module 'commande' : "
                        + "Class " + preneurDeCommande + " implements interface " + passerCommande);
    }

    @Test
    void une_dependance_interdite_est_signalee_avec_les_zones_qu_elle_traverse() {
        Violation violation = violation("domaine_depend_de_son_infrastructure");

        assertThat(ReglesRuche.de(violation.ruche()).regle(LE_DOMAINE_NE_DEPEND_PAS_DE_SON_INFRASTRUCTURE)
                .evaluate(violation.classes()).getFailureReport().getDetails())
                .singleElement().asString()
                .startsWith("[LE_DOMAINE_NE_DEPEND_PAS_DE_SON_INFRASTRUCTURE] "
                        + "domaine du module 'commande' -> infrastructure du module 'commande' : Field <"
                        + violation.paquetageDeBase() + ".commande.domaine.Commande.entite> has type <"
                        + violation.paquetageDeBase() + ".commande.infrastructure.persistance.EntiteCommande>");
    }

    @Test
    void une_table_partagee_est_signalee_avec_chaque_module_qui_la_mappe() {
        Violation violation = violation("deux_modules_mappent_la_meme_table");

        assertThat(ReglesRuche.de(violation.ruche()).regle(LES_DONNEES_SONT_CLOISONNEES)
                .evaluate(violation.classes()).getFailureReport().getDetails())
                .containsExactly("[LES_DONNEES_SONT_CLOISONNEES] La table 'produit' est mappée par le module "
                        + "'catalogue' [" + violation.paquetageDeBase()
                        + ".catalogue.infrastructure.persistance.EntiteProduit] et le module 'commande' ["
                        + violation.paquetageDeBase() + ".commande.infrastructure.persistance.EntiteLigneProduit]");
    }

    @Test
    void les_domaines_peuvent_dependre_des_bibliotheques_declarees_pures() {
        Violation violation = violation("domaine_depend_d_une_bibliotheque_externe");
        JavaClasses classes = violation.classes();
        Ruche stricte = Ruche.nouvelle().module(violation.paquetageDeBase() + ".commande").construire();
        Ruche tolerante = Ruche.nouvelle().module(violation.paquetageDeBase() + ".commande")
                .bibliothequesPures("jakarta.persistence..")
                .construire();

        assertThat(ReglesRuche.de(stricte).regle(LE_DOMAINE_NE_DEPEND_QUE_DE_BIBLIOTHEQUES_PURES)
                .evaluate(classes).hasViolation()).isTrue();
        assertThat(ReglesRuche.de(tolerante).regle(LE_DOMAINE_NE_DEPEND_QUE_DE_BIBLIOTHEQUES_PURES)
                .evaluate(classes).hasViolation()).isFalse();
    }

    private static Violation violation(String exemple) {
        return Violation.toutes()
                .filter(violation -> violation.toString().equals(exemple))
                .findFirst()
                .orElseThrow();
    }
}
