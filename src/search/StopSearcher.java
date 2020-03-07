package search;

import common.Stop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

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
     *
     * @param stops Stops to be indexed.
     */
    public StopSearcher(Collection<Stop> stops) {
        buildTrie(stops);
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
        List<PrefixMatch> matches = new ArrayList<>();

        if (name == null || name.length() == 0)
            return matches;

        StopNode node = findNode(name.toLowerCase());

        if (node == null) {
            matches.add(new PrefixMatch("No results found.", null));
            return matches;
        }

        // Returns a single node if the query was matched exactly.
        if(node.hasStop()) {
            node.getStops().forEach(matches::add);
            return matches;
        }

        // Depth first search for leaf nodes.
        Stack<StopNode> searchNodes = new Stack<>();
        searchNodes.push(node);

        while (!searchNodes.isEmpty()) {
            StopNode searchNode = searchNodes.pop();

            if (searchNode.hasStop())
                searchNode.getStops().forEach(matches::add);

            for (StopNode childNode : searchNode.getChildren()) {
                searchNodes.push(childNode);
            }
        }

        return matches;
    }

    /**
     * Prints trie into the console for debugging.
     */
    public void printTrie() {
        printNodes(trieRoot, "");
    }

    /**
     * Prints a node and all its children to the console.
     *
     * @param node  Node to print.
     * @param space Recursive space.
     */
    private void printNodes(StopNode node, String space) {
        System.out.print(space);
        System.out.println(node.getNameCharacter());

        for (StopNode child : node.getChildren()) {
            printNodes(child, space + "  ");
        }
    }

    /**
     * Find node with path matching search prefix.
     * @param query
     * @return
     */
    private StopNode findNode(String query) {
        StopNode node = trieRoot;
        boolean resultsFound = true;

        for (int i = 0; i < query.length(); ++i) {
            if (node.hasChild(query.charAt(i))) {
                node = node.getChild(query.charAt(i));
            } else {
                resultsFound = false;
                break;
            }
        }

        if(!resultsFound)
            return null;

        return node;
    }

    /**
     * Builds search trie from collection of stops.
     *
     * @param stops Collection of stops to build trie from.
     */
    private void buildTrie(Collection<Stop> stops) {
        if (stops == null)
            throw new IllegalArgumentException("Stops must not be null.");

        // Add stop ID's and names to the trie.
        for (Stop stop : stops) {
            if(stop.getName().equals("app")) {
                System.out.println("A");
            }
            addStop(stop, stop.getId().toLowerCase(), true);
            addStop(stop, stop.getName().toLowerCase(), false);
        }

        // Depth first traversal.
        Stack<StopNode> nodes = new Stack<>();
        nodes.push(trieRoot);

        // Lock all nodes to prevent modification (make immutable).
        while (!nodes.isEmpty()) {
            StopNode node = nodes.pop();
            node.lockChildren();

            for (StopNode childNode : node.getChildren()) {
                nodes.push(childNode);
            }
        }
    }

    /**
     * Adds a stop to the search trie.
     *
     * @param stop Stop to add.
     * @param tag  Tag to add the stop as. (Name or ID)
     */
    private void addStop(Stop stop, String tag, boolean useId) {
        StopNode node = trieRoot;

        if (stop == null)
            throw new IllegalArgumentException("Stop must not be null.");

        if (tag == null || tag.length() < 1)
            throw new IllegalArgumentException("Cannot add a node with no name.");

        // Add path to leaf node
        char[] chars = tag.toCharArray();

        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            if (node.hasChild(c))
                node = node.getChild(c);
            else
                node = node.addChild(c, new StopNode(c));
        }

        node.addStop(stop, useId);
    }
}
