package io.github.fanoupro.ruche;

import java.util.regex.Pattern;

final class Paquetages {

    private static final Pattern PAQUETAGE = Pattern.compile("[A-Za-z_$][\\w$]*(\\.[A-Za-z_$][\\w$]*)*");

    private Paquetages() {
    }

    static String exigerPaquetageValide(String paquetage, String quoi) {
        if (paquetage == null || !PAQUETAGE.matcher(paquetage).matches()) {
            throw new IllegalArgumentException("Le " + quoi + " '" + paquetage + "' est invalide");
        }
        return paquetage;
    }

    static boolean appartientA(String paquetage, String ancetre) {
        return paquetage.equals(ancetre) || paquetage.startsWith(ancetre + ".");
    }

    static boolean estSousPaquetageDe(String paquetage, String ancetre) {
        return paquetage.startsWith(ancetre + ".");
    }

    static String relatifA(String paquetage, String ancetre) {
        return paquetage.equals(ancetre) ? "" : paquetage.substring(ancetre.length() + 1);
    }

    static String dernierSegment(String paquetage) {
        return paquetage.substring(paquetage.lastIndexOf('.') + 1);
    }
}
