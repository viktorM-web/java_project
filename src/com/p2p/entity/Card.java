package com.p2p.entity;

import com.p2p.util.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Card {
    private Long idNumber;
    private User owner;
    private LocalDate validity;
    private BigDecimal balance;
    private Currency currency;

    public Card(Long idNumber, User owner, LocalDate validity, BigDecimal balance, Currency currency) {
        this.idNumber = idNumber;
        this.owner = owner;
        this.validity = validity;
        this.balance = balance;
        this.currency = currency;
    }

    public Card() {
    }

    @Override
    public String toString() {
        return "Card{" +
               "idNumber='" + idNumber + '\'' +
               ", owner=" + owner +
               ", validity=" + validity +
               ", balance=" + balance +
               ", currency=" + currency +
               '}';
    }

    public Long getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(Long idNumber) {
        this.idNumber = idNumber;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public LocalDate getValidity() {
        return validity;
    }

    public void setValidity(LocalDate validity) {
        this.validity = validity;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
