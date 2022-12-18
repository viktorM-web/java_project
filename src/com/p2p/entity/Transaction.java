package com.p2p.entity;

import com.p2p.util.Status;

public class Transaction {
    private Integer id;
    private Offer offer;
    private User consumer;
    private Status status;

    public Transaction(Integer id, Offer offer, User consumer, Status status) {
        this.id = id;
        this.offer = offer;
        this.consumer = consumer;
        this.status = status;
    }

    public Transaction() {
    }

    @Override
    public String toString() {
        return "Transaction{" +
               "id=" + id +
               ", offer=" + offer +
               ", consumer=" + consumer +
               ", status=" + status +
               '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public User getConsumer() {
        return consumer;
    }

    public void setConsumer(User consumer) {
        this.consumer = consumer;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
