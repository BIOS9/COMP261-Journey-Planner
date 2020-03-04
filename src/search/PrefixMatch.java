package search;

import common.Stop;

/**
 * A matched search and the stop that matches the prefix
 */
public class PrefixMatch {
    private final String tokenMatched;
    private final Stop stop;

    public PrefixMatch(String prefixMatched, Stop stop) {
        this.tokenMatched = prefixMatched;
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
        return tokenMatched;
    }
}
