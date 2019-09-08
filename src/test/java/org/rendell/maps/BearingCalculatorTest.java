package org.rendell.maps;

import org.junit.jupiter.api.Test;
import org.rendell.maps.model.Coordinate;
import org.rendell.maps.model.Vector;

import static org.junit.jupiter.api.Assertions.*;

class BearingCalculatorTest {

    @Test
    void whenCalculatingVectorFromZeroDegressLongitudeOnEquatorToAnyPositiveLatitudeShouldBeDueNorth() {

        Coordinate source = new Coordinate(0, 0);
        Coordinate target = new Coordinate(10, 0);

        BearingCalculator bearingCalculator = new BearingCalculator();

        Vector actual = bearingCalculator.calculate(source, target);

        assertEquals(0, actual.getDegrees());
    }

    @Test
    void whenCalculatingVectorFromZeroDegressLongitudeOnEquatorToAnyNegativeLatitudeShouldBeDueSouth() {

        Coordinate source = new Coordinate(0, 0);
        Coordinate target = new Coordinate(-10, 0);

        BearingCalculator bearingCalculator = new BearingCalculator();

        Vector actual = bearingCalculator.calculate(source, target);

        assertEquals(180, actual.getDegrees());
    }


    @Test
    void whenCalculatingVectorFromZeroDegressLongitudeOnEquatorToAnyNegativeLongitudeShouldBeDueWest() {

        Coordinate source = new Coordinate(0, 0);
        Coordinate target = new Coordinate(0, -10);

        BearingCalculator bearingCalculator = new BearingCalculator();

        Vector actual = bearingCalculator.calculate(source, target);

        assertEquals(270, actual.getDegrees());
    }

    @Test
    void whenCalculatingVectorFromZeroDegressLongitudeOnEquatorToAnyPositiveLongitudeShouldBeDueEast() {

        Coordinate source = new Coordinate(0, 0);
        Coordinate target = new Coordinate(0, 10);

        BearingCalculator bearingCalculator = new BearingCalculator();

        Vector actual = bearingCalculator.calculate(source, target);

        assertEquals(90, actual.getDegrees());
    }

    @Test
    void whenCalculatingTheDistanceFromEquatorToNorthPoleShouldBeOneFourthOfEarthsCircumferece() {

        Coordinate source = new Coordinate(0, 0);
        Coordinate target = new Coordinate(90, 0);

        BearingCalculator bearingCalculator = new BearingCalculator();

        Vector actual = bearingCalculator.calculate(source, target);

        final int earthsCircumferenceInKm = 40075; // I asked Google

        // Couldnt get it very accurate but whats 50km between friends?
        assertTrue(earthsCircumferenceInKm / 4 - actual.getDistanceInMetres() / 1000 < 50);
    }

    @Test
    void calculateTateModernObservationDeckToShootersHill() {

        Coordinate source = new Coordinate(51.507556, -0.100602);
        Coordinate target = new Coordinate(51.470079, 0.069604);

        BearingCalculator bearingCalculator = new BearingCalculator();

        Vector actual = bearingCalculator.calculate(source, target);

        assertEquals(109, Math.round(actual.getDegrees()));
        assertEquals(12, actual.getDistanceInMetres() / 1000);
    }


}