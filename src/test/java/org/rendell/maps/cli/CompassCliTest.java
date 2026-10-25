package org.rendell.maps.cli;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class CompassCliTest {

    private final StringWriter out = new StringWriter();
    private final StringWriter err = new StringWriter();

    private int run(String... args) {
        CommandLine commandLine = new CommandLine(new CompassCli());
        commandLine.setOut(new PrintWriter(out));
        commandLine.setErr(new PrintWriter(err));
        return commandLine.execute(args);
    }

    @Test
    void whenGivenOriginAndNamedPoiShouldPrintBearingAndDistanceAsJson() throws Exception {

        int exitCode = run("--origin", "51.507556,-0.100602", "--poi", "Shooters Hill=51.470079,0.069604");

        assertEquals(0, exitCode, err.toString());

        JsonNode json = new ObjectMapper().readTree(out.toString());
        assertEquals(51.507556, json.get("origin").get("latitude").asDouble());
        assertEquals(-0.100602, json.get("origin").get("longitude").asDouble());

        JsonNode point = json.get("points").get(0);
        assertEquals("Shooters Hill", point.get("name").asText());
        assertEquals(51.470079, point.get("latitude").asDouble());
        assertEquals(0.069604, point.get("longitude").asDouble());
        assertEquals(109, Math.round(point.get("bearingDegrees").asDouble()));
        assertEquals(12, point.get("distanceMetres").asLong() / 1000);
    }

    @Test
    void whenGivenSeveralPoisShouldOutputOneEntryEachInOrder() throws Exception {

        int exitCode = run("-o", "0,0", "-p", "North=10,0", "-p", "10,90", "-p", "West=0,-10");

        assertEquals(0, exitCode, err.toString());

        JsonNode points = new ObjectMapper().readTree(out.toString()).get("points");
        assertEquals(3, points.size());
        assertEquals("North", points.get(0).get("name").asText());
        assertEquals(0.0, points.get(0).get("bearingDegrees").asDouble());
        assertFalse(points.get(1).has("name"), "Unnamed POI should have no name field");
        assertEquals(270.0, points.get(2).get("bearingDegrees").asDouble());
    }

    @Test
    void shouldAcceptNegativeCoordinates() throws Exception {

        int exitCode = run("--origin", "-33.856784,151.215297", "--poi", "-33.852306,151.210787");

        assertEquals(0, exitCode, err.toString());
        assertEquals(1, new ObjectMapper().readTree(out.toString()).get("points").size());
    }

    @Test
    void whenOriginIsMissingShouldFailWithUsageError() {

        int exitCode = run("--poi", "10,0");

        assertEquals(2, exitCode);
        assertTrue(err.toString().contains("--origin"), err.toString());
        assertEquals("", out.toString());
    }

    @Test
    void whenNoPoiIsGivenShouldFailWithUsageError() {

        assertEquals(2, run("--origin", "0,0"));
        assertTrue(err.toString().contains("--poi"), err.toString());
    }

    @Test
    void whenLatitudeIsOutOfRangeShouldFail() {

        assertEquals(2, run("--origin", "91,0", "--poi", "10,0"));
        assertTrue(err.toString().contains("between -90 and 90"), err.toString());
    }

    @Test
    void whenLongitudeIsOutOfRangeShouldFail() {

        assertEquals(2, run("--origin", "0,0", "--poi", "Somewhere=10,181"));
        assertTrue(err.toString().contains("between -180 and 180"), err.toString());
    }

    @Test
    void whenCoordinateIsMalformedShouldFail() {

        assertEquals(2, run("--origin", "51.5", "--poi", "10,0"));
        assertTrue(err.toString().contains("expected latitude,longitude"), err.toString());
    }

    @Test
    void whenCoordinateIsNotANumberShouldFail() {

        assertEquals(2, run("--origin", "0,0", "--poi", "Hill=abc,0"));
        assertTrue(err.toString().contains("'abc'"), err.toString());
    }

    @Test
    void bearingJustBelow360ShouldRoundToZero() {

        assertEquals(0.0, CompassCli.roundBearing(359.97));
        assertEquals(359.9, CompassCli.roundBearing(359.94));
        assertEquals(109.3, CompassCli.roundBearing(109.26));
    }
}
