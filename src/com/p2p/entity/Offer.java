package com.p2p.entity;

import com.p2p.util.Currency;
import com.p2p.util.Operation;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Offer {
    private Integer id;
    private User supplier;
    private BigDecimal sum;
    private Currency currency;
    private BigDecimal price;
    private Currency expectedCurrency;
    private LocalDate publication;
    private Operation operation;

    public Offer(Integer id, User supplier, BigDecimal sum, Currency currency,
                 BigDecimal price, Currency expectedCurrency, LocalDate publication, Operation operation) {
        this.id = id;
        this.supplier = supplier;
        this.sum = sum;
        this.currency = currency;
        this.price = price;
        this.expectedCurrency = expectedCurrency;
        this.publication = publication;
        this.operation = operation;
    }
    public Offer(){
    }

    @Override
    public String toString() {
        return "Offer{" +
               "id=" + id +
               ", supplier=" + supplier +
               ", sum=" + sum +
               ", currency=" + currency +
               ", price=" + price +
               ", expected_Currency=" + expectedCurrency +
               ", publication=" + publication +
               ", operation=" + operation +
               '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getSupplier() {
        return supplier;
    }

    public void setSupplier(User supplier) {
        this.supplier = supplier;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Currency getExpectedCurrency() {
        return expectedCurrency;
    }

    public void setExpectedCurrency(Currency expectedCurrency) {
        this.expectedCurrency = expectedCurrency;
    }

    public LocalDate getPublication() {
        return publication;
    }

    public void setPublication(LocalDate publication) {
        this.publication = publication;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }
}
