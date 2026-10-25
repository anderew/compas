package org.rendell.maps.cli;

import lombok.Data;
import org.rendell.maps.model.Coordinate;
import picocli.CommandLine.ITypeConverter;

/**
 * A point of interest given on the command line as "[name=]latitude,longitude".
 */
@Data
public class PointOfInterest {

    private final String name;
    private final Coordinate coordinate;

    public static PointOfInterest parse(String value) {
        // Use the last '=' so names may themselves contain '='
        int separator = value.lastIndexOf('=');
        if (separator < 0) {
            return new PointOfInterest(null, CoordinateParser.parse(value));
        }
        String name = value.substring(0, separator).trim();
        return new PointOfInterest(name.isEmpty() ? null : name, CoordinateParser.parse(value.substring(separator + 1)));
    }

    public static class Converter implements ITypeConverter<PointOfInterest> {
        @Override
        public PointOfInterest convert(String value) {
            return parse(value);
        }
    }
}
