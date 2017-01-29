package fr.ilovemycity.android.events;

import android.content.Intent;

/**
 * Created by Fab on 29/08/2016.
 * All rights reserved
 */
public class GalleryResultEvent {

    public Intent data;

    public GalleryResultEvent(Intent data) {
        this.data = data;
    }

}
