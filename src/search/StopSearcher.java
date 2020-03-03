package search;

import common.Stop;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Manages indexing and searching for bus stops in the journey.
 *
 * @author Matthew Corfiatis
 */
public class StopSearcher {
    private final StopNode trieRoot = new StopNode();

    /**
     * Creates a StopSearcher object and indexes the provided stops to
     * allow easy searching.
     * @param stops Stops to be indexed.
     */
    public StopSearcher(Collection<Stop> stops) {
        buildTrie(stops);
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

    /**
     * Prints trie into the console for debugging.
     */
    public void printTrie() {
        printNodes(trieRoot, "");
    }

    /**
     * Prints a node and all its children to the console.
     * @param node Node to print.
     * @param space Recursive space.
     */
    private void printNodes(StopNode node, String space) {
        System.out.print(space);
        System.out.println(node.getNameCharacter());

        for(StopNode child : node.getChildren()) {
            printNodes(child, space + "  ");
        }
    }

    /**
     * Builds search trie from collection of stops.
     * @param stops Collection of stops to build trie from.
     */
    private void buildTrie(Collection<Stop> stops) {
        if(stops == null)
            throw new IllegalArgumentException("Stops must not be null.");

        for(Stop stop : stops) {
            addStop(stop, stop.getId());
            addStop(stop, stop.getName());
        }
    }

    /**
     * Adds a stop to the search trie.
     * @param stop Stop to add.
     * @param tag  Tag to add the stop as. (Name or ID)
     */
    private void addStop(Stop stop, String tag) {
        StopNode node = trieRoot;

        if(stop == null)
            throw new IllegalArgumentException("Stop must not be null.");

        if(tag == null || tag.length() < 1)
            throw new IllegalArgumentException("Cannot add a node with no name.");

        // Add path to leaf node
        char[] chars = tag.toCharArray();

        for (int i = 0; i < chars.length - 1; ++i) {
            char c = chars[i];
            if(node.hasChild(c))
                node = node.getChild(c);
            else
                node = node.addChild(c, new StopNode(c));
        }

        // Add leaf node.
        char finalChar = chars[chars.length - 1];
        node.addChild(finalChar, new StopNode(finalChar, stop));
    }
}
