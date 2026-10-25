package org.rendell.maps.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.rendell.maps.BearingCalculator;
import org.rendell.maps.dao.HillsDbLocationsDao;
import org.rendell.maps.dao.LocationsDao;
import org.rendell.maps.model.Coordinate;
import org.rendell.maps.model.Location;
import org.rendell.maps.model.Vector;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Command(name = "compass",
        mixinStandardHelpOptions = true,
        version = "compass 1.0-SNAPSHOT",
        description = "Finds the hills within a radius of an origin and prints, as JSON, the bearing "
                + "(degrees from true north) and distance (metres) to each, nearest first.")
public class CompassCli implements Callable<Integer> {

    static final long DEFAULT_RADIUS_METRES = 10_000;

    @Spec
    CommandSpec spec;

    @Option(names = {"-o", "--origin"}, required = true, paramLabel = "LAT,LON",
            converter = OriginConverter.class,
            description = "Origin in decimal degrees, e.g. 57.068484,-3.810029")
    Coordinate origin;

    @Option(names = {"-r", "--radius"}, paramLabel = "METRES",
            description = "Search radius in metres (default: ${DEFAULT-VALUE})")
    long radiusMetres = DEFAULT_RADIUS_METRES;

    private final Supplier<LocationsDao> locationsDaoSupplier;

    private final BearingCalculator bearingCalculator = new BearingCalculator();

    public CompassCli() {
        this(() -> {
            HillsDbLocationsDao dao = new HillsDbLocationsDao();
            dao.initialise();
            return dao;
        });
    }

    CompassCli(Supplier<LocationsDao> locationsDaoSupplier) {
        this.locationsDaoSupplier = locationsDaoSupplier;
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new CompassCli()).execute(args));
    }

    @Override
    public Integer call() throws Exception {
        if (radiusMetres <= 0) {
            throw new ParameterException(spec.commandLine(), "Radius must be greater than 0 metres, was " + radiusMetres);
        }

        List<Location> locations = locationsDaoSupplier.get().findLocations(origin, radiusMetres);

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        spec.commandLine().getOut().println(mapper.writeValueAsString(buildReport(locations)));
        spec.commandLine().getOut().flush();
        return 0;
    }

    CompassReport buildReport(List<Location> locations) {
        List<CompassReport.Point> points = locations.stream()
                .map(this::toPoint)
                .sorted(Comparator.comparingLong(CompassReport.Point::getDistanceMetres))
                .collect(Collectors.toList());

        return new CompassReport(new CompassReport.Origin(origin.getLatitude(), origin.getLongitude()), radiusMetres, points);
    }

    private CompassReport.Point toPoint(Location location) {
        Coordinate target = location.getCoordinate();
        Vector vector = bearingCalculator.calculate(origin, target);
        return new CompassReport.Point(location.getLabel(),
                location.getLocationType() == null ? null : location.getLocationType().name(),
                location.getAltitude(),
                target.getLatitude(),
                target.getLongitude(),
                roundBearing(vector.getDegrees()),
                vector.getDistanceInMetres());
    }

    /**
     * Rounds to one decimal place, keeping the result in [0, 360).
     */
    static double roundBearing(double degrees) {
        double rounded = Math.round(degrees * 10) / 10.0;
        return rounded >= 360 ? 0 : rounded;
    }

    static class OriginConverter implements CommandLine.ITypeConverter<Coordinate> {
        @Override
        public Coordinate convert(String value) {
            return CoordinateParser.parse(value);
        }
    }
}
