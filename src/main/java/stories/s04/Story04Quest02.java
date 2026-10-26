package stories.s04;

import common.AbstractQuest;
import common.geo.CardinalDirection2D;
import common.geo.Coordinate2D;
import common.geo.ImmutableCoordinate2D;
import common.geo.ImmutableCoordinate3D;
import common.support.interfaces.Quest02;
import common.support.interfaces.Story04;
import common.support.params.ExecutionParameters;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Story04Quest02 extends AbstractQuest implements Story04, Quest02 {

    private static final ThreadLocalRandom RND = ThreadLocalRandom.current();

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        inputLines.forEach(this::log);

        String[] startLineCoordinates = inputLines.getFirst()
                .replace("START=[","").replace("]","")
                .split(",");
        ImmutableCoordinate2D start = ImmutableCoordinate2D.of(
                Integer.parseInt(startLineCoordinates[0]), Integer.parseInt(startLineCoordinates[1])
        );

        String[] aLineCoordinates = inputLines.get(1).replace("A=[", "").replace("]", "").split(",");
        ImmutableCoordinate2D a = ImmutableCoordinate2D.of(Integer.parseInt(aLineCoordinates[0]), Integer.parseInt(aLineCoordinates[1]));

        String[] bLineCoordinates = inputLines.get(2).replace("B=[", "").replace("]", "").split(",");
        ImmutableCoordinate2D b = ImmutableCoordinate2D.of(Integer.parseInt(bLineCoordinates[0]), Integer.parseInt(bLineCoordinates[1]));

        String[] cLineCoordinates = inputLines.get(3).replace("C=[", "").replace("]", "").split(",");
        ImmutableCoordinate2D c = ImmutableCoordinate2D.of(Integer.parseInt(cLineCoordinates[0]), Integer.parseInt(cLineCoordinates[1]));

        char[] moves = inputLines.get(4).replace("MOVES=", "").toCharArray();

        log("Start {}, A = {}, B = {}, C = {}, moves = {}", start, a, b, c, Arrays.toString(moves));

        ImmutableCoordinate2D current = start;
        Set<ImmutableCoordinate2D> visited =  new HashSet<>();
        visited.add(current);

        for (char move: moves) {

            ImmutableCoordinate2D target = switch (move) {
                case 'A' -> a;
                case 'B' -> b;
                case 'C' -> c;
                default -> throw new IllegalArgumentException("Invalid move: " + move);
            };

            ImmutableCoordinate2D midway = midway(current, target);

            log("By moving towards {} we reach {}", target, midway);

            visited.add(midway);
            current = midway;

        }

        return Integer.toString(visited.size());
    }

    private ImmutableCoordinate2D midway(Coordinate2D c1, Coordinate2D c2) {

        int midX = (c1.getX() + c2.getX()) / 2;
        int midY = (c1.getY() + c2.getY()) / 2;

        return ImmutableCoordinate2D.of(midX, midY);

    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        inputLines.forEach(this::log);

        String[] startLineCoordinates = inputLines.getFirst()
                .replace("START=[","").replace("]","")
                .split(",");
        ImmutableCoordinate2D start = ImmutableCoordinate2D.of(
                Integer.parseInt(startLineCoordinates[0]), Integer.parseInt(startLineCoordinates[1])
        );

        String[] aLineCoordinates = inputLines.get(1).replace("A=[", "").replace("]", "").split(",");
        ImmutableCoordinate2D a = ImmutableCoordinate2D.of(Integer.parseInt(aLineCoordinates[0]), Integer.parseInt(aLineCoordinates[1]));

        String[] bLineCoordinates = inputLines.get(2).replace("B=[", "").replace("]", "").split(",");
        ImmutableCoordinate2D b = ImmutableCoordinate2D.of(Integer.parseInt(bLineCoordinates[0]), Integer.parseInt(bLineCoordinates[1]));

        String[] cLineCoordinates = inputLines.get(3).replace("C=[", "").replace("]", "").split(",");
        ImmutableCoordinate2D c = ImmutableCoordinate2D.of(Integer.parseInt(cLineCoordinates[0]), Integer.parseInt(cLineCoordinates[1]));

        char[] moves = inputLines.get(4).replace("MOVES=", "").toCharArray();

        log("Start {}, A = {}, B = {}, C = {}, moves = {}", start, a, b, c, Arrays.toString(moves));

        ImmutableCoordinate2D current = start;
        Set<ImmutableCoordinate2D> visited =  new HashSet<>();
        visited.add(current);

        for (char move: moves) {

            ImmutableCoordinate2D target = switch (move) {
                case 'A' -> a;
                case 'B' -> b;
                case 'C' -> c;
                default -> throw new IllegalArgumentException("Invalid move: " + move);
            };

            ImmutableCoordinate2D midway = midway(current, target);

            log("By moving towards {} we reach {}", target, midway);

            visited.add(midway);
            current = midway;

        }

        Set<ImmutableCoordinate2D> fireflyLocations = new HashSet<>();

        for (ImmutableCoordinate2D beetle: visited) {
            for (CardinalDirection2D direction: CardinalDirection2D.values()) {
                ImmutableCoordinate2D potentialFireflyLocation = beetle.add(direction);

                if (!visited.contains(potentialFireflyLocation)) {
                    fireflyLocations.add(potentialFireflyLocation);
                }
            }
        }

        return Integer.toString(fireflyLocations.size());
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        /*
            15060 right!
         */

        inputLines.forEach(this::log);

        String[] startLineCoordinates = inputLines.getFirst()
                .replace("START=[","").replace("]","")
                .split(",");
        ImmutableCoordinate2D start = ImmutableCoordinate2D.of(
                Integer.parseInt(startLineCoordinates[0]), Integer.parseInt(startLineCoordinates[1])
        );

        String[] aLineCoordinates = inputLines.get(1).replace("A=[", "").replace("]", "").split(",");
        ImmutableCoordinate2D a = ImmutableCoordinate2D.of(Integer.parseInt(aLineCoordinates[0]), Integer.parseInt(aLineCoordinates[1]));

        String[] bLineCoordinates = inputLines.get(2).replace("B=[", "").replace("]", "").split(",");
        ImmutableCoordinate2D b = ImmutableCoordinate2D.of(Integer.parseInt(bLineCoordinates[0]), Integer.parseInt(bLineCoordinates[1]));

        String[] cLineCoordinates = inputLines.get(3).replace("C=[", "").replace("]", "").split(",");
        ImmutableCoordinate2D c = ImmutableCoordinate2D.of(Integer.parseInt(cLineCoordinates[0]), Integer.parseInt(cLineCoordinates[1]));

        log("Start {}, A = {}, B = {}, C = {}", start, a, b, c);

        ImmutableCoordinate2D current = start;
        Set<ImmutableCoordinate2D> visited =  new HashSet<>();
        visited.add(current);

        ImmutableCoordinate2D[] targets = {a, b, c};

        for (long i = 1L; i < 20000000000L; i++) {

            ImmutableCoordinate2D target = targets[RND.nextInt(3)];

            ImmutableCoordinate2D midway = midway(current, target);

            //log("By moving towards {} we reach {}", target, midway);

            visited.add(midway);
            current = midway;

        }

        Set<ImmutableCoordinate2D> fireflyLocations = new HashSet<>();

        for (ImmutableCoordinate2D beetle: visited) {
            for (CardinalDirection2D direction: CardinalDirection2D.values()) {
                ImmutableCoordinate2D potentialFireflyLocation = beetle.add(direction);

                if (!visited.contains(potentialFireflyLocation)) {
                    fireflyLocations.add(potentialFireflyLocation);
                }
            }
        }

        return Integer.toString(fireflyLocations.size());

        //return NOT_IMPLEMENTED;
    }

}
