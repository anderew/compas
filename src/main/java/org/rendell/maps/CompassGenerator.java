package org.rendell.maps;

import org.rendell.maps.model.Compass;
import org.rendell.maps.model.Coordinate;
import org.rendell.maps.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompassGenerator {

    @Autowired
    private BearingCalculator bearingCalculator;

    public Compass generate(final Location source, final List<Location> targets) {

        List<Location> pointsOnCompass = targets.stream()
                .map(target -> new Location(target, bearingCalculator.calculate(source.getCoordinate(), target.getCoordinate())))
                .collect(Collectors.toList());

        return new Compass(source, pointsOnCompass);


    }


}
