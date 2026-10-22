package io.github.fanoupro.hive.fixtures.valid.ordering.domain;

public class UnknownProductException extends RuntimeException {

    public UnknownProductException(String sku) {
        super("Unknown product " + sku);
    }
}
