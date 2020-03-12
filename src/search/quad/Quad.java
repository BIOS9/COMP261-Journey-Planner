package search.quad;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a shape with 4 sides that can contain other quads or points.
 * @author Matthew Corfiatis
 */
public class Quad {
    public final Rectangle2D bounds;
    private final List<Quad> children = new ArrayList<>();
    private final Set<Point2D> points = new HashSet<>();

    public Quad(Rectangle2D bounds) {
        this.bounds = bounds;
    }

    public Quad(double x, double y, double width, double height) {
        this.bounds = new Rectangle2D.Double(x, y, width, height);
    }

    public Rectangle2D getBounds() {
        return bounds;
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
}
