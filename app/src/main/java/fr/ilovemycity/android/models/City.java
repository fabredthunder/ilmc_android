package fr.ilovemycity.android.models;

import java.io.Serializable;

/**
 * Created by Fab on 12/11/15.
 * All rights reserved
 */

@SuppressWarnings("UnusedDeclaration")
public class City implements Serializable {

    /**************
     * ATTRIBUTES *
     **************/

    // City informations
    private String      id;
    private String      name;
    private Loc         location;
    private boolean     activated;
    private String      address;
    private String      website;
    private String      phoneNumber;
    private String      adminId;
    private String[]    pictures;
    private String[]    openingHours;


    // Date informations
    private String createdAt;
    private String updatedAt;

    /***********
     * METHODS *
     ***********/

    /**
     * Getters and setters
     */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Loc getLocation() {
        return location;
    }

    public void setLocation(Loc location) {
        this.location = location;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String[] getPictures() {
        return pictures;
    }

    public void setPictures(String[] pictures) {
        this.pictures = pictures;
    }

    public String[] getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String[] openingHours) {
        this.openingHours = openingHours;
    }
}