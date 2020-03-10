package common;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Represents a bus trip consisting of multiple stops.
 * Trips are formed using Connections inside a Stop object.
 *
 * @author Matthew Corfiatis
 */
public class Trip {
    private final String id;
    private final List<Stop> stops = new ArrayList<>();
    private boolean lockStops;

    public Trip(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<Stop> getStops() {
        return Collections.unmodifiableList(stops);
    }

    /**
     * Adds a stop to the trip.
     * @param stop Stop to add.
     * @throws IllegalAccessError When method is called but the stops have been locked.
     */
    public void addStop(Stop stop) throws IllegalAccessError {
        if(lockStops)
            throw new IllegalAccessError();
        stops.add(stop);
    }

    /**
     * Prevents stops from being added after this method is called.
     */
    public void lockStops() {
        lockStops = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return Objects.equals(getId(), trip.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
