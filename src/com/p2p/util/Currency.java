package com.p2p.util;

import java.util.Optional;

public enum Currency {
    RUB("RUB"),
    USD("USD"),
    EUR("EUR"),
    BTC("BTC"),
    ETH("ETH"),
    USDT("USDT");

    private final String name;

    Currency(String name) {
        this.name = name;
    }

    public static Optional<Currency> getCurrency(String currencyName) {
        Currency result = null;
        for (Currency currency : Currency.values()) {
            if (currency.name.equals(currencyName)) {
                result = currency;
            }
        }
        return Optional.ofNullable(result);
    }

}
