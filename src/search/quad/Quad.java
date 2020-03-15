package search.quad;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.stream.Stream;

/**
 * Represents a shape with 4 sides that can contain other quads or points.
 * 784
 * @author Matthew Corfiatis
 */
public class Quad {
    public final Rectangle2D bounds;
    public final int maxPoints;
    public final Quad parent;
    private Quad
            northEast,
            southEast,
            southWest,
            northWest;
    private boolean divided = false;
    private final Set<QuadPoint> points = new HashSet<>();

    public boolean ignored = false;

    public Quad(Rectangle2D bounds, int maxPoints) {
        this.bounds = bounds;
        this.parent = null;
        this.maxPoints = maxPoints;
    }

    public Quad(Rectangle2D bounds, Quad parent, int maxPoints) {
        this.bounds = bounds;
        this.parent = parent;
        this.maxPoints = maxPoints;
    }

    public Quad(double x, double y, double width, double height, Quad parent, int maxPoints) {
        this.bounds = new QuadRectangle(x, y, width, height);
        this.parent = parent;
        this.maxPoints = maxPoints;
    }

    public Quad(double x, double y, double width, double height, int maxPoints) {
        this.bounds = new QuadRectangle(x, y, width, height);
        this.parent = null;
        this.maxPoints = maxPoints;
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

    public Quad findChild(Point2D point) {
        if(isDivided()) {
            Optional<Quad> quad = streamQuads().map(x -> x.findChild(point)).filter(x -> x != null).findAny();
            if(quad.isPresent())
                return quad.get();
        } else if(bounds.contains(point))
            return this;

        return null;
    }

    /**
     * Finds any nearby point that can be used for first approximation of closest point.
     * @param exclude Point to exclude from the search
     * @return A QuadPoint object.
     */
    public QuadPoint findNearbyPoint(Point2D exclude) {
        return findNearbyPoint(exclude, new HashSet<>());
    }

    private QuadPoint findNearbyPoint(Point2D exclude, Set<Quad> visited) {
        if(visited.contains(this))
            return null;

        visited.add(this);

        if(hasPoints()) {
            Optional<QuadPoint> point = streamPoints().filter(x -> !x.getPoint().equals(exclude)).findAny();
            if(point.isPresent())
                return point.get();
        }

        for(Quad q : getQuads()) {
            QuadPoint nearby = q.findNearbyPoint(exclude, visited);
            if(nearby != null)
                return nearby;
        }

        if(parent != null) {
            return parent.findNearbyPoint(exclude, visited);
        }

        return null;
    }

    /**
     * Inserts a quad point into the quad tree.
     * @param point
     */
    public boolean addPoint(QuadPoint point) {
        if(!bounds.contains(point.getPoint()))
            return false;

        if(isDivided()) { // If this quad has already divided, the children can be put inside
            if(northEast.addPoint(point))
                return true;
            if(southEast.addPoint(point))
                return true;
            if(southWest.addPoint(point))
                return true;
            if(northWest.addPoint(point))
                return true;
            return false;
        } else if(points.size() == maxPoints) { // If divide is required
            points.add(point);
            return divide();
        }

        return points.add(point);
    }

    private boolean divide() {
        if(divided)
            return false;

        double x = bounds.getX();
        double y = bounds.getY();
        double w = bounds.getWidth();
        double h = bounds.getHeight();

        northEast = new Quad(x + w/2, y, w/2, h/2, this, maxPoints);
        southEast = new Quad(x + w/2, y + h/2, w/2, h/2, this, maxPoints);
        southWest = new Quad(x, y + h/2, w/2, h/2, this, maxPoints);
        northWest = new Quad(x, y, w/2, h/2, this, maxPoints);

        divided = true;

        for(QuadPoint p : points) {
            if(!addPoint(p))
                return false;
        }
        points.clear();

        return true;
    }

    public boolean isDivided() {
        return divided;
    }

    public boolean hasPoints() {
        return !points.isEmpty();
    }

    public Set<Quad> getQuads() {
        if(!isDivided())
            return new HashSet<>();

        Set<Quad> set = new HashSet<>();

        set.add(northEast);
        set.add(southEast);
        set.add(southWest);
        set.add(northWest);

        return set;
    }

    public Set<QuadPoint> getPoints() {
        return Collections.unmodifiableSet(points);
    }

    public Stream<QuadPoint> streamPoints() {
        return getPoints().stream();
    }

    public Stream<Quad> streamQuads() {
        return getQuads().stream();
    }

    /**
     * Returns all quads for this node and all child nodes.
     */
    public Set<Quad> getAllQuads() {
        Set<Quad> set = new HashSet<>();
        set.addAll(getQuads());

        if(!isDivided())
            return set;

        set.addAll(northEast.getAllQuads());
        set.addAll(southEast.getAllQuads());
        set.addAll(southWest.getAllQuads());
        set.addAll(northWest.getAllQuads());

        return set;
    }

    public Rectangle2D getScreenBounds(Point2D origin, double scale, Point2D translate) {
        double x = (bounds.getX() - origin.getX()) * scale;
        double y = (origin.getY() - bounds.getMaxY()) * scale;
        double width = bounds.getWidth() * scale;
        double height = bounds.getHeight() * scale;

        return new QuadRectangle(x + translate.getX(), y + translate.getY(), width, height);
    }

    /**
     * Finds the closest point on/in the quad to a given point.
     * @param point Point to find the closest point to.
     * @return Closest point on/in the quad.
     */
    public Point2D closestPointOnQuad(Point2D point) {
        // Find which region point is in
        // There are 8 regions around the quad.
        // 7 \    0    /  1
        // ___\_______/____
        // 6   |     |    2
        // ____|_____|_____
        //    /       \
        // 5 /    4    \  3
        // Closest point in the corner regions, is the corner of quad in that region.
        // Closest point in any other external region is a straight line which intersects the nearest edge at a right angle.

        if(point.getX() <= bounds.getMinX()) { // Point is left of rect
            if(point.getY() <= bounds.getMinY()) { // Point is in top-left region (7)
                return new Point2D.Double(bounds.getMinX(), bounds.getMinY()); // Return top-left corner
            } else if(point.getY() >= bounds.getMaxY()) { // Point is in bottom-left region (5)
                return new Point2D.Double(bounds.getMinX(), bounds.getMaxY()); // Return bottom-left corner
            } else { // Point is in middle-left region (6)
                return new Point2D.Double(bounds.getMinX(), point.getY()); // Return point on left edge
            }
        } else if(point.getX() >= bounds.getMaxX()) { // Point is right of rect
            if(point.getY() <= bounds.getMinY()) { // Point is in top-right region (1)
                return new Point2D.Double(bounds.getMaxX(), bounds.getMinY()); // Return top-right corner
            } else if(point.getY() >= bounds.getMaxY()) { // Point is in bottom-right region (3)
                return new Point2D.Double(bounds.getMaxX(), bounds.getMaxY()); // Return bottom-right corner
            } else { // Point is in middle-right region (2)
                return new Point2D.Double(bounds.getMaxX(), point.getY()); // Return point on right edge
            }
        } else { // Point is above or below rect
            if(point.getY() <= bounds.getMinY()) { // Point is in top-middle region (0)
                return new Point2D.Double(point.getX(), bounds.getMinY()); // Return point on top edge
            } else if(point.getY() >= bounds.getMaxY()) { // Point is in bottom-middle region (4)
                return new Point2D.Double(point.getX(), bounds.getMaxY()); // Return point on bottom edge
            } else { // Point is the quad
                // If the point is inside the rect, then return it as closest.
                return point;
            }
        }
    }

    /**
     * Finds the distance from a given point to the closest point on/in the quad.
     * @param point Point to find the distance from.
     * @return A distance double.
     */
    public double distanceToQuad(Point2D point) {
        return point.distance(closestPointOnQuad(point));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quad quad = (Quad) o;
        return Objects.equals(bounds, quad.bounds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bounds);
    }
}
