package search;

import common.Stop;

/**
 * A search result for a prefix search and the stop that matches the prefix
 */
public class PrefixMatch {
    private final String tokenMatched;
    private final Stop stop;

    public PrefixMatch(String tokenMatched, Stop stop) {
        this.tokenMatched = tokenMatched;
        this.stop = stop;
    }

    public String getTokenMatched() {
        return tokenMatched;
    }

    public Stop getStop() {
        return stop;
    }

    @Override
    public String toString() {
        return getTokenMatched();
    }
}
