package com.p2p.dto;

import com.p2p.entity.User;

public record CryptoWalletFilter(
        int limit,
        int offset,
        User owner) {
}
