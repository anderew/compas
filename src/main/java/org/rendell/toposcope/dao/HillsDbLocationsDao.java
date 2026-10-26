package org.rendell.toposcope.dao;

import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.SpatialResultSet;
import org.rendell.toposcope.BearingCalculator;
import org.rendell.toposcope.model.Coordinate;
import org.rendell.toposcope.model.Location;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HillsDbLocationsDao implements LocationsDao {

    private Connection connection;

    private final BearingCalculator bearingCalculator = new BearingCalculator();

    @PostConstruct
    public void initialise() {
        try {
            Class.forName("org.h2.Driver");
            // Open a private in-memory H2 database, so each DAO gets its own copy

            connection = SFSUtilities.wrapConnection(DriverManager.getConnection("jdbc:h2:mem:", "sa", "sa"));

            Statement st = connection.createStatement();
            // Import spatial functions, domains and drivers
            st.execute("CREATE ALIAS IF NOT EXISTS H2GIS_SPATIAL FOR \"org.h2gis.functions.factory.H2GISFunctions.load\";\n" +
                    "CALL H2GIS_SPATIAL();");

            st.execute("CREATE TABLE POINTS(ID INT PRIMARY KEY,\n" +
                    "                    name VARCHAR ,\n" +
                    "                    THE_GEOM GEOMETRY,\n" +
                    "                    height_in_metres number(4),\n" +
                    "                    Classification varchar)\n" +
                    "AS\n" +
                    "SELECT Number as id, name, ST_MakePoint(Longitude, Latitude) THE_GEOM, Metres, Classification\n" +
                    "        FROM CSVREAD('classpath:DoBIH_v16_2.csv')");


        } catch (Exception ex) {
            throw new IllegalStateException("Unable to load hills database", ex);
        }
    }

    @Override
    public List<Location> findLocations(Coordinate nearTo, long maxDistanceMetres) {

        List<Location> locations = new ArrayList<>();

        double degrees = convertMetresToDegrees(maxDistanceMetres, nearTo.getLatitude());

        try {
            // Open memory H2 table
            Statement st = connection.createStatement();

            try (SpatialResultSet rs = st.executeQuery("SELECT p2.name, p2.the_geom, p2.classification, height_in_metres, ST_GoogleMapLink(p2.the_geom)\n" +
                    "FROM  points p2\n" +
                    "WHERE ST_INTERSECTS(ST_BUFFER(ST_MakePoint(" + nearTo.getLongitude() + ", " + nearTo.getLatitude() + "), " + degrees + "), p2.the_geom)")
                    .unwrap(SpatialResultSet.class)) {
                while (rs.next()) {
                    locations.add(new LocationBuilderFromGis()
                            .setGeometry(rs.getGeometry("the_geom"))
                            .setName(rs.getString("name"))
                            .setHeight(rs.getInt("height_in_metres"))
                            .setClassification(rs.getString("classification"))
                            .build());
                }
            }

        } catch (Exception ex) {
            throw new IllegalStateException("Unable to query hills database", ex);
        }

        // The buffer is a circle in degrees, so trim the candidates to those truly within range
        return locations.stream()
                .filter(location -> bearingCalculator.calculate(nearTo, location.getCoordinate()).getDistanceInMetres() <= maxDistanceMetres)
                .collect(Collectors.toList());
    }

    /**
     * Converts to a buffer in degrees large enough to contain the radius in every direction. A degree of
     * longitude shrinks with latitude, so the buffer is sized for longitude and filtered afterwards.
     */
    private double convertMetresToDegrees(long metres, double latitude) {
        final double KM_PER_DEGREE = 111;
        final double M_PER_DEGREE = KM_PER_DEGREE * 1000;
        double longitudeScale = Math.max(Math.cos(Math.toRadians(latitude)), 0.01);
        return metres / (M_PER_DEGREE * longitudeScale) * 1.01;
    }
}