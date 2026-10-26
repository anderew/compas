package org.rendell.toposcope.model;

import lombok.Data;

import java.util.List;

@Data
public class Compass {

    private final Location source;
    private final List<Location> pointsOnCompass;

}
