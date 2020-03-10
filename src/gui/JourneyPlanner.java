package gui;

import common.Connection;
import common.Location;
import common.Stop;
import common.Trip;
import io.JourneyReader;
import io.ParseError;
import search.PrefixMatch;
import search.StopSearcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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

    private static final double ZOOM_SCALE_CHANGE = 0.3;
    private static final double ZOOM_SCROLL_SCALE_CHANGE = 0.1;
    private static final double MIN_SCALE = 0.1;
    private static final double MAX_SCALE = 10000;
    private static final double MOVE_CHANGE = 30;
    private double scale = 20;
    private double originX = 0;
    private double originY = 0;
    Location origin = new Location(originX, originY);
    private double dragStartOriginX = 0;
    private double dragStartOriginY = 0;

    @Override
    protected void redraw(Graphics g) {
        if (stops == null)
            return;

        Set<Stop> selectedTripStops = new HashSet<>();

        // TODO: Draw links between trips and highlight them different colours.
        // TODO: Use assertions.
        //if(selectedStops != null)
        //    selectedStops.stream().flatMap(x -> x.getConnections().stream()).flatMap(x -> x.getTrip().getStops().stream()).collect(Collectors.toSet());
        Dimension drawingAreaSize = getDrawingAreaDimension();
        origin = new Location(originX, originY);

        int size = 5;

        for (Stop stop : stops) {
            if (selectedStops.contains(stop))
                g.setColor(Color.RED);
            else if (selectedTripStops.contains(stop))
                g.setColor(Color.GREEN);
            else
                g.setColor(Color.BLACK);

            Point point = stop.getLocation().asPoint(origin, scale);
            point.translate(drawingAreaSize.width / 2, drawingAreaSize.height / 2);

            g.fillRect(point.x, point.y, size, size);
        }
    }

    @Override
    protected void onClick(MouseEvent e) {
        if(stopSearcher == null) // Don't try to search if no stops exist yet.
            return;

        // Find stop closest to pointer
        Dimension drawingAreaSize = getDrawingAreaDimension();

        Point cursorPoint = e.getPoint();
        cursorPoint.translate(-drawingAreaSize.width / 2, -drawingAreaSize.height / 2);

        Location cursorLocation = Location.newFromPoint(cursorPoint, origin, scale);

        Stop closest = stopSearcher.searchClosest(cursorLocation);
        if(closest == null)
            return;

        selectedStops = new HashSet<>();
        selectedStops.add(closest);

        printStopInfo(closest);
    }

    @Override
    protected void onMouseDragged(double draggedX, double draggedY) {
        originX = dragStartOriginX - draggedX / scale;
        originY = dragStartOriginY + draggedY / scale;
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
        if(stops.size() == 0) {
            getTextOutputArea().setText("No results found.");
            selectedStops.clear();
        } else if(stops.size() == 1) {
            Stop stop = stops.iterator().next().getStop();
            selectedStops = new HashSet<>();
            selectedStops.add(stop);
            printStopInfo(stops.iterator().next().getStop());
        } else {
            String result = stops.stream().map(Objects::toString).collect(Collectors.joining("\n"));
            selectedStops = stops.stream().map(x -> x.getStop()).collect(Collectors.toSet());
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
            stops = JourneyReader.getConnectedStops(stopFile, tripFile);
            stopSearcher = new StopSearcher(stops);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "There was an error reading one of the files: " + ex.getMessage(), "Error Reading File", JOptionPane.ERROR_MESSAGE);
        } catch (ParseError ex) {
            JOptionPane.showMessageDialog(null, "Invalid data encountered while reading one of the files: " + ex.getMessage(), "Error Parsing File", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Prints information about a stop to the GUI window.
     * @param stop Stop to print.
     */
    private void printStopInfo(Stop stop) {
        StringBuilder builder = new StringBuilder();
        for(Trip t : stop.getTrips()) {
            builder.append(t.getId());
            builder.append(", ");
        }

        String tripsString = builder.toString();
        if(tripsString.length() > 2)
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
