package org.rendell.maps.dao;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import lombok.extern.slf4j.Slf4j;
import org.rendell.maps.model.Coordinate;
import org.rendell.maps.model.Location;
import org.rendell.maps.model.LocationType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class LocationBuilderFromGis {

    private Coordinate coordinate;
    private String name;
    private int height;
    private LocationType locationType;

    public LocationBuilderFromGis setGeometry(Geometry geometry) {
        Point point = geometry.getInteriorPoint();
        coordinate = new Coordinate(point.getY(), point.getX());
        return this;
    }

    public LocationBuilderFromGis setName(String name) {
        this.name = name;
        return this;
    }

    public LocationBuilderFromGis setHeight(int height) {
        this.height = height;
        return this;
    }

    public Location build() {
        return new Location(coordinate, name, locationType, height);
    }

    public LocationBuilderFromGis setClassification(String classification) {
        String[] classifications = classification.split(",");

        locationType = mapClassificationToLocationType(new HashSet<>(Arrays.asList(classifications)));

        log.info("classification {} maps to {}", classification.toString(), locationType);

        return this;
    }

    /**
     *         // Ma,M,Sim
     *         // Ma,M,Sim,CoH,CoU,CoA
     *         // Hu,M,Sim
     *         http://www.hills-database.co.uk/database_notes.html#fields
     *
     *             Ma	Marilyn
     *             Hu	Hump
     *             Sim	Simm
     *             5	Dodd
     *             M	Munro
     *             MT	Munro Top
     *             F	Furth
     *             C	Corbett
     *             G	Graham
     *             D	Donald
     *             DT	Donald Top
     *             Hew	Hewitt
     *             N	Nuttall
     *             Dew	Dewey
     *             DDew	Donald Dewey
     *             HF	Highland Five
     *             4	400-499m Tump
     *             3	300-399m Tump (GB)
     *             2	200-299m Tump (GB)
     *             1	100-199m Tump (GB)
     *             0	0-99m Tump (GB)
     *             W	Wainwright
     *             WO	Wainwright Outlying Fell
     *             B	Birkett
     *             Sy	Synge
     *             Fel	Fellranger
     *             CoH	County Top – Historic (pre-1974)
     *             CoA	County Top – Administrative (1974 to mid-1990s)
     *             CoU	County Top – Current County or Unitary Authority
     *             CoL	County Top – Current London Borough
     *             SIB	Significant Island of Britain
     *             Dil	Dillon51.754222
     *             A	Arderin
     *             VL	Vandeleur-Lynam
     *             MDew	Myrddyn Dewey
     *             O	Other list
     *             Un	unclassified
     *             prefixes
     *             s	sub
     *             x	deleted
     *             suffixes
     *             =	twin
     *
     * @param classifications
     * @return
     */
    private LocationType mapClassificationToLocationType(Set<String> classifications) {

        // Order is significant (all Marylyns are Munros but all Munros are not Marylyns)

        if(classifications.contains("M")) {
            return LocationType.MUNRO;
        }
        if(classifications.contains("C")) {
            return LocationType.CORBETT;
        }
        if(classifications.contains("G")) {
            return LocationType.GRAHAM;
        }
        if(classifications.contains("D")) {
            return LocationType.DONALD;
        }
        if(classifications.contains("Ma")) {
            return LocationType.MARYLYN;
        }

        return LocationType.UNCLASSFIED;
    }
}
