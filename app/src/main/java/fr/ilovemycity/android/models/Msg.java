package fr.ilovemycity.android.models;

import java.io.Serializable;

/**
 * Created by Fab on 12/11/15.
 * All rights reserved
 */

@SuppressWarnings("UnusedDeclaration")
public class Msg implements Serializable {

    /**************
     * ATTRIBUTES *
     **************/

    // Creator informations
    private User creator;

    // Ticket informations
    private String   id;
    private String   text;

    // Date informations
    private String createdAt;
    private String updatedAt;

    /***********
     * METHODS *
     ***********/

    /**
     * Getters and setters
     */

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}