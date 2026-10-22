package io.github.fanoupro.hive.fixtures.valid.money;

public record Money(long cents, String currency) {

    public Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add " + other.currency + " to " + currency);
        }
        return new Money(cents + other.cents, currency);
    }
}
