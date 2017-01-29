package fr.ilovemycity.android.models;

import java.io.Serializable;

/**
 * Created by Fab on 12/11/15.
 * All rights reserved
 */

@SuppressWarnings("UnusedDeclaration")
public class User implements Serializable {

    /**************
     * ATTRIBUTES *
     **************/

    private String  username;
    private String  id;
    private String  email;
    private String  password;
    private String  currPassword;
    private String  token;
    private String  firstname;
    private String  lastname;
    private String  phone;
    private String  picture;
    private String  createdAt;
    private Boolean admin;
    private String  cityId;

    /***********
     * METHODS *
     ***********/

    public User() {}

    public User(String username, String id, String email, String password, String token,
                String firstname, String lastname, String phone, String picture,
                String createdAt, Boolean admin, String cityId) {
        this.username = username;
        this.id = id;
        this.email = email;
        this.password = password;
        this.token = token;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.picture = picture;
        this.createdAt = createdAt;
        this.admin = admin;
        this.cityId = cityId;
    }

    /**
     * Getters and setters
     */

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCurrPassword() {
        return currPassword;
    }

    public void setCurrPassword(String currPassword) {
        this.currPassword = currPassword;
    }

    /**
     * Helpers
     */

    public String getFullName() {
        return firstname + " " + lastname;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", currPassword='" + currPassword + '\'' +
                ", token='" + token + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", phone='" + phone + '\'' +
                ", picture='" + picture + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", admin=" + admin +
                ", cityId='" + cityId + '\'' +
                '}';
    }
}