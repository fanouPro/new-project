package io.github.fanoupro.ruche;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.PackageMatcher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static io.github.fanoupro.ruche.Paquetages.appartientA;
import static io.github.fanoupro.ruche.Paquetages.estSousPaquetageDe;
import static io.github.fanoupro.ruche.Paquetages.relatifA;

/**
 * Situe les classes dans une {@link Ruche} : la zone à laquelle elles appartiennent et, dans un module, leur couche.
 */
final class TopologieRuche {

    enum TypeDeZone {
        MODULE("modules"),
        NOYAU_PARTAGE("noyaux partagés"),
        ANNOTATIONS("annotations"),
        MODULE_TECHNIQUE("modules techniques"),
        RACINE_DE_COMPOSITION("racines de composition");

        private final String pluriel;

        TypeDeZone(String pluriel) {
            this.pluriel = pluriel;
        }

        String pluriel() {
            return pluriel;
        }
    }

    enum Couche {
        API,
        SPI,
        DOMAINE,
        INFRASTRUCTURE,
        HORS_DU_DOMAINE_ET_DE_L_INFRASTRUCTURE
    }

    record Zone(TypeDeZone type, String paquetage, ModuleRuche module, boolean couvreLesSousPaquetages) {

        boolean contient(String paquetageDeLaClasse) {
            return couvreLesSousPaquetages
                    ? appartientA(paquetageDeLaClasse, paquetage)
                    : paquetageDeLaClasse.equals(paquetage);
        }
    }

    private final Ruche ruche;
    private final ConventionsRuche conventions;
    private final Textes textes;
    private final List<Zone> zonesDeLaPlusSpecifique;
    private final List<PackageMatcher> bibliothequesPures;

    TopologieRuche(Ruche ruche) {
        this.ruche = ruche;
        this.conventions = ruche.conventions();
        this.textes = ruche.langue().textes();
        this.zonesDeLaPlusSpecifique = zonesDe(ruche);
        this.bibliothequesPures = ruche.bibliothequesPures().stream().map(PackageMatcher::of).toList();
    }

    private static List<Zone> zonesDe(Ruche ruche) {
        List<Zone> zones = new ArrayList<>();
        ruche.modules().forEach(module -> zones.add(new Zone(TypeDeZone.MODULE, module.paquetage(), module, true)));
        ruche.noyauxPartages().forEach(noyau -> zones.add(new Zone(TypeDeZone.NOYAU_PARTAGE, noyau, null, true)));
        ruche.annotations().forEach(annotations ->
                zones.add(new Zone(TypeDeZone.ANNOTATIONS, annotations, null, true)));
        ruche.modulesTechniques().forEach(module ->
                zones.add(new Zone(TypeDeZone.MODULE_TECHNIQUE, module, null, true)));
        List<String> zonesHorsRacines = zones.stream().map(Zone::paquetage).toList();
        ruche.racinesDeComposition().forEach(racine -> zones.add(new Zone(TypeDeZone.RACINE_DE_COMPOSITION, racine,
                null, zonesHorsRacines.stream().noneMatch(zone -> estSousPaquetageDe(zone, racine)))));
        zones.sort(Comparator.comparingInt((Zone zone) -> zone.paquetage().length()).reversed());
        return List.copyOf(zones);
    }

    Optional<Zone> zoneDe(JavaClass classe) {
        String paquetageDeLaClasse = classe.getBaseComponentType().getPackageName();
        return zonesDeLaPlusSpecifique.stream().filter(zone -> zone.contient(paquetageDeLaClasse)).findFirst();
    }

    boolean estDans(JavaClass classe, TypeDeZone type) {
        return zoneDe(classe).filter(zone -> zone.type() == type).isPresent();
    }

    boolean estDansUneZoneDeclaree(JavaClass classe) {
        return zoneDe(classe).isPresent();
    }

    boolean estDansLePaquetageDeBase(JavaClass classe) {
        return ruche.paquetageDeBase().filter(base -> appartientA(classe.getPackageName(), base)).isPresent();
    }

    Optional<ModuleRuche> moduleDe(JavaClass classe) {
        return zoneDe(classe).map(Zone::module);
    }

    boolean estDansUnModule(JavaClass classe) {
        return moduleDe(classe).isPresent();
    }

    boolean memeModule(JavaClass premiere, JavaClass seconde) {
        Optional<ModuleRuche> module = moduleDe(premiere);
        return module.isPresent() && module.equals(moduleDe(seconde));
    }

