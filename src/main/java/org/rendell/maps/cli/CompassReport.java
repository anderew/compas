package org.rendell.maps.cli;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

/**
 * JSON output of the CLI: the origin plus the bearing and distance to each point of interest.
 */
@Data
@JsonPropertyOrder({"origin", "points"})
public class CompassReport {

    private final Origin origin;
    private final List<Point> points;

    @Data
    @JsonPropertyOrder({"latitude", "longitude"})
    public static class Origin {
        private final double latitude;
        private final double longitude;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({"name", "latitude", "longitude", "bearingDegrees", "distanceMetres"})
    public static class Point {
        private final String name;
        private final double latitude;
        private final double longitude;
        private final double bearingDegrees;
        private final long distanceMetres;
    }
}
