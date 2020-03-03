package common;

/**
 * Represents a connection from one stop to another on a certain trip.
 *
 * Connections are one way links, so for two stops that are both connected together, two connections are required.
 * This means that one way roads with a stop can just have a one way link without implying that a bus can travel
 * back the same way.
 *
 * @author Matthew Corfiatis
 */
public class Connection {
    private final Stop stop;
    private final Trip trip;

    Connection(Stop stop, Trip trip) {
        this.stop = stop;
        this.trip = trip;
    }

    public Stop getStop() {
        return stop;
    }

    public Trip getTrip() {
        return trip;
    }
}
