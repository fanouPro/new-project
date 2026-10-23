package io.github.fanoupro.ruche;

import com.tngtech.archunit.ArchConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

class RucheTest {

    @AfterEach
    void oublier_la_configuration() {
        ArchConfiguration.get().reset();
    }

    @Test
    void un_module_porte_par_defaut_le_nom_de_son_paquetage() {
        Ruche ruche = Ruche.nouvelle().module("com.acme.boutique.commande").construire();

        assertThat(ruche.modules()).containsExactly(new ModuleRuche("commande", "com.acme.boutique.commande"));
    }

    @Test
    void le_code_pur_peut_toujours_dependre_du_jdk() {
        Ruche ruche = Ruche.nouvelle().module("com.acme.boutique.commande").bibliothequesPures("org.jspecify..")
                .construire();

        assertThat(ruche.bibliothequesPures()).containsExactly("java..", "org.jspecify..");
    }

    @Test
    void une_ruche_a_besoin_d_un_module() {
        assertThatIllegalStateException()
                .isThrownBy(() -> Ruche.nouvelle().noyauPartage("com.acme.monnaie").construire())
                .withMessage("Une ruche a besoin d'au moins un module");
    }

    @Test
    void les_noms_de_modules_sont_uniques() {
        assertThatIllegalStateException()
                .isThrownBy(() -> Ruche.nouvelle()
                        .module("com.acme.boutique.commande")
                        .module("com.acme.historique.commande")
                        .construire())
                .withMessage("Deux modules de la ruche s'appellent 'commande'");
    }

    @Test
    void un_paquetage_n_est_declare_qu_une_fois() {
        assertThatIllegalStateException()
                .isThrownBy(() -> Ruche.nouvelle()
                        .module("com.acme.boutique.commande")
                        .moduleTechnique("com.acme.boutique.commande")
                        .construire())
                .withMessage("Le paquetage 'com.acme.boutique.commande' est déclaré deux fois");
    }

    @Test
    void une_zone_ne_peut_pas_etre_imbriquee_dans_une_autre() {
        assertThatIllegalStateException()
                .isThrownBy(() -> Ruche.nouvelle()
                        .module("com.acme.boutique.commande")
                        .noyauPartage("com.acme.boutique.commande.monnaie")
                        .construire())
                .withMessage("Le paquetage 'com.acme.boutique.commande.monnaie' est imbriqué dans le paquetage "
                        + "déclaré 'com.acme.boutique.commande'");
    }

    @Test
    void une_racine_de_composition_ne_peut_pas_etre_imbriquee_dans_une_autre_zone() {
        assertThatIllegalStateException()
                .isThrownBy(() -> Ruche.nouvelle()
                        .module("com.acme.boutique.commande")
                        .racineDeComposition("com.acme.boutique.commande.application")
                        .construire())
                .withMessage("La racine de composition 'com.acme.boutique.commande.application' est imbriquée dans "
                        + "le paquetage déclaré 'com.acme.boutique.commande'");
    }

    @Test
    void des_zones_peuvent_etre_imbriquees_dans_une_racine_de_composition() {
        Ruche ruche = Ruche.nouvelle()
                .module("com.acme.boutique.commande")
                .racineDeComposition("com.acme.boutique")
                .construire();

        assertThat(ruche.racinesDeComposition()).containsExactly("com.acme.boutique");
    }

    @Test
    void les_paquetages_doivent_etre_des_paquetages_java_valides() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Ruche.nouvelle().module("com.acme..commande"))
                .withMessage("Le paquetage de module 'com.acme..commande' est invalide");
    }

    @Test
    void sans_declaration_une_ruche_est_verifiee_en_francais_avec_les_conventions_francaises() {
        Ruche ruche = Ruche.nouvelle().module("com.acme.boutique.commande").construire();

        assertThat(ruche.langue()).isEqualTo(Langue.FRANCAIS);
        assertThat(ruche.conventions()).isEqualTo(ConventionsRuche.francaises());
    }

    @Test
    void une_ruche_verifiee_en_anglais_attend_les_conventions_anglaises() {
        Ruche ruche = Ruche.nouvelle().langue(Langue.ANGLAIS).module("com.acme.boutique.commande").construire();

        assertThat(ruche.langue()).isEqualTo(Langue.ANGLAIS);
        assertThat(ruche.conventions()).isEqualTo(ConventionsRuche.anglaises());
    }

    @Test
    void des_conventions_declarees_l_emportent_sur_celles_de_la_langue() {
        Ruche ruche = Ruche.nouvelle()
                .langue(Langue.ANGLAIS)
                .conventions(ConventionsRuche.francaises())
                .module("com.acme.boutique.commande")
                .construire();

        assertThat(ruche.langue()).isEqualTo(Langue.ANGLAIS);
        assertThat(ruche.conventions()).isEqualTo(ConventionsRuche.francaises());
    }

    @Test
    void la_configuration_archunit_choisit_la_langue_d_une_ruche() {
        ArchConfiguration.get().setProperty(Langue.PROPRIETE, "en");

        Ruche ruche = Ruche.nouvelle().module("com.acme.boutique.commande").construire();

        assertThat(ruche.langue()).isEqualTo(Langue.ANGLAIS);
        assertThat(ruche.conventions()).isEqualTo(ConventionsRuche.anglaises());
    }

    @Test
    void la_langue_declaree_l_emporte_sur_la_configuration_archunit() {
        ArchConfiguration.get().setProperty(Langue.PROPRIETE, "en");

        Ruche ruche = Ruche.nouvelle().langue(Langue.FRANCAIS).module("com.acme.boutique.commande").construire();

        assertThat(ruche.langue()).isEqualTo(Langue.FRANCAIS);
    }
}
