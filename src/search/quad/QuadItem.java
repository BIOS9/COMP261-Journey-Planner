package search.quad;

import java.awt.geom.Point2D;

/**
 * An item stored in the quad tree.
 * Can be a quad or a point.
 *
 * @author Matthew Corfiatis
 */
public interface QuadItem {
    /**
     * Returns the distance the item from a given point.
     */
    double distance(Point2D point);
}