    boolean estDansUnAutreModule(JavaClass origine, JavaClass cible) {
        return estDansUnModule(cible) && !memeModule(origine, cible);
    }

    boolean estPure(JavaClass classe) {
        JavaClass type = classe.getBaseComponentType();
        return type.isPrimitive()
                || bibliothequesPures.stream().anyMatch(bibliotheque -> bibliotheque.matches(type.getPackageName()));
    }

    boolean estExterne(JavaClass classe) {
        return !estPure(classe) && !estDansUneZoneDeclaree(classe);
    }

    boolean estDansLeDomaine(JavaClass classe) {
        return estDansLaCouche(classe, conventions.domaine());
    }

    boolean estDansLApi(JavaClass classe) {
        return estDansLaCouche(classe, conventions.api());
    }

    boolean estDansLaSpi(JavaClass classe) {
        return estDansLaCouche(classe, conventions.spi());
    }

    boolean estDansLInfrastructure(JavaClass classe) {
        return estDansLaCouche(classe, conventions.infrastructure());
    }

    boolean estDansLaConfiguration(JavaClass classe) {
        return estDansLaCouche(classe, conventions.configuration());
    }

    boolean estUnAdaptateurPrimaire(JavaClass classe) {
        return conventions.adaptateursPrimaires().stream()
                .anyMatch(adaptateurs -> estDansLaCouche(classe, adaptateurs));
    }

    boolean estUnAdaptateurIntraprocessus(JavaClass classe) {
        return conventions.adaptateursIntraprocessus().stream()
                .anyMatch(adaptateurs -> estDansLaCouche(classe, adaptateurs));
    }

    private boolean estDansLaCouche(JavaClass classe, String couche) {
        JavaClass type = classe.getBaseComponentType();
        return moduleDe(type)
                .map(module -> appartientA(relatifA(type.getPackageName(), module.paquetage()), couche))
                .orElse(false);
    }

    boolean implementeUnPortApiDe(JavaClass classe, ModuleRuche module) {
        return classe.getAllRawInterfaces().stream()
                .anyMatch(port -> estDansLApi(port) && moduleDe(port).filter(module::equals).isPresent());
    }

    boolean implementeUnPortSpiDe(JavaClass classe, ModuleRuche module) {
        return classe.getAllRawInterfaces().stream()
                .anyMatch(port -> estDansLaSpi(port) && moduleDe(port).filter(module::equals).isPresent());
    }

    boolean implementeUnPortApiDeSonModule(JavaClass classe) {
        return moduleDe(classe).filter(module -> implementeUnPortApiDe(classe, module)).isPresent();
    }

    boolean implementeUnPortSpiDeSonModule(JavaClass classe) {
        return moduleDe(classe).filter(module -> implementeUnPortSpiDe(classe, module)).isPresent();
    }

    boolean estUnServiceDuDomaine(JavaClass classe) {
        JavaClass type = classe.getBaseComponentType();
        return (estDansLeDomaine(type) && !type.isInterface() && implementeUnPortApiDeSonModule(type))
                || ruche.annotationServiceDuDomaine().filter(type::isAnnotatedWith).isPresent();
    }

    boolean estUnBouchon(JavaClass classe) {
        return ruche.annotationBouchon().filter(classe.getBaseComponentType()::isAnnotatedWith).isPresent();
    }

    String decrire(JavaClass classe) {
        JavaClass type = classe.getBaseComponentType();
        Optional<Zone> zone = zoneDe(type);
        if (zone.isEmpty()) {
            return estPure(type) ? textes.bibliothequePure() : textes.paquetageExterne(type.getPackageName());
        }
        if (zone.get().type() != TypeDeZone.MODULE) {
            return textes.zone(zone.get().type(), zone.get().paquetage());
        }
        return textes.couche(coucheDe(type), zone.get().module().nom());
    }

    private Couche coucheDe(JavaClass classe) {
        if (estDansLApi(classe)) {
            return Couche.API;
        }
        if (estDansLaSpi(classe)) {
            return Couche.SPI;
        }
        if (estDansLeDomaine(classe)) {
            return Couche.DOMAINE;
        }
        if (estDansLInfrastructure(classe)) {
            return Couche.INFRASTRUCTURE;
        }
        return Couche.HORS_DU_DOMAINE_ET_DE_L_INFRASTRUCTURE;
    }
}
