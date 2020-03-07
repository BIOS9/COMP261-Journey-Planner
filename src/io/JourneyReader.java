package io;

import common.Location;
import common.Stop;
import common.Trip;

import java.io.*;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * @param stopsFile Stops file to load.
     * @param tripsFile Trips file to load.
     * @return A collection containing all of the stops linked together with trip information.
     * @throws IOException    When an error occurs finding or reading the specified file.
     * @throws ParseException When the data does not meet the expected format.
     */
    public static Collection<Stop> getConnectedStops(File stopsFile, File tripsFile) throws IOException, ParseError {
        Map<String, Stop> stops = readStops(stopsFile);
        Map<Trip, String[]> trips = readTrips(tripsFile);

        for (Map.Entry<Trip, String[]> tripEntry : trips.entrySet()) {
            Stop previousStop = null;

            for (String stopId : tripEntry.getValue()) {
                Stop stop = stops.get(stopId);

                // Ensure the stop listed in the trip exists.
                if (stop == null)
                    throw new ParseError("Trip contains stop that was not found.");

                // Add the stop to the trip.
                tripEntry.getKey().addStop(stop);

                // Connects the current stop with the previous stop, skips the first stop.
                if (previousStop != null) {
                    // Making two connections so the trip can go through the stop travelling
                    // in any direction.
                    stop.makeConnection(previousStop, tripEntry.getKey());
                    previousStop.makeConnection(stop, tripEntry.getKey());
                }

                previousStop = stop;
            }

            tripEntry.getKey().lockStops();
        }

        // Prevent further modification of the stops.
        for (Stop stop : stops.values()) {
            stop.lockConnections();
        }

        return stops.values();
    }


    /**
     * Reads stops from a file into a map.
     *
     * @param file File to read.
     * @return Map with the Stop ID as the key and Stop object as the value.
     * @throws IOException    When an error occurs finding or reading the specified file.
     * @throws ParseException When the data does not meet the expected format.
     */
    private static Map<String, Stop> readStops(File file) throws IOException, ParseError {
        Stream<String> stream = Files.lines(file.toPath());

        AtomicInteger lineNumber = new AtomicInteger(2);
        return stream.skip(1).map(lines -> {
            String[] tokens = lines.split("\t"); // Split line into tab separated values.

            int lineNum = lineNumber.getAndIncrement(); // Not safe for parallel

            if (tokens.length != 4)
                throw new ParseError(String.format("Invalid data on line %d of stops file. Invalid number of tokens found.", lineNum));

            String id = tokens[0];
            String name = tokens[1];
            double latitude;
            double longitude;

            // Try parse latitude.
            try {
                latitude = Double.parseDouble(tokens[2]);
            } catch (NumberFormatException ex) {
                throw new ParseError(String.format("Invalid latitude on line %d of stops file. Expected double, got %s", lineNum, tokens[2]));
            }

            // Try parse longitude.
            try {
                longitude = Double.parseDouble(tokens[3]);
            } catch (NumberFormatException ex) {
                throw new ParseError(String.format("Invalid longitude on line %d of stops file. Expected double, got %s", lineNum, tokens[3]));
            }

            return new Stop(id, name, Location.newFromLatLon(latitude, longitude));
        }).collect(Collectors.toMap(Stop::getId, stop -> stop, (stop1, stop2) -> {
            throw new ParseError(String.format("Duplicate stop found: %s", stop1.getId())); // Merge function called when multiple element have same key.
        }));
    }

    /**
     * Reads trips from a file into a map.
     *
     * @param file File to read.
     * @return Map of trips where the key is the trip and the value is an array of stops.
     * @throws IOException    When an error occurs finding or reading the specified file.
     * @throws ParseException When the data does not meet the expected format.
     */
    private static Map<Trip, String[]> readTrips(File file) throws IOException, ParseError {
        Stream<String> stream = Files.lines(file.toPath());

        AtomicInteger lineNumber = new AtomicInteger(2);
        return stream.skip(1).map(lines -> {
            String[] tokens = lines.split("\t"); // Split line into tab separated values.

            int lineNum = lineNumber.getAndIncrement(); // Not safe for parallel.

            if (tokens.length < 2)
                throw new ParseError(String.format("Invalid data on line %d of trips file. No stops found.", lineNum));

            String id = tokens[0];
            String[] stopIDs = Arrays.stream(tokens).skip(1).toArray(String[]::new); // Skip first element to get bus stops.

            return new UnlinkedTrip(new Trip(id), stopIDs);
        }).collect(Collectors.toMap(UnlinkedTrip::getTrip, UnlinkedTrip::getStopIDs, (trip1, trip2) -> {
            throw new ParseError(String.format("Duplicate trip found.")); // Merge function called when multiple element have same key.
        }));
    }
}
