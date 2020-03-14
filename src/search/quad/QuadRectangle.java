package search.quad;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Extends a Rectangle2D.Double and implements contains to include all edges.
 *
 * @author Matthew Corfiatis
 */
public class QuadRectangle extends Rectangle2D.Double {

    public QuadRectangle(double x, double y, double w, double h) {
        super(x, y, w, h);
    }

    /**
     * Implemented to include points that are exactly on the edge of the rectangle.
     */
    @Override
    public boolean contains(Point2D p) {
        if(super.contains(p))
            return true;

        // Check top and bottom edges
        if(p.getX() >= getMinX() && p.getX() <= getMaxX())
            if(p.getY() == getMinY() || p.getY() == getMaxY())
                return true;

        // Check left and right edges
        if(p.getY() >= getMinY() && p.getY() <= getMaxY())
            if(p.getX() == getMinX() || p.getX() == getMaxX())
                return true;

        return false;
    }
}
