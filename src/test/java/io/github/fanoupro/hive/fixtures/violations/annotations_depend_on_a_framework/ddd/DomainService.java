package io.github.fanoupro.hive.fixtures.violations.annotations_depend_on_a_framework.ddd;

import org.springframework.stereotype.Service;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Service
@Retention(RetentionPolicy.RUNTIME)
public @interface DomainService {
}
