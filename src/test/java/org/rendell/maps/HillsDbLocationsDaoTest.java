package org.rendell.maps;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.rendell.maps.dao.HillsDbLocationsDao;
import org.rendell.maps.model.Coordinate;
import org.rendell.maps.model.Location;
import org.rendell.maps.model.Vector;

import java.util.List;

@Slf4j
class HillsDbLocationsDaoTest {

    @Test
    void whenSearchingForLocationsShouldCallHillsDatabase() {

        HillsDbLocationsDao dao = new HillsDbLocationsDao();

        dao.initialise();

        //Coordinate nearTo = new Coordinate(57.561215, -4.430092);
        //Coordinate nearTo = new Coordinate(56.79685, -5.003508);
        //Coordinate nearTo = new Coordinate(57.696944, -5.048056);
        Coordinate nearTo = new Coordinate(51.5076, -0.0994);
        //Coordinate nearTo = new Coordinate(57.068484, -3.810029);
        //Coordinate nearTo = new Coordinate(51.754222, -0.332428);

        List<Location> locations = dao.findLocations(nearTo, 10000);
        BearingCalculator bearingCalculator = new BearingCalculator();
        for(Location location : locations) {
            //log.info("Location {} and bearing {}", location, bearingCalculator.calculate(nearTo, location.getCoordinate()));
            Vector c = bearingCalculator.calculate(nearTo, location.getCoordinate());
            log.info("drawPoi(\"{}\", {}, scaledDistance(10000.0, {}.0), dc)", location.getLabel(), c.getDegrees(), c.getDistanceInMetres());
        }
        log.info("{} locations found", locations.size());
    }
}