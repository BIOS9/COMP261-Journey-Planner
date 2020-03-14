package search.quad;

import common.Location;
import common.Stop;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * Searches quad trees to find the nearest point neighbour.
 *
 * @author Matthew Corfiatis
 */
public class QuadSearcher {
    private Quad rootQuad;
    private Set<Quad> quads = new HashSet<>();
    private List<QuadPoint> quadPoints = new ArrayList<>();

    private double
            minX = Double.NaN,
            minY = Double.NaN,
            maxX = Double.NaN,
            maxY = Double.NaN;

    public QuadSearcher(Collection<Stop> stops) {
        generatePoints(stops);
        buildTree(quadPoints);
    }

    /**
     * Finds the closest stop to a lat/long co-ordinate location.
     * @param location Location to base the search.
     * @return Closest stop or null if none found.
     */
    public Stop searchClosest(Point2D location) {
        return null;
    }

    public Collection<Quad> getQuads() {
        return Collections.unmodifiableCollection(quads);
    }

    /**
     * Builds the quad tree using the given points.
     * @param points Points to generate the tree from.
     */
    private void buildTree(Collection<QuadPoint> points) {
        rootQuad = new Quad(minX, minY, maxX - minX, maxY - minY, 4);
        quads.add(rootQuad);
        for(QuadPoint p : points) {
            if(!rootQuad.addPoint(p))
                throw new Error("Failed to add point to quadtree: " + p.toString());
        }
        quads.addAll(rootQuad.getAllQuads());
    }

    /**
     * Populates the quadPoints field and finds the spatial range of the points.
     * @param stops Stops to generate data from.
     */
    private void generatePoints(Collection<Stop> stops) {
        for (Stop stop : stops) {
            Location l = stop.getLocation();

            // Find range of points.
            if(Double.isNaN(minX) || l.x < minX)
                minX = l.x;
            if(Double.isNaN(minY) || l.y < minY)
                minY = l.y;
            if(Double.isNaN(maxX) || l.x > maxX)
                maxX = l.x;
            if(Double.isNaN(maxY) || l.y > maxY)
                maxY = l.y;

            quadPoints.add(new QuadPoint(new Point2D.Double(l.x, l.y), stop));
        }
    }
}
