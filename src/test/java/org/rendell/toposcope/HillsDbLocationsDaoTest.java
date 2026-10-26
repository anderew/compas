package org.rendell.toposcope;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.rendell.toposcope.dao.HillsDbLocationsDao;
import org.rendell.toposcope.model.Coordinate;
import org.rendell.toposcope.model.Location;
import org.rendell.toposcope.model.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void shouldFindOnlyLocationsWithinRadiusIncludingThoseFarEastOrWest() {

        HillsDbLocationsDao dao = new HillsDbLocationsDao();
        dao.initialise();

        Coordinate cairngorms = new Coordinate(57.068484, -3.810029);
        long radius = 10000;

        List<Location> locations = dao.findLocations(cairngorms, radius);
        BearingCalculator bearingCalculator = new BearingCalculator();

        assertFalse(locations.isEmpty());
        for (Location location : locations) {
            assertTrue(bearingCalculator.calculate(cairngorms, location.getCoordinate()).getDistanceInMetres() <= radius,
                    location.getLabel() + " is outside the radius");
        }

        // At this latitude 10km east or west is about 0.165 degrees of longitude; a buffer of
        // 10km / 111km = 0.09 degrees would miss these
        assertTrue(locations.stream()
                .anyMatch(location -> Math.abs(location.getCoordinate().getLongitude() - cairngorms.getLongitude()) > 0.1),
                "Expected hills more than 0.1 degrees of longitude away");
    }
}
