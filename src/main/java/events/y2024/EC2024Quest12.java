package events.y2024;

import common.geo.CardinalDirection2D;
import common.geo.ImmutableCoordinate2D;
import common.geo.MutableCoordinate2D;
import common.geo.PrincipalDirection2D;
import common.AbstractQuest;
import common.support.interfaces.MainEvent2024;
import common.support.interfaces.Quest12;
import common.support.params.ExecutionParameters;

import java.util.*;

public class EC2024Quest12 extends AbstractQuest implements MainEvent2024, Quest12 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int height = inputLines.size();

        List<Character> sources = List.of('A', 'B', 'C');

        Map<Character, Integer> sourceValues = Map.of('A', 1, 'B', 2, 'C', 3);
        Map<Character, ImmutableCoordinate2D> sourcePositions = new HashMap<>();
        NavigableSet<ImmutableCoordinate2D> targetPositions = new TreeSet<>();

        for (int inputLineRowIndex = 0; inputLineRowIndex < height; inputLineRowIndex++) {

            String line = inputLines.get(inputLineRowIndex);

            int width = line.length();

            for (int columnIndex = 0; columnIndex < width; columnIndex++) {
                char c = line.charAt(columnIndex);

                if (c == 'A' || c == 'B' || c == 'C') {
                    sourcePositions.put(c, ImmutableCoordinate2D.of(columnIndex, height - inputLineRowIndex - 2));
                } else if (c == 'T') {
                    targetPositions.add(ImmutableCoordinate2D.of(columnIndex, height - inputLineRowIndex - 2));
                }
            }

        }

        int total = 0;

        while (!targetPositions.isEmpty()) {

            ImmutableCoordinate2D targetTowerTopPosition = targetPositions.getFirst();
            int targetTowerHeight = targetTowerTopPosition.getY();

            for (int step = 0; step <= targetTowerHeight; step++ ) {

                ImmutableCoordinate2D targetPosition = targetTowerTopPosition.add(ImmutableCoordinate2D.nTimes(CardinalDirection2D.SOUTH, step));

                if (targetPositions.remove(targetPosition)) {

                    int rankingValue = computeRankingValue(sources, sourceValues, sourcePositions, targetPosition);
                    total += rankingValue;

                }

            }

        }

        return Integer.toString(total);
    }

    private List<ImmutableCoordinate2D> createPath(ImmutableCoordinate2D source, int shootingPower) {

        List<ImmutableCoordinate2D> path = new ArrayList<>();

        MutableCoordinate2D projectileCurrentPosition = MutableCoordinate2D.copyOf(source);

        // Move up and to the right
        for (int step = 1; step <= shootingPower; step++) {
            projectileCurrentPosition.move(PrincipalDirection2D.NORTH_EAST);
            path.add(ImmutableCoordinate2D.copyOf(projectileCurrentPosition));
        }

        // To the right
        for (int step = 1; step <= shootingPower; step++) {
            projectileCurrentPosition.move(PrincipalDirection2D.EAST);
            path.add(ImmutableCoordinate2D.copyOf(projectileCurrentPosition));
        }

        int heightReached = projectileCurrentPosition.getY();
        // Move down
        for (int step = 1; step <= heightReached; step++) {
            projectileCurrentPosition.move(PrincipalDirection2D.SOUTH_EAST);
            path.add(ImmutableCoordinate2D.copyOf(projectileCurrentPosition));
        }

        return path;

    }

    private int computeRankingValue(List<Character> sources, Map<Character, Integer> sourceValues,
                                    Map<Character, ImmutableCoordinate2D> sourcePositions, ImmutableCoordinate2D targetPosition) {

        int distanceToTarget = targetPosition.getX();
        for (int shootingPower = 1; shootingPower < distanceToTarget; shootingPower++) {
            for (Character source: sources) {

                ImmutableCoordinate2D sourcePosition = sourcePositions.get(source);
                List<ImmutableCoordinate2D> path = createPath(sourcePosition, shootingPower);

                if (path.contains(targetPosition)) {
                    return sourceValues.get(source) * shootingPower;
                }

            }

        }

        throw new IllegalStateException("No trajectory hits the target");

    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int height = inputLines.size();

        List<Character> sources = List.of('A', 'B', 'C');

        Map<Character, Integer> sourceValues = Map.of('A', 1, 'B', 2, 'C', 3);
        Map<Character, ImmutableCoordinate2D> sourcePositions = new HashMap<>();
        NavigableSet<ImmutableCoordinate2D> targetPositions = new TreeSet<>();
        Set<ImmutableCoordinate2D> hardRockPositions = new HashSet<>();

        for (int inputLineRowIndex = 0; inputLineRowIndex < height; inputLineRowIndex++) {

            String line = inputLines.get(inputLineRowIndex);

            int width = line.length();

            for (int columnIndex = 0; columnIndex < width; columnIndex++) {
                char c = line.charAt(columnIndex);

                if (c == 'A' || c == 'B' || c == 'C') {
                    sourcePositions.put(c, ImmutableCoordinate2D.of(columnIndex, height - inputLineRowIndex - 2));
                } else if (c == 'T' || c == 'H') {

                    ImmutableCoordinate2D coordinate2D = ImmutableCoordinate2D.of(columnIndex, height - inputLineRowIndex - 2);
                    targetPositions.add(coordinate2D);
                    if (c == 'H') {
                        hardRockPositions.add(coordinate2D);
                    }

                }
            }

        }

        int total = 0;

        while (!targetPositions.isEmpty()) {

            ImmutableCoordinate2D targetTowerTopPosition = targetPositions.getFirst();
            int targetTowerHeight = targetTowerTopPosition.getY();

            for (int step = 0; step <= targetTowerHeight; step++ ) {

                ImmutableCoordinate2D targetPosition = targetTowerTopPosition.add(ImmutableCoordinate2D.nTimes(CardinalDirection2D.SOUTH, step));

                if (targetPositions.remove(targetPosition)) {

                    int rankingValue = computeRankingValue(sources, sourceValues, sourcePositions, targetPosition);

                    if (hardRockPositions.contains(targetPosition)) {
                        rankingValue *= 2;
                    }

                    total += rankingValue;

                }

            }

        }

        return Integer.toString(total);


    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return NOT_IMPLEMENTED;
    }
}
