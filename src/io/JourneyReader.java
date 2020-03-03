package io;

import common.Location;
import common.Stop;
import common.Trip;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.text.ParseException;
import java.util.*;

/**
 * Reads files containing stops and trips from files on a disk.
 * The data will be parsed, checked for errors.
 *
 * @author Matthew Corfiatis
 */
public class JourneyReader {
    public static final String DEFAULT_STOPS_FILE = "stops.txt";
    public static final String DEFAULT_TRIPS_FILE = "trips.txt";

    /**
     * Reads stops and trips from file, then connects stops using the
     * information from the set of trips.
     *
     * @param stopsFilePath File path of the stops file to load.
     * @param tripsFilePath File path of the trips file to load.
     * @throws IOException When an error occurs finding or reading the specified file.
     * @throws ParseException When the data does not meet the expected format.
     * @return A collection containing all of the stops linked together with trip information.
     */
    public static Collection<Stop> getConnectedStops(String stopsFilePath, String tripsFilePath) throws IOException, ParseException {
        Map<String, Stop> stops = readStops(stopsFilePath);
        Map<Trip, String[]> trips = readTrips(tripsFilePath);

        for (Map.Entry<Trip, String[]> tripEntry : trips.entrySet()) {
            Stop previousStop = null;

            for (String stopId : tripEntry.getValue()) {
                Stop stop = stops.get(stopId);

                // Ensure the stop listed in the trip exists.
                if(stop == null)
                    throw new ParseException("Trip contains stop that was not found.", 0);

                // Add the stop to the trip.
                tripEntry.getKey().addStop(stop);

                // Connects the current stop with the previous stop, skips the first stop.
                if(previousStop != null) {
                    // Making two connections so the trip can go through the stop travelling
                    // in any direction.
                    stop.makeConnection(previousStop, tripEntry.getKey());
                    previousStop.makeConnection(stop, tripEntry.getKey());
                    previousStop.lockConnections();
                }

                previousStop = stop;
            }

            // Prevent further modification.
            if(previousStop != null)
                previousStop.lockConnections();
            tripEntry.getKey().lockStops();
        }

        return stops.values();
    }


    /**
     * Reads stops from a file into a map.
     *
     * @param filePath Path of the file to load/read.
     * @throws IOException When an error occurs finding or reading the specified file.
     * @throws ParseException When the data does not meet the expected format.
     * @return Map with the Stop ID as the key and Stop object as the value.
     */
    private static Map<String, Stop> readStops(String filePath) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        Map<String, Stop> stops = new HashMap<>();

        reader.readLine(); // Skip the file header
        int lineNum = 1;

        String line;
        while((line = reader.readLine()) != null) {
            ++lineNum;
            String[] tokens = line.split("\t"); // Split line into tab separated values.

            // Ensure all tokens are present.
            if(tokens.length < 4)
                throw new ParseException(String.format("Missing data on line %d. Not enough tokens found.", lineNum), lineNum);

            String id = tokens[0];
            String name = tokens[1];
            double latitude;
            double longitude;

            // Ensure latitude and longitude are doubles.
            try {
                latitude = Integer.parseInt(tokens[2]);
            } catch (NumberFormatException ex) {
                throw new ParseException(String.format("Invalid latitude on line %d. Expected double, got %s", lineNum, tokens[2]), lineNum);
            }

            try {
                longitude = Integer.parseInt(tokens[3]);
            } catch (NumberFormatException ex) {
                throw new ParseException(String.format("Invalid longitude on line %d. Expected double, got %s", lineNum, tokens[3]), lineNum);
            }

            // Check duplicate stops, and add the stop to the collection.
            if(stops.put(id, new Stop(id, name, Location.newFromLatLon(latitude, longitude))) != null)
                throw new ParseException(String.format("Duplicate stop found %s", id), lineNum);
        }

        return stops;
    }

    /**
     * Reads trips from a file into a map.
     *
     * @param filePath Path of the file to load/read.
     * @throws IOException When an error occurs finding or reading the specified file.
     * @throws ParseException When the data does not meet the expected format.
     * @return Map of trips where the key is the trip and the value is an array of stops.
     */
    private static Map<Trip, String[]> readTrips(String filePath) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        Map<Trip, String[]> trips = new HashMap<>();

        reader.readLine(); // Skip the file header
        int lineNum = 1;

        String line;
        while((line = reader.readLine()) != null) {
            ++lineNum;
            String[] tokens = line.split("\t"); // Split line into tab separated values.

            // Ensure all tokens are present.
            if(tokens.length < 1)
                throw new ParseException(String.format("Missing data on line %d. Not enough tokens found.", lineNum), lineNum);

            String id = tokens[0];
            String[] stopIDs = Arrays.stream(tokens).skip(1).toArray(String[]::new);

            // Check duplicate trips, and add the trip to the collection.
            if(trips.put(new Trip(id), stopIDs) != null)
                throw new ParseException(String.format("Duplicate trip found %s", id), lineNum);
        }

        return trips;
    }
}
