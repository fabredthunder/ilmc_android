package fr.ilovemycity.android.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Fab on 12/11/15.
 * All rights reserved
 */

@SuppressWarnings("UnusedDeclaration")
public class Ticket implements Serializable {

    /**************
     * ATTRIBUTES *
     **************/

    // Creator informations
    private User creator;

    // Ticket informations
    private String   id;
    private String   title;
    private String   description;
    private int      votes;
    private boolean  close;
    private boolean  vote;
    private String   status;
    private String  creatorId;

    // City informations
    private String cityId;

    // Pictures
    private ArrayList<String> pictures;

    // Adress informations
    private String   address;
    private Loc      location;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public boolean isClose() {
        return close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Loc getLocation() {
        return location;
    }

    public void setLocation(Loc location) {
        this.location = location;
    }

    public ArrayList<String> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<String> pictures) {
        this.pictures = pictures;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getStatus() {
        return status;
    }

    public boolean isVote() {
        return vote;
    }

    public void setVote(boolean vote) {
        this.vote = vote;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * Helpers
     */

    public double getLatitude() { return location.getLatitude(); }

    public double getLongitude() { return location.getLongitude(); }

    public LatLng getLatLng() { return new LatLng(getLocation().getCoordinates()[0], getLocation().getCoordinates()[1]); }
}