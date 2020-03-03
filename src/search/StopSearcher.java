package search;

import common.Stop;

import java.util.Collection;

/**
 * Manages indexing and searching for bus stops in the journey.
 *
 * @author Matthew Corfiatis
 */
public class StopSearcher {

    /**
     * Creates a StopSearcher object and indexes the provided stops to
     * allow easy searching.
     * @param stops Stops to be indexed.
     */
    public StopSearcher(Collection<Stop> stops) {
        // TODO: Create trie structure for index.
    }

    /**
     * Searches for stops by name.
     *
     * If one stop name exactly matches the provided value, only that
     * one stop will be returned.
     * If multiple stops match, multiple stops will be returned.
     *
     * @param name Name prefix to search for.
     * @return A collection of stops matching the name prefix.
     */
    public Collection<Stop> searchName(String name) {
        // TODO: Return the collection of stops.
        return null;
    }
}
