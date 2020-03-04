package gui;

import common.Location;
import common.Stop;
import io.JourneyReader;
import search.StopSearcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This is a gui.GUI class that displays a journey and allows a user to edit it.
 *
 * @author Matthew Corfiatis
 */
public class JourneyPlanner extends GUI {
    private StopSearcher stopSearcher;
    private Collection<Stop> stops;

    private static final double ZOOM_SCALE_CHANGE = 0.3;
    private static final double ZOOM_SCROLL_SCALE_CHANGE = 0.1;
    private static final double MIN_SCALE = 1;
    private static final double MAX_SCALE = 1000;
    private static final double MOVE_CHANGE = 30;
    private double scale = 20;
    private double originX = 0;
    private double originY = 0;

    @Override
    protected void redraw(Graphics g) {
        if(stops == null)
            return;

        g.setColor(Color.BLACK);

        Dimension drawingAreaSize = getDrawingAreaDimension();
        Location origin = new Location(originX, originY);

        int size = 5;

        for(Stop stop : stops) {
            Point point = stop.getLocation().asPoint(origin, scale);
            point.x += drawingAreaSize.width / 2;
            point.y += drawingAreaSize.height / 2;

            g.fillRect(point.x, drawingAreaSize.height - point.y, size, size);
        }
    }

    @Override
    protected void onClick(MouseEvent e) {

    }

    @Override
    protected void onSearch() {
        if(stopSearcher == null)
            return;

        String query = getSearchBox().getText();
        String result = stopSearcher.searchPrefix(query).stream().map(Objects::toString).collect(Collectors.joining("\n"));

        getTextOutputArea().setText(result);
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
                originY -= MOVE_CHANGE / scale;
                break;
            case SOUTH:
                originY += MOVE_CHANGE / scale;
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
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(null, "Invalid data encountered while reading one of the files: " + ex.getMessage(), "Error Parsing File", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new JourneyPlanner();
    }
}
