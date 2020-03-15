package gui;

import common.Location;
import common.Stop;
import common.Trip;
import io.JourneyReader;
import io.ParseError;
import search.StopSearcher;
import search.quad.Quad;
import search.trie.PrefixMatch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is a gui.GUI class that displays a journey and allows a user to edit it.
 *
 * @author Matthew Corfiatis
 */
public class JourneyPlanner extends GUI {
    private StopSearcher stopSearcher;
    private Collection<Stop> stops;
    private Set<Stop> selectedStops = new HashSet<>();
    private Set<Trip> selectedTrips = new HashSet<>();

    private static final double ZOOM_SCALE_CHANGE = 0.3;
    private static final double ZOOM_SCROLL_SCALE_CHANGE = 0.1;
    private static final double MIN_SCALE = 0.1;
    private static final double MAX_SCALE = 10000;
    private static final double MOVE_CHANGE = 30;
    private static final float STOP_SIZE = 0.10f;
    private static final float OUTLINE_SIZE = 0.004f;
    private static final int MIN_STOP_SIZE = 3;
    private static final boolean DISPLAY_QUADS = true;
    private double scale = 10;
    private double originX = 0, originY = 0;
    private double cursorX = 0, cursorY = 0;
    Location origin = new Location(originX, originY);
    private double dragStartOriginX = 0;
    private double dragStartOriginY = 0;

    @Override
    protected void redraw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        Dimension drawingAreaSize = getDrawingAreaDimension();
        g2d.setColor(Color.decode("#212121"));
        g2d.fillRect(0, 0, drawingAreaSize.width, drawingAreaSize.height);

        if (stops == null)
            return;

