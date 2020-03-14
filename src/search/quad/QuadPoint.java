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

    public Point2D getPoint() {
        return point;
    }

    public Stop getStop() {
        return stop;
    }

    @Override
    public String toString() {
        return "QuadPoint{" +
                "point=" + point +
                ", stop=" + stop +
                '}';
    }
}
