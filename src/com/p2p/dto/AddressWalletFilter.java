package com.p2p.dto;

import com.p2p.entity.WalletContent;

public record AddressWalletFilter(
        int limit,
        int offset,
        WalletContent idWalletContent,
        String net,
        String address) {
}
