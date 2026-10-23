package io.github.fanoupro.ruche.exemples.violations.annotations_dependent_d_une_bibliotheque_externe.ddd;

import org.springframework.stereotype.Service;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Service
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceDuDomaine {
}
