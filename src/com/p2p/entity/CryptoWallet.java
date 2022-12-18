package com.p2p.entity;

public class CryptoWallet {
    private String idNumber;
    private User owner;

    public CryptoWallet(String idNumber, User owner) {
        this.idNumber = idNumber;
        this.owner = owner;
    }

    public CryptoWallet() {
    }

    @Override
    public String toString() {
        return "CryptoWallet{" +
               "idNumber='" + idNumber + '\'' +
               ", owner=" + owner +
               '}';
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
