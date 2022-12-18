package com.p2p.entity;

public class User {
    private Integer id;
    private String firstName;
    private String LastName;
    private String numberPassport;
    private String email;
    private String numberTelephone;
    private Boolean admin;

    public User(Integer id, String firstName, String lastName, String numberPassport, String email,
                String numberTelephone, Boolean admin) {
        this.id = id;
        this.firstName = firstName;
        LastName = lastName;
        this.numberPassport = numberPassport;
        this.email = email;
        this.numberTelephone = numberTelephone;
        this.admin = admin;
    }

    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", LastName='" + LastName + '\'' +
               ", numberPassport='" + numberPassport + '\'' +
               ", email='" + email + '\'' +
               ", numberTelephone='" + numberTelephone + '\'' +
               ", admin=" + admin +
               '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getNumberPassport() {
        return numberPassport;
    }

    public void setNumberPassport(String numberPassport) {
        this.numberPassport = numberPassport;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumberTelephone() {
        return numberTelephone;
    }

    public void setNumberTelephone(String numberTelephone) {
        this.numberTelephone = numberTelephone;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
