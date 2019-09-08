package org.rendell.maps;

import org.rendell.maps.model.Coordinate;
import org.rendell.maps.model.Vector;

import static java.lang.Math.*;

/**
 * Calculations adapted from Javascript on https://www.movable-type.co.uk/scripts/latlong.html
 */
public class BearingCalculator {

    public Vector calculate(Coordinate source, Coordinate target) {

        return new Vector(calculateBearing(source, target), calculateDistance(source, target));
    }

    private double calculateBearing(Coordinate source, Coordinate target) {
        double sourceLatitude = toRadians(source.getLatitude());
        double targetLatitude = toRadians(target.getLatitude());
        double longitudeDifference = toRadians(target.getLongitude() - source.getLongitude());
        double y = sin(longitudeDifference) * cos(targetLatitude);
        double x = cos(sourceLatitude) * sin(targetLatitude) - sin(sourceLatitude) * cos(targetLatitude) * cos(longitudeDifference);

        return (toDegrees(Math.atan2(y, x)) + 360) % 360;
    }

    private long calculateDistance(Coordinate source, Coordinate target) {

        final int earthsRadius = 6371000;

        double sourceLatitude = toRadians(source.getLatitude());
        double targetLatitude = toRadians(target.getLatitude());

        double deltaLatitude = toRadians(target.getLatitude() - source.getLatitude());
        double deltaLongitude = toRadians(target.getLongitude() - source.getLongitude());

        double a =  sin(deltaLatitude / 2) * sin(deltaLatitude / 2) +
                    cos(sourceLatitude) * cos(targetLatitude) *
                    sin(deltaLongitude / 2) * sin(deltaLongitude / 2);

        double c = 2 * atan2(sqrt(a), sqrt(1 - a));

        double d = earthsRadius * c;

        return round(d);
    }

}
