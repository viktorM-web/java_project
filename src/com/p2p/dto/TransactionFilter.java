package com.p2p.dto;

import com.p2p.entity.Offer;
import com.p2p.entity.User;
import com.p2p.util.Status;

public record TransactionFilter(int limit,
                                int offset,
                                Offer offer,
                                User consumer,
                                Status status) {
}
