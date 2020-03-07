package io;

import common.Trip;

/**
 * Represents a trip object before the stops have been linked to it directly.
 * Stores ID's of stops and the trip that will be linked.
 *
 * @author Matthew Corfiatis
 */
class UnlinkedTrip {
    private final Trip trip;
    private final String[] stopIDs;

    public UnlinkedTrip(Trip trip, String[] stopIDs) {
        this.trip = trip;
        this.stopIDs = stopIDs;
    }

    public Trip getTrip() {
        return trip;
    }

    public String[] getStopIDs() {
        return stopIDs;
    }
}
