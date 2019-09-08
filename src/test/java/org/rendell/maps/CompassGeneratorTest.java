package org.rendell.maps;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rendell.maps.model.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompassGeneratorTest {

    @Mock
    BearingCalculator bearingCalculator;

    @InjectMocks
    CompassGenerator compassGenerator;

    @Test
    void whenPassedALocationShouldCreateACompassDataStruectureShowingBearingAndDistanceFromSource() {


        Coordinate myHouse =  new Coordinate(51.754219,-0.332430);
        Coordinate theAbbey = new Coordinate(51.750484, -0.341889);

        when(bearingCalculator.calculate(myHouse, theAbbey)).thenReturn(new Vector(237, 772));

        Location source = new Location(myHouse,
                "my house",
                LocationType.BUILDING,
                126);

        Location[] targets = {new Location(theAbbey,
                "St Albans Abbey",
                LocationType.BUILDING,
                121)};


        Compass actual = compassGenerator.generate(source, targets);

        assertEquals(source, actual.getSource());
        assertEquals(1, actual.getPointsOnCompass().size());
        Location expectedTargetWithBearing = new Location(targets[0], new Vector(237, 772));
        assertEquals(expectedTargetWithBearing, actual.getPointsOnCompass().get(0));

    }

    // When passed a target location which is the source, omit

    // Handle an empty list
}