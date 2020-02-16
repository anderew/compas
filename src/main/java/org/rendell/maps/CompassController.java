package org.rendell.maps;

import org.rendell.maps.model.Compass;
import org.rendell.maps.model.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class CompassController {

    @Autowired
    private CompassService compassService;

    @RequestMapping(method = GET, value = "/compass")
    @CrossOrigin(origins = "https://localhost:3000")
    public Compass compass(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam(value = "radius", defaultValue = "20000") long radius) {

        return compassService.generateCompass(new Coordinate(latitude, longitude), radius);
    }

}
