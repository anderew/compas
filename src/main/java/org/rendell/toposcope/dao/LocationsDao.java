package org.rendell.toposcope.dao;

import org.rendell.toposcope.model.Coordinate;
import org.rendell.toposcope.model.Location;

import java.util.List;

public interface LocationsDao {

    /**
     * Finds locations filtered by distance in degrees lat/long from source
     * @param nearTo
     * @param maxDistanceMetres
     * @return
     */
    List<Location> findLocations(Coordinate nearTo, long maxDistanceMetres);

}
