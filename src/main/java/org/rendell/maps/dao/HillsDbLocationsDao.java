package org.rendell.maps.dao;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
//import org.h2gis.ext.H2GISExtension;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.SpatialResultSet;
import org.rendell.maps.dao.LocationsDao;
import org.rendell.maps.model.Coordinate;
import org.rendell.maps.model.Location;
import org.rendell.maps.model.LocationType;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class HillsDbLocationsDao implements LocationsDao {

    private Connection connection;

    @PostConstruct
    public void initialise() {
        try {
            Class.forName("org.h2.Driver");
            // Open memory H2 table

            //DataSource wrappedDataSource = SFSUtilities.wrapSpatialDataSource(originalDataSource);
            connection = SFSUtilities.wrapConnection(DriverManager.getConnection("jdbc:h2:mem:syntax", "sa", "sa"));

            Statement st = connection.createStatement();
            // Import spatial functions, domains and drivers
            // If you are using a file database, you have to do only that once.
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
            ex.printStackTrace();
        }
    }

    @Override
    public List<Location> findLocations(Coordinate nearTo, long maxDistanceMetres) {

        List<Location> locations = new ArrayList<>();

        double degrees = convertMetresToDegrees(maxDistanceMetres);

        try {
            // Open memory H2 table
            Statement st = connection.createStatement();

            try (SpatialResultSet rs = st.executeQuery("SELECT p2.name, p2.the_geom, p2.classification, height_in_metres, ST_GoogleMapLink(p2.the_geom)\n" +
                    "FROM  points p2\n" +
                    "WHERE ST_INTERSECTS(ST_BUFFER(ST_MakePoint(" + nearTo.getLongitude() + ", " + nearTo.getLatitude() + "), " + degrees + "), p2.the_geom)")
                    .unwrap(SpatialResultSet.class)) {
                while (rs.next()) {
                    LocationBuilderFromGis builder = new LocationBuilderFromGis();
                    builder.setGeometry(rs.getGeometry("the_geom"))
                        .setName(rs.getString("name"))
                        .setHeight(rs.getInt("height_in_metres"))
                        .setClassification(rs.getString("classification"));
                    locations.add(builder.build());
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return locations;
    }

    private double convertMetresToDegrees(long metres) {
        final double KM_PER_DEGREE = 111;
        final double M_PER_DEGREE = KM_PER_DEGREE * 1000;
        return metres / M_PER_DEGREE;
    }
}