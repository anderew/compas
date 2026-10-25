package org.rendell.maps.cli;

import org.junit.jupiter.api.Test;
import org.rendell.maps.model.Coordinate;

import static org.junit.jupiter.api.Assertions.*;

class PointOfInterestTest {

    @Test
    void shouldParseNamedPoint() {
        assertEquals(new PointOfInterest("Shooters Hill", new Coordinate(51.470079, 0.069604)),
                PointOfInterest.parse("Shooters Hill=51.470079,0.069604"));
    }

    @Test
    void shouldParseUnnamedPoint() {
        assertEquals(new PointOfInterest(null, new Coordinate(-10.5, 20)),
                PointOfInterest.parse("-10.5, 20"));
    }

    @Test
    void nameMayContainEquals() {
        assertEquals("a=b", PointOfInterest.parse("a=b=1,2").getName());
    }

    @Test
    void blankNameIsTreatedAsUnnamed() {
        assertNull(PointOfInterest.parse(" =1,2").getName());
    }
}
