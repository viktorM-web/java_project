package com.p2p.util;

import java.util.Optional;

public enum Operation {
    SELL("SELL"),
    BUY("BUY");

    private final String name;

    Operation(String name) {
        this.name = name;
    }

    public static Optional<Operation> getOperation(String operationName) {
        Operation result = null;
        for (Operation operation : Operation.values()) {
            if (operation.name.equals(operationName)) {
                result = operation;
            }
        }
        return Optional.ofNullable(result);
    }
}
