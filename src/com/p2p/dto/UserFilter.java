package com.p2p.dto;

public record UserFilter(
        int limit,
        int offset,
        String firstName,
        String lastName,
        String email,
        String numberTelephone,
        Boolean admin) {
}
