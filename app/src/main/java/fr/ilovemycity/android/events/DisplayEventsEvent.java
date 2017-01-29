package fr.ilovemycity.android.events;

import java.util.List;

import fr.ilovemycity.android.models.Event;

/**
 * Created by Fab on 29/08/2016.
 * All rights reserved
 */
public class DisplayEventsEvent {

    public List<Event> events;
    public String cityName;

    public DisplayEventsEvent(List<Event> events, String cityName) {
        this.events = events;
        this.cityName = cityName;
    }

}
