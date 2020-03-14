package quad;

import org.junit.jupiter.api.Test;
import search.quad.Quad;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class quadTests {

    @Test
    public void testCreateQuad() {
        Quad quad = new Quad(100, 150, 200, 250, 1);
        Rectangle2D bounds = quad.getBounds();

        assertEquals(100, bounds.getMinX());
        assertEquals(150, bounds.getMinY());
        assertEquals(200, bounds.getWidth());
        assertEquals(250, bounds.getHeight());
        assertEquals(300, bounds.getMaxX());
        assertEquals(400, bounds.getMaxY());
    }

    @Test
    public void testClosestPointExternal() {
        Quad quad = new Quad(100, 150, 200, 250, 1);
        Rectangle2D bounds = quad.getBounds();

        // Corners

        // top-left
        assertEquals(new Point2D.Double(bounds.getMinX(), bounds.getMinY()),
                quad.closestPointOnQuad(new Point2D.Double(50, 25)));

        // top-right
        assertEquals(new Point2D.Double(bounds.getMaxX(), bounds.getMinY()),
                quad.closestPointOnQuad(new Point2D.Double(450, 25)));

        // bottom-right
        assertEquals(new Point2D.Double(bounds.getMaxX(), bounds.getMaxY()),
                quad.closestPointOnQuad(new Point2D.Double(500, 500)));

        // bottom-left
        assertEquals(new Point2D.Double(bounds.getMinX(), bounds.getMaxY()),
                quad.closestPointOnQuad(new Point2D.Double(50, 500)));


        // Sides

        // top
        assertEquals(new Point2D.Double(125, bounds.getMinY()),
                quad.closestPointOnQuad(new Point2D.Double(125, 25)));

        // right
        assertEquals(new Point2D.Double(bounds.getMaxX(), 175),
                quad.closestPointOnQuad(new Point2D.Double(450, 175)));

        // bottom
        assertEquals(new Point2D.Double(150, bounds.getMaxY()),
                quad.closestPointOnQuad(new Point2D.Double(150, 500)));

        // left
        assertEquals(new Point2D.Double(bounds.getMinX(), 200),
                quad.closestPointOnQuad(new Point2D.Double(50, 200)));
    }

    @Test
    public void testClosestPointInteral() {
        Quad quad = new Quad(100, 150, 200, 250, 1);
        Rectangle2D bounds = quad.getBounds();

        Point2D testPoint = new Point2D.Double(173, 300);

        assertEquals(testPoint, quad.closestPointOnQuad(testPoint));
    }

    @Test
    public void testClosestPointOnEdge() {
        Quad quad = new Quad(100, 150, 200, 250, 1);
        Rectangle2D bounds = quad.getBounds();

        // Left edge
        assertEquals(new Point2D.Double(bounds.getMinX(), bounds.getMinY() + 20),
                quad.closestPointOnQuad(new Point2D.Double(bounds.getMinX(), bounds.getMinY() + 20)));

        // Right edge
        assertEquals(new Point2D.Double(bounds.getMaxX(), bounds.getMinY() + 20),
                quad.closestPointOnQuad(new Point2D.Double(bounds.getMaxX(), bounds.getMinY() + 20)));

        // Top edge
        assertEquals(new Point2D.Double(bounds.getMinX() + 20, bounds.getMinY()),
                quad.closestPointOnQuad(new Point2D.Double(bounds.getMinX() + 20, bounds.getMinY())));

        // Bottom edge
        assertEquals(new Point2D.Double(bounds.getMinX() + 20, bounds.getMaxY()),
                quad.closestPointOnQuad(new Point2D.Double(bounds.getMinX() + 20, bounds.getMaxY())));
    }

    @Test
    public void testClosestPointOnCorner() {
        Quad quad = new Quad(100, 150, 200, 250, 1);
        Rectangle2D bounds = quad.getBounds();

        // top-left
        assertEquals(new Point2D.Double(bounds.getMinX(), bounds.getMinY()),
                quad.closestPointOnQuad(new Point2D.Double(bounds.getMinX(), bounds.getMinY())));

        // top-right
        assertEquals(new Point2D.Double(bounds.getMaxX(), bounds.getMinY()),
                quad.closestPointOnQuad(new Point2D.Double(bounds.getMaxX(), bounds.getMinY())));

        // bottom-right
        assertEquals(new Point2D.Double(bounds.getMaxX(), bounds.getMaxY()),
                quad.closestPointOnQuad(new Point2D.Double(bounds.getMaxX(), bounds.getMaxY())));

        // bottom-left
        assertEquals(new Point2D.Double(bounds.getMinX(), bounds.getMaxY()),
                quad.closestPointOnQuad(new Point2D.Double(bounds.getMinX(), bounds.getMaxY())));
    }

    @Test
    public void testDistanceToQuad() {
        Quad quad = new Quad(100, 150, 200, 250, 1);

        assertEquals(50, quad.distanceToQuad(new Point2D.Double(50, 226)));
    }
}
