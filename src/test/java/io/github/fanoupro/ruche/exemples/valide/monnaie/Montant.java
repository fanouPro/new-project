package io.github.fanoupro.ruche.exemples.valide.monnaie;

public record Montant(long centimes, String devise) {

    public Montant ajouter(Montant autre) {
        if (!devise.equals(autre.devise)) {
            throw new IllegalArgumentException("Impossible d'ajouter " + autre.devise + " à " + devise);
        }
        return new Montant(centimes + autre.centimes, devise);
    }
}
