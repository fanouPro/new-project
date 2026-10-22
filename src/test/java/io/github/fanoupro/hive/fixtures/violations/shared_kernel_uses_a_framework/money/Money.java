package io.github.fanoupro.hive.fixtures.violations.shared_kernel_uses_a_framework.money;

import jakarta.persistence.Embeddable;

@Embeddable
public record Money(long cents) {
}
