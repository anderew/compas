package org.rendell.maps.dao;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.rendell.maps.model.Coordinate;
import org.rendell.maps.model.Location;
import org.rendell.maps.model.LocationType;

public class LocationBuilderFromGis {

    private Coordinate coordinate;
    private String name;
    private int height;

    public LocationBuilderFromGis setGeometry(Geometry geometry) {
        Point point = geometry.getInteriorPoint();
        coordinate = new Coordinate(point.getY(), point.getX());
        return this;
    }

    public LocationBuilderFromGis setName(String name) {
        this.name = name;
        return this;
    }

    public LocationBuilderFromGis setHeight(int height) {
        this.height = height;
        return this;
    }

    public Location build() {
        return new Location(coordinate, name, LocationType.MUNRO, height);
    }
}
