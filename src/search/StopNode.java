package search;

import common.Stop;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A node in a trie structure that contains a character in the name of a
 * bus stop and a Stop object if the name is complete.
 *
 * @author Matthew Corfiatis
 */
class StopNode {
    private final boolean isRoot;
    private final boolean isId;
    private final char nameCharacter;
    private final Stop stop;
    private final Map<Character, StopNode> children = new HashMap<>();
    private boolean childrenLocked;

    /**
     * Constructor for constructing a root node.
     */
    public StopNode() {
        this.isRoot = true;
        this.nameCharacter = 0;
        this.stop = null;
        this.isId = false;
    }

    /**
     * Constructor for constructing a branch node.
     * @param nameCharacter Character in the bus stop name.
     */
    public StopNode(char nameCharacter) {
        this.isRoot = false;
        this.nameCharacter = nameCharacter;
        this.stop = null;
        this.isId = false;
    }

    /**
     * Constructor for constructing a leaf node.
     * @param nameCharacter Character in the bus stop name.
     * @param stop Stop to store.
     * @param isId Whether the prefix is a stop ID instead of a stop name.
     */
    public StopNode(char nameCharacter, Stop stop, boolean isId) {
        this.isRoot = false;
        this.nameCharacter = nameCharacter;
        this.stop = stop;
        this.isId = isId;
    }

    public char getNameCharacter() {
        return nameCharacter;
    }

    public Stop getStop() {
        return stop;
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
        return stop != null;
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
