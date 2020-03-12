package search.trie;

import common.Stop;

import java.util.*;

/**
 * A node in a trie structure that contains a character in the name of a
 * bus stop and a Stop object if the name is complete.
 *
 * @author Matthew Corfiatis
 */
public class StopNode {
    private final boolean isRoot;
    private final boolean isId;
    private final char nameCharacter;
    private final List<PrefixMatch> stops = new ArrayList<>();
    private final Map<Character, StopNode> children = new HashMap<>();
    private boolean childrenLocked;

    /**
     * Constructor for constructing a root node.
     */
    public StopNode() {
        this.isRoot = true;
        this.nameCharacter = 0;
        this.isId = false;
    }

    /**
     * Constructor for constructing a branch node.
     * @param nameCharacter Character in the bus stop name.
     */
    public StopNode(char nameCharacter) {
        this.isRoot = false;
        this.nameCharacter = nameCharacter;
        this.isId = false;
    }

    public char getNameCharacter() {
        return nameCharacter;
    }

    public List<PrefixMatch> getStops() {
        return Collections.unmodifiableList(stops);
    }

    public boolean isId() {
        return isId;
    }

    /**
     * Checks whether this node is a leaf node that contains
     * a stop object.
     * @return Boolean indicating if this is a leaf node and contains a Stop.
     */
    public boolean hasStop() {
        return !stops.isEmpty();
    }

    /**
     * Adds a stop to the list of stops that match the path in the trie.
     * @param stop The stop to add.
     * @param useId Whether the string value to be matched should use the ID or the name of the stop.
     */
    public void addStop(Stop stop, boolean useId) {
        if(childrenLocked)
            throw new IllegalAccessError("Cannot add stop locked node.");

        stops.add(new PrefixMatch(useId ? stop.getId() : stop.getName(), stop));
    }

    /**
     * Checks if a child with the character exists.
     * @param c Character of the child.
     * @return Boolean indicating if it exists.
     */
    public boolean hasChild(char c) {
        return children.containsKey(c);
    }

    /**
     * Returns a child with the specified name character.
     * @param c Character of the child.
     * @return Child stop node or null if no child exists
     * with the specified name character.
     */
    public StopNode getChild(char c) {
        return children.get(c);
    }

    /**
     * Returns collection of all children with a depth of one.
     * @return Children.
     */
    public Collection<StopNode> getChildren() {
        return children.values();
    }

    /**
     * Adds a stop node as a child.
     * @param c Name character for the new node.
     * @param node The node to add.
     * @return Added node or null if add failed because of existing StopNode.
     * @throws IllegalAccessError When this method is called but the node has been locked immutable.
     */
    public StopNode addChild(char c, StopNode node) throws IllegalAccessError {
        if(childrenLocked)
            throw new IllegalAccessError("Cannot add child node to locked node.");

        if(children.putIfAbsent(c, node) == null)
            return node;
        else
            return null;
    }

    /**
     * Prevents modification of this node.
     */
    public void lockChildren() {
        childrenLocked = true;
    }
}
