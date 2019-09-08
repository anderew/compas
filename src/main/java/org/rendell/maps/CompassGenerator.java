package org.rendell.maps;

import org.rendell.maps.model.Compass;
import org.rendell.maps.model.Coordinate;
import org.rendell.maps.model.Location;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CompassGenerator {

    private BearingCalculator bearingCalculator;

    public Compass generate(final Location source, final Location... targets) {

        List<Location> pointsOnCompass = Arrays.stream(targets)
                .map(target -> new Location(target, bearingCalculator.calculate(source.getCoordinate(), target.getCoordinate())))
                .collect(Collectors.toList());

        return new Compass(source, pointsOnCompass);


    }


}
