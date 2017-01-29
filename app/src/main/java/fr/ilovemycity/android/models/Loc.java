package fr.ilovemycity.android.models;

/**
 * Created by Fab on 19/06/2016.
 * All rights reserved
 */

@SuppressWarnings("UnusedDeclaration")
public class Loc {

    /**************
     * ATTRIBUTES *
     **************/

    private double   latitude;
    private double   longitude;
    private double[] coordinates;

    /***********
     * METHODS *
     ***********/

    /**
     * Getters and setters
     */

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
