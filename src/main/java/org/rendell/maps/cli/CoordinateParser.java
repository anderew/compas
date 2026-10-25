package org.rendell.maps.cli;

import org.rendell.maps.model.Coordinate;
import picocli.CommandLine.TypeConversionException;

/**
 * Parses "latitude,longitude" in decimal degrees, e.g. "51.507556,-0.100602".
 */
public class CoordinateParser {

    public static Coordinate parse(String value) {
        String[] parts = value.split(",");
        if (parts.length != 2) {
            throw new TypeConversionException("'" + value + "' is not a coordinate, expected latitude,longitude");
        }

        double latitude = parseNumber(parts[0], value);
        double longitude = parseNumber(parts[1], value);

        if (latitude < -90 || latitude > 90) {
            throw new TypeConversionException("Latitude " + latitude + " in '" + value + "' must be between -90 and 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new TypeConversionException("Longitude " + longitude + " in '" + value + "' must be between -180 and 180");
        }

        return new Coordinate(latitude, longitude);
    }

    private static double parseNumber(String number, String value) {
        try {
            double parsed = Double.parseDouble(number.trim());
            if (Double.isNaN(parsed) || Double.isInfinite(parsed)) {
                throw new NumberFormatException();
            }
            return parsed;
        } catch (NumberFormatException ex) {
            throw new TypeConversionException("'" + number.trim() + "' in '" + value + "' is not a number");
        }
    }
}
