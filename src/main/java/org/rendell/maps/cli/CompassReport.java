package org.rendell.maps.cli;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

/**
 * JSON output of the CLI: the origin and search radius, plus the bearing and distance to each point found.
 */
@Data
@JsonPropertyOrder({"origin", "radiusMetres", "points"})
public class CompassReport {

    private final Origin origin;
    private final long radiusMetres;
    private final List<Point> points;

    @Data
    @JsonPropertyOrder({"latitude", "longitude"})
    public static class Origin {
        private final double latitude;
        private final double longitude;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({"name", "type", "heightMetres", "latitude", "longitude", "bearingDegrees", "distanceMetres"})
    public static class Point {
        private final String name;
        private final String type;
        private final long heightMetres;
        private final double latitude;
        private final double longitude;
        private final double bearingDegrees;
        private final long distanceMetres;
    }
}
