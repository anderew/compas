package org.rendell.maps.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class Location {

    private final Coordinate coordinate;
    private final String label;
    private final LocationType locationType;
    private final long altitude;
    private final Vector fromSource;

    public Location(Coordinate coordinate, String label,  LocationType locationType, long altitude) {
        this.coordinate = coordinate;
        this.label = label;
        this.locationType = locationType;
        this.altitude = altitude;
        fromSource = null;
    }

    public Location(Location original, final Vector fromSource) {
        coordinate = original.coordinate;
        label = original.label;
        locationType = original.locationType;
        altitude = original.altitude;
        this.fromSource = fromSource;
    }

}
