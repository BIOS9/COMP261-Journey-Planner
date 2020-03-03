package search;

import common.Stop;

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
    private final char nameCharacter;
    private final Stop stop;
    private final Map<Character, StopNode> children = new HashMap<>();
    private boolean childrenLocked;

    /**
     * Constructor for constructing a root node.
     */
    public StopNode() {
        isRoot = true;
        nameCharacter = 0;
        stop = null;
    }

    /**
     * Constructor for constructing a branch node.
     * @param nameCharacter Character in the bus stop name.
     */
    public StopNode(char nameCharacter) {
        isRoot = false;
        this.nameCharacter = nameCharacter;
        this.stop = null;
    }

    /**
     * Constructor for constructing a leaf node.
     * @param nameCharacter Character in the bus stop name.
     * @param stop Stop to store.
     */
    public StopNode(char nameCharacter, Stop stop) {
        isRoot = false;
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
     * Adds a stop node as a child.
     * @param c Name character for the new node.
     * @param node The node to add.
     * @return Added node or null if add failed because of existing StopNode.
     */
    public StopNode addChild(char c, StopNode node) {
        if(children.putIfAbsent(c, node) == null)
            return node;
        else
            return null;
    }
}
