package gui;

import common.Stop;
import io.JourneyReader;
import search.StopSearcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
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

    @Override
    protected void redraw(Graphics g) {

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
    protected void onMove(GUI.Move m) {

    }

    @Override
    protected void onLoad(File stopFile, File tripFile) {
        try {
            Collection<Stop> stops = JourneyReader.getConnectedStops(stopFile, tripFile);
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
