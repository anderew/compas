package org.rendell.maps.dao;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rendell.maps.model.Coordinate;
import org.rendell.maps.model.Location;
import org.rendell.maps.model.LocationType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationBuilderFromGisTest {

    @Mock
    Geometry geometry;

    @Mock Point point;

    @Test
    public void buildMunro() {

        when(geometry.getInteriorPoint()).thenReturn(point);
        // Letting the point return zeros as default mock behaviour

        LocationBuilderFromGis builder = new LocationBuilderFromGis();
        builder.setGeometry(geometry)
                .setName("big hill")
                .setClassification("Ma,M,Sim,CoH,CoU,CoA")
                .setHeight(1000);

        Location actual = builder.build();
        Location expected = new Location(new Coordinate(0, 0), "big hill", LocationType.MUNRO, 1000);

        assertEquals(expected, actual);
    }

    @Test
    public void buildMarylyn() {

        when(geometry.getInteriorPoint()).thenReturn(point);
        // Letting the point return zeros as default mock behaviour

        LocationBuilderFromGis builder = new LocationBuilderFromGis();
        builder.setGeometry(geometry)
                .setName("smaller hill")
                .setClassification("Ma,Sim,CoH,CoU,CoA")
                .setHeight(1000);

        Location actual = builder.build();
        Location expected = new Location(new Coordinate(0, 0), "smaller hill", LocationType.MARYLYN, 1000);

        assertEquals(expected, actual);
    }

    @Test
    public void buildUnclassfiedLocation() {

        when(geometry.getInteriorPoint()).thenReturn(point);
        // Letting the point return zeros as default mock behaviour

        LocationBuilderFromGis builder = new LocationBuilderFromGis();
        builder.setGeometry(geometry)
                .setName("bump")
                .setClassification("CoA")
                .setHeight(1000);

        Location actual = builder.build();
        Location expected = new Location(new Coordinate(0, 0), "bump", LocationType.UNCLASSFIED, 1000);

        assertEquals(expected, actual);
    }

}