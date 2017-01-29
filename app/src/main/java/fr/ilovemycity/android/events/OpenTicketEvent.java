package fr.ilovemycity.android.events;

import fr.ilovemycity.android.models.Ticket;

/**
 * Created by Fab on 29/08/2016.
 * All rights reserved
 */
public class OpenTicketEvent {

    public Ticket ticket;

    public OpenTicketEvent(Ticket ticket) {
        this.ticket = ticket;
    }

}
