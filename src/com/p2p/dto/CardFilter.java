package com.p2p.dto;

import com.p2p.entity.User;
import com.p2p.util.Currency;
import com.p2p.util.Pair;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardFilter(
        int limit,
        int offset,
        User owner,
        Pair<String, LocalDate> validityWithCondition,
        Pair<String, BigDecimal> balanceWithCondition,
        Currency currency) {
}
