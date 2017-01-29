package fr.ilovemycity.android.events;

import android.content.Intent;

/**
 * Created by Fab on 29/08/2016.
 * All rights reserved
 */
public class CameraResultEvent {

    public Intent data;

    public CameraResultEvent(Intent data) {
        this.data = data;
    }

}
