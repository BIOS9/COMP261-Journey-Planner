package search;

import common.Stop;

/**
 * A node in a trie structure that contains a character in the name of a
 * bus stop and a Stop object if the name is complete.
 *
 * @author Matthew Corfiatis
 */
class StopNode {
    private final char nameCharacter;
    private final Stop stop;

    public StopNode(char nameCharacter, Stop stop) {
        this.nameCharacter = nameCharacter;
        this.stop = stop;
    }

    public char getNameCharacter() {
        return nameCharacter;
    }

    public Stop getStop() {
        return stop;
    }

    /**
     * Checks whether this node is a leaf node that contains
     * a stop object.
     * @return Boolean indicating if this is a leaf node and contains a Stop.
     */
    public boolean hasStop() {
        return stop != null;
    }
}
