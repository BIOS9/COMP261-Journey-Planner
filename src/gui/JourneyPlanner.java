package gui;

import common.Location;
import common.Stop;
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
import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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
    private double dragStartOriginX = 0;
    private double dragStartOriginY = 0;

    @Override
    protected void redraw(Graphics g) {
        if(stops == null)
            return;

        Dimension drawingAreaSize = getDrawingAreaDimension();
        Location origin = new Location(originX, originY);

        int size = 5;

        for(Stop stop : stops) {
            if(selectedStops.contains(stop))
                g.setColor(Color.RED);
            else
                g.setColor(Color.BLACK);

            Point point = stop.getLocation().asPoint(origin, scale);
            point.x += drawingAreaSize.width / 2;
            point.y += drawingAreaSize.height / 2;

            g.fillRect(point.x, point.y, size, size);
        }
    }

    @Override
    protected void onClick(MouseEvent e) {

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
        if(stopSearcher == null)
            return;

        String query = getSearchBox().getText();
        Collection<PrefixMatch> stops = stopSearcher.searchPrefix(query);
        String result = stops.stream().map(Objects::toString).collect(Collectors.joining("\n"));
        selectedStops = stops.stream().map(x -> x.getStop()).collect(Collectors.toSet());
        getTextOutputArea().setText(result);
        redraw();
    }

    @Override
    protected void onMouseWheelMove(MouseWheelEvent e) {
        scale -= e.getWheelRotation() * ZOOM_SCROLL_SCALE_CHANGE * scale;

        if(scale > MAX_SCALE)
            scale = MAX_SCALE;
        if(scale < MIN_SCALE)
            scale = MIN_SCALE;
    }

    @Override
    protected void onMove(GUI.Move m) {
        switch(m) {
            case ZOOM_IN:
                scale += ZOOM_SCALE_CHANGE * scale; // Scale multiplication normalizes zooming so the zooming speed remains constant.
                if(scale > MAX_SCALE)
                    scale = MAX_SCALE;
                break;
            case ZOOM_OUT:
                scale -= ZOOM_SCALE_CHANGE * scale;
                if(scale < MIN_SCALE)
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
            System.out.println();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "There was an error reading one of the files: " + ex.getMessage(), "Error Reading File", JOptionPane.ERROR_MESSAGE);
        } catch (ParseError ex) {
            JOptionPane.showMessageDialog(null, "Invalid data encountered while reading one of the files: " + ex.getMessage(), "Error Parsing File", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new JourneyPlanner();
    }
}
