package org.rendell.toposcope.cli;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.rendell.toposcope.dao.LocationsDao;
import org.rendell.toposcope.model.Coordinate;
import org.rendell.toposcope.model.Location;
import org.rendell.toposcope.model.LocationType;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.*;

class ToposcopeCliTest {

    private static final Location SHOOTERS_HILL = new Location(new Coordinate(51.470079, 0.069604), "Shooters Hill", LocationType.HILL, 132);
    private static final Location PRIMROSE_HILL = new Location(new Coordinate(51.539722, -0.160833), "Primrose Hill", LocationType.HILL, 63);

    private final StringWriter out = new StringWriter();
    private final StringWriter err = new StringWriter();

    private Coordinate searchedFrom;
    private Long searchedRadius;

    private int run(List<Location> found, String... args) {
        LocationsDao dao = (nearTo, maxDistanceMetres) -> {
            searchedFrom = nearTo;
            searchedRadius = maxDistanceMetres;
            return found;
        };
        CommandLine commandLine = new CommandLine(new ToposcopeCli(() -> dao));
        commandLine.setOut(new PrintWriter(out));
        commandLine.setErr(new PrintWriter(err));
        return commandLine.execute(args);
    }

    @Test
    void whenGivenOriginShouldSearchDefaultRadiusAndPrintBearingAndDistanceAsJson() throws Exception {

        int exitCode = run(Collections.singletonList(SHOOTERS_HILL), "--origin", "51.507556,-0.100602");

        assertEquals(0, exitCode, err.toString());
        assertEquals(new Coordinate(51.507556, -0.100602), searchedFrom);
        assertEquals(10_000L, searchedRadius);

        JsonNode json = new ObjectMapper().readTree(out.toString());
        assertEquals(51.507556, json.get("origin").get("latitude").asDouble());
        assertEquals(-0.100602, json.get("origin").get("longitude").asDouble());
        assertEquals(10_000, json.get("radiusMetres").asLong());

        JsonNode point = json.get("points").get(0);
        assertEquals("Shooters Hill", point.get("name").asText());
        assertEquals("HILL", point.get("type").asText());
        assertEquals(132, point.get("heightMetres").asLong());
        assertEquals(51.470079, point.get("latitude").asDouble());
        assertEquals(0.069604, point.get("longitude").asDouble());
        assertEquals(109.4, point.get("bearingDegrees").asDouble());
        assertEquals(12, point.get("distanceMetres").asLong() / 1000);
    }

    @Test
    void shouldPassRadiusToSearch() {

        assertEquals(0, run(Collections.emptyList(), "-o", "0,0", "-r", "2500"), err.toString());
        assertEquals(2500L, searchedRadius);
    }

    @Test
    void shouldListPointsNearestFirst() throws Exception {

        assertEquals(0, run(Arrays.asList(SHOOTERS_HILL, PRIMROSE_HILL), "-o", "51.507556,-0.100602", "-r", "20000"), err.toString());

        JsonNode points = new ObjectMapper().readTree(out.toString()).get("points");
        assertEquals("Primrose Hill", points.get(0).get("name").asText());
        assertEquals("Shooters Hill", points.get(1).get("name").asText());
    }

    @Test
    void whenNothingIsInRangeShouldPrintEmptyList() throws Exception {

        assertEquals(0, run(Collections.emptyList(), "-o", "0,0"), err.toString());
        assertEquals(0, new ObjectMapper().readTree(out.toString()).get("points").size());
    }

    @Test
    void shouldAcceptNegativeCoordinates() {

        assertEquals(0, run(Collections.emptyList(), "--origin", "-33.856784,151.215297"), err.toString());
        assertEquals(new Coordinate(-33.856784, 151.215297), searchedFrom);
    }

    @Test
    void whenOriginIsMissingShouldFailWithUsageError() {

        assertEquals(2, run(Collections.emptyList()));
        assertTrue(err.toString().contains("--origin"), err.toString());
        assertEquals("", out.toString());
    }

    @Test
    void whenRadiusIsNotPositiveShouldFail() {

        assertEquals(2, run(Collections.emptyList(), "-o", "0,0", "-r", "0"));
        assertTrue(err.toString().contains("greater than 0"), err.toString());
        assertNull(searchedRadius, "Should not search with an invalid radius");
    }

    @Test
    void whenLatitudeIsOutOfRangeShouldFail() {

        assertEquals(2, run(Collections.emptyList(), "--origin", "91,0"));
        assertTrue(err.toString().contains("between -90 and 90"), err.toString());
    }

    @Test
    void whenLongitudeIsOutOfRangeShouldFail() {

        assertEquals(2, run(Collections.emptyList(), "--origin", "0,181"));
        assertTrue(err.toString().contains("between -180 and 180"), err.toString());
    }

    @Test
    void whenCoordinateIsMalformedShouldFail() {

        assertEquals(2, run(Collections.emptyList(), "--origin", "51.5"));
        assertTrue(err.toString().contains("expected latitude,longitude"), err.toString());
    }

    @Test
    void whenCoordinateIsNotANumberShouldFail() {

        assertEquals(2, run(Collections.emptyList(), "--origin", "abc,0"));
        assertTrue(err.toString().contains("'abc'"), err.toString());
    }

    @Test
    void bearingJustBelow360ShouldRoundToZero() {

        assertEquals(0.0, ToposcopeCli.roundBearing(359.97));
        assertEquals(359.9, ToposcopeCli.roundBearing(359.94));
        assertEquals(109.3, ToposcopeCli.roundBearing(109.26));
    }
}
