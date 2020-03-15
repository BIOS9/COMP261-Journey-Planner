package search.quad;

import common.Stop;

import java.awt.geom.Point2D;
import java.util.Objects;

/**
 * Represents a point in a quad tree and an item associated with that point.
 *
 * @author Matthew Corfiatis
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuadPoint quadPoint = (QuadPoint) o;
        return Objects.equals(point, quadPoint.point) &&
                Objects.equals(stop, quadPoint.stop);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, stop);
    }
}
