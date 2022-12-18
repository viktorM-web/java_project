package com.p2p.entity;

import com.p2p.util.Currency;

import java.math.BigDecimal;

public class WalletContent {
    private Integer id;
    private CryptoWallet idNumber;
    private Currency cryptocurrency;
    private BigDecimal amount;

    public WalletContent(Integer id, CryptoWallet idNumber, Currency cryptocurrency, BigDecimal amount) {
        this.id = id;
        this.idNumber = idNumber;
        this.cryptocurrency = cryptocurrency;
        this.amount = amount;
    }

    public WalletContent() {
    }

    @Override
    public String toString() {
        return "WalletContent{" +
               "id=" + id +
               ", idNumber=" + idNumber +
               ", cryptocurrency=" + cryptocurrency +
               ", amount=" + amount +
               '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CryptoWallet getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(CryptoWallet idNumber) {
        this.idNumber = idNumber;
    }

    public Currency getCryptocurrency() {
        return cryptocurrency;
    }

    public void setCryptocurrency(Currency cryptocurrency) {
        this.cryptocurrency = cryptocurrency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
