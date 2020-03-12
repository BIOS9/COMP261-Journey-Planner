package search.quad;

import common.Stop;

import java.awt.geom.Point2D;

/**
 * Represents a point in a quad tree and an item associated with that point.
 */
public class QuadPoint {
    private final Point2D point;
    private final Stop stop;

    public QuadPoint(Point2D point, Stop stop) {
        this.point = point;
        this.stop = stop;
    }
}
