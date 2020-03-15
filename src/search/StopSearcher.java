package search;

import common.Location;
import common.Stop;
import search.quad.Quad;
import search.quad.QuadSearcher;
import search.trie.PrefixMatch;
import search.trie.PrefixSearcher;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * Manages indexing and searching for bus stops in the journey.
 *
 * @author Matthew Corfiatis
 */
public class StopSearcher {
    private final Collection<Stop> stops;
    private final PrefixSearcher prefixSearcher;
    private final QuadSearcher quadSearcher;

    /**
     * Creates a StopSearcher object and indexes the provided stops to
     * allow easy searching.
     *
     * @param stops Stops to be indexed.
     */
    public StopSearcher(Collection<Stop> stops) {
        this.stops = stops;
        prefixSearcher = new PrefixSearcher(stops);
        quadSearcher = new QuadSearcher(stops);
    }

    /**
     * Searches for stops by name.
     * <p>
     * If one stop name exactly matches the provided value, only that
     * one stop will be returned.
     * If multiple stops match, multiple stops will be returned.
     *
     * @param name Name prefix to search for.
     * @return A collection of stops matching the name prefix.
     */
    public Collection<PrefixMatch> searchPrefix(String name) {
        return prefixSearcher.searchPrefix(name);
    }

    /**
     * Finds the closest stop to a lat/long co-ordinate location.
     * @param location Location to base the search.
     * @return Closest stop or null if none found.
     */
    public Stop searchClosest(Location location) {
        return quadSearcher.searchClosest(new Point2D.Double(location.x, location.y));
//        Stop closestStop = null;
//        double closestDistance = 0;
//
//        // Linear search. Slow
//        for(Stop s : getStops()) {
//            if(closestStop == null) {
//                closestStop = s;
//                closestDistance = s.getLocation().distance(location);
//                continue;
//            }
//            double newDistance = s.getLocation().distance(location);
//            if(newDistance < closestDistance) {
//                closestDistance = newDistance;
//                closestStop = s;
//            }
//        }
//
//        return closestStop;
    }

    /**
     * Streams all stops.
     * @return A stream of all stops.
     */
    public Stream<Stop> streamStops() {
        return getStops().stream();
    }

    /**
     * Gets a collection of all stops.
     * @return A collection of stops.
     */
    public Collection<Stop> getStops() {
        return Collections.unmodifiableCollection(stops);
    }

    public Collection<Quad> getQuads() {
        return quadSearcher.getQuads();
    }

}
