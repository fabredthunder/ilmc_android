package fr.ilovemycity.android.events;

import java.util.List;

import fr.ilovemycity.android.models.Survey;

/**
 * Created by Fab on 29/08/2016.
 * All rights reserved
 */
public class DisplaySurveysEvent {

    public List<Survey> surveys;
    public String cityName;

    public DisplaySurveysEvent(List<Survey> surveys, String cityName) {
        this.surveys = surveys;
        this.cityName = cityName;
    }

}
