package com.p2p.dto;

import com.p2p.entity.User;
import com.p2p.util.Currency;
import com.p2p.util.Operation;
import com.p2p.util.Pair;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OfferFilter(
        int limit,
        int offset,
        User supplier,
        Pair<String, BigDecimal> sumWithCondition,
        Currency currency,
        Pair<String, BigDecimal> priceWithCondition,
        Currency expectedCurrency,
        Pair<String, LocalDate> publicationWithCondition,
        Operation operation) {
}
