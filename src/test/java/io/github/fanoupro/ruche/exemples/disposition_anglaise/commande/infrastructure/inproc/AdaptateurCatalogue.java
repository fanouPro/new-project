package io.github.fanoupro.ruche.exemples.disposition_anglaise.commande.infrastructure.inproc;

import io.github.fanoupro.ruche.exemples.disposition_anglaise.catalogue.domain.api.TrouverProduit;
import io.github.fanoupro.ruche.exemples.disposition_anglaise.commande.domain.spi.Catalogue;
import org.springframework.stereotype.Component;

@Component
public class AdaptateurCatalogue implements Catalogue {

    private final TrouverProduit trouverProduit;

    public AdaptateurCatalogue(TrouverProduit trouverProduit) {
        this.trouverProduit = trouverProduit;
    }

    @Override
    public boolean propose(String reference) {
        return trouverProduit.parReference(reference).isPresent();
    }
}
