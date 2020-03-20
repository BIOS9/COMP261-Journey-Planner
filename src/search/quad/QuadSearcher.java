package search.quad;

import common.Location;
import common.Stop;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Searches quad trees to find the nearest point neighbour.
 *
 * @author Matthew Corfiatis
 */
public class QuadSearcher {
    private static final int MAX_POINTS = 2; // Max points per quad
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
     *
     * @param location Location to base the search.
     * @return Closest stop or null if none found.
     */
    public Stop searchClosest(final Point2D location) {
        return searchClosestPriorityQueue(location);
        //return searchClosestElimination(location);
    }

    /**
     * Uses priority queue implementation of closest neighbour search to find
     * closest point to another point.
     * @param location The point to search for the closest neighbour around.
     * @return The closest stop or null if no stops found.
     */
    public Stop searchClosestPriorityQueue(final Point2D location) {
        PriorityQueue<QuadItem> items = new PriorityQueue<QuadItem>(1, (i1, i2) -> {
            double i1Dist = i1.distance(location);
            double i2Dist = i2.distance(location);

            if(i1Dist < i2Dist)
                return -1;
            else if(i1Dist > i2Dist)
                return 1;

            return 0;
        });

        items.add(rootQuad);

        while (!items.isEmpty()) {
            QuadItem item = items.poll();

            if(item instanceof Quad) {
                Quad quad = (Quad)item;
                if(quad.isDivided()) {
                    items.addAll(quad.getQuads());
                } else {
                    items.addAll(quad.getPoints());
                }
            } else if(item instanceof QuadPoint) {
                return ((QuadPoint)item).getStop();
            }
        }

        return null;
    }

    /**
     * Uses priority queue implementation of closest neighbour search to find
     * closest point to another point.
     * @param location The point to search for the closest neighbour around.
     * @return The closest stop or null if no stops found.
     */
    public Stop searchClosestElimination(Point2D location) {
        // Find the smallest quad that contains the location
        Quad quad = rootQuad.findChild(location);

        QuadPoint nearestPoint;
        double nearestPointDistance;

        if (quad == null)
            return null;

        // Find any nearby point to use as initial reference point.
        QuadPoint nearbyPoint = quad.findNearbyPoint(location);
        if (nearbyPoint == null)
            return null;
        nearestPoint = nearbyPoint;
        nearestPointDistance = nearestPoint.getPoint().distance(location);

        // Get all points from quads that are within the range of the distance to the initial reference point.
        Set<QuadPoint> foundPoints = quads.stream().filter(x -> x.hasPoints() && x.distance(location) <= nearestPointDistance)
                .flatMap(x -> x.getPoints().stream())
                .collect(Collectors.toSet());

        // Use regular linear search on the remaining points.
        QuadPoint closest = null;
        double closestDistance = 0;

        for(QuadPoint p : foundPoints) {
            if(closest == null) {
                closest = p;
                closestDistance = closest.getPoint().distance(location);
                continue;
            }

            double distance = p.getPoint().distance(location);
            if(distance < closestDistance) {
                closest = p;
                closestDistance = distance;
            }
        }

        return closest.getStop();
    }

    public Collection<Quad> getQuads() {
        return Collections.unmodifiableCollection(quads);
    }

    /**
     * Builds the quad tree using the given points.
     *
     * @param points Points to generate the tree from.
     */
    private void buildTree(Collection<QuadPoint> points) {
        rootQuad = new Quad(minX, minY, maxX - minX, maxY - minY, MAX_POINTS);
        quads.add(rootQuad);
        for (QuadPoint p : points) {
            if (!rootQuad.addPoint(p))
                throw new Error("Failed to add point to quadtree: " + p.toString());
        }
        quads.addAll(rootQuad.getAllQuads());
    }

    /**
     * Populates the quadPoints field and finds the spatial range of the points.
     *
     * @param stops Stops to generate data from.
     */
    private void generatePoints(Collection<Stop> stops) {
        for (Stop stop : stops) {
            Location l = stop.getLocation();

            // Find range of points.
            if (Double.isNaN(minX) || l.x < minX)
                minX = l.x;
            if (Double.isNaN(minY) || l.y < minY)
                minY = l.y;
            if (Double.isNaN(maxX) || l.x > maxX)
                maxX = l.x;
            if (Double.isNaN(maxY) || l.y > maxY)
                maxY = l.y;

            quadPoints.add(new QuadPoint(new Point2D.Double(l.x, l.y), stop));
        }
    }
}
