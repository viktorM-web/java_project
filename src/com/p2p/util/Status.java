package com.p2p.util;

import java.util.Optional;

public enum Status {
    PROCESSED("PROCESSED"),
    DISMISSED("DISMISSED"),
    SUCCESS("SUCCESS");

    private final String name;

    Status(String name) {
        this.name = name;
    }

    public static Optional<Status> getStatus(String statusName) {
        Status result = null;
        for (Status status : Status.values()) {
            if (status.name.equals(statusName)) {
                result = status;
            }
        }
        return Optional.ofNullable(result);
    }
}
