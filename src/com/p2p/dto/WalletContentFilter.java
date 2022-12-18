package com.p2p.dto;

import com.p2p.entity.CryptoWallet;
import com.p2p.util.Currency;
import com.p2p.util.Pair;

import java.math.BigDecimal;

public record WalletContentFilter(
        int limit,
        int offset,
        CryptoWallet idNumber,
        Currency cryptocurrency,
        Pair<String, BigDecimal> amountWithCondition) {
}
