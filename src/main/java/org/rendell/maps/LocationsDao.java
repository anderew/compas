package org.rendell.maps;

import org.rendell.maps.model.Coordinate;
import org.rendell.maps.model.Location;

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
