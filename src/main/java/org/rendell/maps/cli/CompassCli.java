package org.rendell.maps.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.rendell.maps.BearingCalculator;
import org.rendell.maps.model.Coordinate;
import org.rendell.maps.model.Vector;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Command(name = "compass",
        mixinStandardHelpOptions = true,
        version = "compass 1.0-SNAPSHOT",
        description = "Prints, as JSON, the bearing (degrees from true north) and distance (metres) "
                + "from an origin to each point of interest.")
public class CompassCli implements Callable<Integer> {

    @Spec
    CommandSpec spec;

    @Option(names = {"-o", "--origin"}, required = true, paramLabel = "LAT,LON",
            converter = OriginConverter.class,
            description = "Origin in decimal degrees, e.g. 51.507556,-0.100602")
    Coordinate origin;

    @Option(names = {"-p", "--poi"}, required = true, paramLabel = "[NAME=]LAT,LON",
            converter = PointOfInterest.Converter.class,
            description = "Point of interest, may be repeated, e.g. \"Shooters Hill=51.470079,0.069604\"")
    List<PointOfInterest> pointsOfInterest;

    private final BearingCalculator bearingCalculator = new BearingCalculator();

    public static void main(String[] args) {
        System.exit(new CommandLine(new CompassCli()).execute(args));
    }

    @Override
    public Integer call() throws Exception {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        spec.commandLine().getOut().println(mapper.writeValueAsString(buildReport()));
        spec.commandLine().getOut().flush();
        return 0;
    }

    CompassReport buildReport() {
        List<CompassReport.Point> points = pointsOfInterest.stream()
                .map(this::toPoint)
                .collect(Collectors.toList());

        return new CompassReport(new CompassReport.Origin(origin.getLatitude(), origin.getLongitude()), points);
    }

    private CompassReport.Point toPoint(PointOfInterest poi) {
        Coordinate target = poi.getCoordinate();
        Vector vector = bearingCalculator.calculate(origin, target);
        return new CompassReport.Point(poi.getName(),
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
