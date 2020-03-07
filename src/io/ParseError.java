package io;

/**
 * Unchecked exception for errors while parsing data.
 *
 * @author Matthew Corfiatis
 */
public class ParseError extends Error{
    public ParseError(String message) {
        super(message);
    }
}
