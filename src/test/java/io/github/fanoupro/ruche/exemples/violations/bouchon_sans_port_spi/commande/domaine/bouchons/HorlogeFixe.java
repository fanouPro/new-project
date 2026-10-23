package io.github.fanoupro.ruche.exemples.violations.bouchon_sans_port_spi.commande.domaine.bouchons;

import io.github.fanoupro.ruche.exemples.ddd.Bouchon;

import java.time.Instant;

@Bouchon
public class HorlogeFixe {

    public Instant maintenant() {
        return Instant.EPOCH;
    }
}
