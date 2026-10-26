package org.rendell.toposcope;

import lombok.extern.slf4j.Slf4j;
import org.rendell.toposcope.dao.LocationsDao;
import org.rendell.toposcope.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Component
public class CompassService {

    @Autowired
    private LocationsDao locationsDao;

    @Autowired
    private CompassGenerator compassGenerator;

    public Compass generateCompass(@NotNull Coordinate nearTo, long maxDistanceMetres) {

        List<Location> locations = locationsDao.findLocations(nearTo, maxDistanceMetres);
        BearingCalculator bearingCalculator = new BearingCalculator();
        for(Location location : locations) {
            log.info("Location {} and bearing {}", location, bearingCalculator.calculate(nearTo, location.getCoordinate()));
            Vector c = bearingCalculator.calculate(nearTo, location.getCoordinate());
            log.info("drawPoi(\"{}\", {}, scaledDistance(10000.0, {}.0), dc)", location.getLabel(), c.getDegrees(), c.getDistanceInMetres());
        }
        log.info("{} locations found", locations.size());

        Location centreOfCompass = new Location(nearTo, "Current Position", LocationType.CURRENT_LOCATION, 0);

        return compassGenerator.generate(centreOfCompass, locations);

    }





}
