package fr.ilovemycity.android.webservice.bundles;

import java.io.Serializable;

/**
 * Created by Fab on 10/11/2016.
 * All rights reserved
 */

public class VoteBundle implements Serializable {

    private String vote;

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }
}
