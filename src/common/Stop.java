package common;

import java.util.*;

/**
 * Represents a bus stop.
 * The bus stop can be part of a trip and have connections to other bus stops.
 * Stops are linked together using Connections to form Trips.
 *
 * @author Matthew Corfiatis
 */
public class Stop {
    private final String id;
    private final String name;
    private final Location location;
    private final List<Connection> outgoingConnections = new ArrayList<>();
    private final List<Connection> incomingConnections = new ArrayList<>();
    private boolean lockConnections; // Prevents adding new connections once the object has been created.

    /**
     * Creates new stop object.
     * @param id Unique ID of the stop.
     * @param name Name of the stop.
     * @param location Location of the stop.
     * @throws IllegalArgumentException When non nullable arguments are null.
     */
    public Stop(String id, String name, Location location) throws IllegalArgumentException {
        if(id == null)
            throw new IllegalArgumentException("ID must not be null.");

        if(name == null)
            throw new IllegalArgumentException("Name must not be null.");

        if(location == null)
            throw new IllegalArgumentException("Location must not be null.");

        this.id = id;
        this.name = name;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public List<Connection> getOutgoingConnections() {
        return Collections.unmodifiableList(outgoingConnections);
    }

    public List<Connection> getIncomingConnections() {
        return Collections.unmodifiableList(incomingConnections);
    }

    public Set<Trip> getTrips() {
        Set<Trip> trips = new HashSet<>();

        for(Connection c : outgoingConnections)
            trips.add(c.getTrip());

        for(Connection c : incomingConnections)
            trips.add(c.getTrip());

        return trips;
    }

    /**
     * Creates a connection between this stop and another stop within a trip.
     * @param stop Stop to connect to.
     * @param trip The trip that contains the connection.
     * @throws IllegalAccessError When method is called after the connections have been locked.
     * @throws IllegalArgumentException When non nullable arguments are null.
     */
    public void makeOutgoingConnection(Stop stop, Trip trip) throws IllegalAccessError, IllegalArgumentException {
        if(stop == null)
            throw new IllegalArgumentException("Stop must not be null.");

        if(trip == null)
            throw new IllegalArgumentException("Trip must not be null.");

        if(lockConnections)
            throw new IllegalAccessError("Connections cannot be added on a locked object.");
        outgoingConnections.add(new Connection(stop, trip));
    }

    /**
     * Creates a connection between another stop and this stop within a trip.
     * @param stop Stop to connect from.
     * @param trip The trip that contains the connection.
     * @throws IllegalAccessError When method is called after the connections have been locked.
     * @throws IllegalArgumentException When non nullable arguments are null.
     */
    public void makeIncomingConnection(Stop stop, Trip trip) throws IllegalAccessError, IllegalArgumentException {
        if(stop == null)
            throw new IllegalArgumentException("Stop must not be null.");

        if(trip == null)
            throw new IllegalArgumentException("Trip must not be null.");

        if(lockConnections)
            throw new IllegalAccessError("Connections cannot be added on a locked object.");
        incomingConnections.add(new Connection(stop, trip));
    }

    /**
     * Prevents connections from being modified inside this stop.
     */
    public void lockConnections() {
        lockConnections = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stop stop = (Stop) o;
        return Objects.equals(getId(), stop.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Stop{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
