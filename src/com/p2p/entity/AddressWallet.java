package com.p2p.entity;

public class AddressWallet {
    private Integer id;
    private WalletContent idWalletContent;
    private String net;
    private String address;

    public AddressWallet(Integer id, WalletContent idWalletContent, String net, String address) {
        this.id = id;
        this.idWalletContent = idWalletContent;
        this.net = net;
        this.address = address;
    }

    public AddressWallet() {
    }

    @Override
    public String toString() {
        return "AddressWallet{" +
               "id=" + id +
               ", net='" + net + '\'' +
               ", address='" + address + '\'' +
               '}';
    }

    public WalletContent getIdWalletContent() {
        return idWalletContent;
    }

    public void setIdWalletContent(WalletContent idWalletContent) {
        this.idWalletContent = idWalletContent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