        if (stopSearcher != null && DISPLAY_QUADS) {
            for (Quad q : stopSearcher.getQuads()) {
                Rectangle2D rect = q.getScreenBounds(new Point2D.Double(originX, originY), scale,
                        new Point2D.Double(getDrawingAreaDimension().getWidth() / 2, getDrawingAreaDimension().getHeight() / 2));

                g2d.setColor(Color.gray);
                if (q.ignored)
                    g2d.fillRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());

                g2d.setColor(Color.white);
                g2d.drawRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
            }
        }
        // TODO: Use assertions.

        origin = new Location(originX, originY);

        int size = (int) (scale * STOP_SIZE);
        if (size < MIN_STOP_SIZE)
            size = MIN_STOP_SIZE;

        Map<Stop, Point> generatedStopPoints = new HashMap<>(); // Map of stop locations converted to points
        for (Stop stop : stops) {
            Point point = stop.getLocation().asPoint(origin, scale);
            point.translate(drawingAreaSize.width / 2, drawingAreaSize.height / 2);

            generatedStopPoints.put(stop, point);
        }

        Set<Stop> selectedTripStops = new HashSet<>();

        float tripWidth = 2;
        float tripHue = 0;
        float hueStep = 1.0f / selectedTrips.size();

        for (Trip trip : selectedTrips) {
            Stop previousTripStop = null;

            g2d.setColor(Color.getHSBColor(tripHue, 1, 1));
            tripHue += hueStep;
            for (Stop stop : trip.getStops()) {
                if (previousTripStop == null) {
                    previousTripStop = stop;
                    continue;
                }

                Point point = generatedStopPoints.get(stop);
                Point previousPoint = generatedStopPoints.get(previousTripStop);

                // Make dashed stroke
                Stroke dashed = new BasicStroke(tripWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{0.1f * (float) scale, 0.1f * (float) scale * tripHue}, 0);
                g2d.setStroke(dashed);
                g2d.drawLine(point.x, point.y, previousPoint.x, previousPoint.y);

                previousTripStop = stop;
            }

            selectedTripStops.addAll(trip.getStops());
        }

        // TODO: make draw method inside stop and trip?
        for (Stop stop : stops) {
            if (selectedStops.contains(stop))
                g2d.setColor(Color.RED);
            else if (selectedTripStops.contains(stop))
                g2d.setColor(Color.GREEN);
            else
                g2d.setColor(Color.white);

            Point point = generatedStopPoints.get(stop);

            g2d.fillOval(point.x - (size / 2), point.y - (size / 2), size, size);

            float penSize = (float) scale * OUTLINE_SIZE;
            g2d.setStroke(new BasicStroke(penSize));
            g2d.setColor(Color.black);
            g2d.drawOval(point.x - (size / 2), point.y - (size / 2), size, size);
        }
    }

    @Override
    protected void onClick(MouseEvent e) {
        if (stopSearcher == null) // Don't try to search if no stops exist yet.
            return;

        // Find stop closest to pointer
        Dimension drawingAreaSize = getDrawingAreaDimension();

        Point cursorPoint = e.getPoint();
        cursorPoint.translate(-drawingAreaSize.width / 2, -drawingAreaSize.height / 2);

        Location cursorLocation = Location.newFromPoint(cursorPoint, origin, scale);

        Stop closest = stopSearcher.searchClosest(cursorLocation);
        if (closest == null)
            return;

        // Select stop.
        selectedStops = new HashSet<>();
        selectedStops.add(closest);

        // Select trips going through stop.
        selectedTrips = new HashSet<>();
        selectedTrips.addAll(closest.getTrips());

        printStopInfo(closest);
    }

    @Override
    protected void onMouseDragged(double draggedX, double draggedY) {
        originX = dragStartOriginX - draggedX / scale;
        originY = dragStartOriginY + draggedY / scale;
    }

    @Override
    protected void onMouseMoved(double x, double y) {
        cursorX = x;
        cursorY = y;
    }

    @Override
    protected void onMouseDragStart() {
        dragStartOriginX = originX;
        dragStartOriginY = originY;
    }

    @Override
    protected void onMouseDragStop() {

    }

    @Override
    protected void onSearch() {
        if (stopSearcher == null)
            return;

        String query = getSearchBox().getText();
        Collection<PrefixMatch> stops = stopSearcher.searchPrefix(query);
        if (stops.size() == 0) { // If no results
            getTextOutputArea().setText("No results found.");
            selectedStops = new HashSet<>();
            selectedTrips = new HashSet<>();
        } else if (stops.size() == 1) { // If single result, display info
            Stop stop = stops.iterator().next().getStop();

            // Select stop.
            selectedStops = new HashSet<>();
            selectedStops.add(stop);

            // Select trips going through stop.
            selectedTrips = new HashSet<>();
            selectedTrips.addAll(stop.getTrips());

            printStopInfo(stops.iterator().next().getStop());
        } else { // If multiple stops highlight all
            String result = stops.stream().map(Objects::toString).collect(Collectors.joining("\n"));

            // Select stops.
            selectedStops = stops.stream().map(x -> x.getStop()).collect(Collectors.toSet());

            // Select trips.
            selectedTrips = selectedStops.stream().flatMap(x -> x.getTrips().stream()).collect(Collectors.toSet());

            getTextOutputArea().setText(result);
        }
    }

    @Override
    protected void onMouseWheelMove(MouseWheelEvent e) {
        scale -= e.getWheelRotation() * ZOOM_SCROLL_SCALE_CHANGE * scale;

        if (scale > MAX_SCALE)
            scale = MAX_SCALE;
        if (scale < MIN_SCALE)
            scale = MIN_SCALE;
    }

    @Override
    protected void onMove(GUI.Move m) {
        switch (m) {
            case ZOOM_IN:
                scale += ZOOM_SCALE_CHANGE * scale; // Scale multiplication normalizes zooming so the zooming speed remains constant.
                if (scale > MAX_SCALE)
                    scale = MAX_SCALE;
                break;
            case ZOOM_OUT:
                scale -= ZOOM_SCALE_CHANGE * scale;
                if (scale < MIN_SCALE)
                    scale = MIN_SCALE;
                break;

            case NORTH:
                originY += MOVE_CHANGE / scale;
                break;
            case SOUTH:
                originY -= MOVE_CHANGE / scale;
                break;
            case EAST:
                originX += MOVE_CHANGE / scale;
                break;
            case WEST:
                originX -= MOVE_CHANGE / scale;
                break;
        }
    }

    @Override
    protected void onLoad(File stopFile, File tripFile) {
        try {
            selectedStops = new HashSet<>();
            selectedTrips = new HashSet<>();

            stops = JourneyReader.getConnectedStops(stopFile, tripFile);
            stopSearcher = new StopSearcher(stops);

            getTextOutputArea().setText(String.format(
                    "Loaded:%n" +
                            "Stops: %d%n" +
                            "Trips: %d%n" +
                            "Connections: %d",
                    JourneyReader.getStopCount(),
                    JourneyReader.getTripCount(),
                    JourneyReader.getConnectionCount()
            ));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "There was an error reading one of the files: " + ex.getMessage(), "Error Reading File", JOptionPane.ERROR_MESSAGE);
        } catch (ParseError ex) {
            JOptionPane.showMessageDialog(null, "Invalid data encountered while reading one of the files: " + ex.getMessage(), "Error Parsing File", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Prints information about a stop to the GUI window.
     *
     * @param stop Stop to print.
     */
    private void printStopInfo(Stop stop) {
        StringBuilder builder = new StringBuilder();
        for (Trip t : stop.getTrips()) {
            builder.append(t.getId());
            builder.append(", ");
        }

        String tripsString = builder.toString();
        if (tripsString.length() > 2)
            tripsString = tripsString.substring(0, tripsString.length() - 2);

        getTextOutputArea().setText(String.format(
                "Stop ID: %s%n" +
                        "Stop Name: %s%n" +
                        "Trips: %s",
                stop.getId(),
                stop.getName(),
                tripsString
        ));
    }

    public static void main(String[] args) {
        new JourneyPlanner();
    }
}
