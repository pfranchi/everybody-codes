package events.y2025;

import common.AbstractQuest;
import common.geo.ImmutableCoordinate2D;
import common.support.interfaces.Quest19;
import common.support.interfaces.MainEvent2025;
import common.support.params.ExecutionParameters;

import java.util.*;

public class EC2025Quest19 extends AbstractQuest implements MainEvent2025, Quest19 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(inputLines);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(inputLines);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(inputLines);
    }

    private String solve(List<String> inputLines) {

        ImmutableCoordinate2D startingPosition = ImmutableCoordinate2D.atOrigin();

        NavigableMap<Integer, Wall> walls = new TreeMap<>();

        for (String inputLine: inputLines) {
            String[] parts = inputLine.split(",");
            int xCoordinate = Integer.parseInt(parts[0]);
            int minY = Integer.parseInt(parts[1]);
            int maxY = minY + Integer.parseInt(parts[2]) - 1;

            Wall wall;
            if (walls.containsKey(xCoordinate)) {
                wall = walls.get(xCoordinate);
            } else {
                wall = new Wall(xCoordinate, new ArrayList<>());
                walls.put(xCoordinate, wall);
            }

            List<WallOpening> wallOpenings = wall.openings();

            wallOpenings.add(new WallOpening(minY, maxY));

        }

        int minimumNumberOfFlaps = mininumNumberOfTotalFlaps(startingPosition, walls, 0).orElseThrow();

        return Integer.toString(minimumNumberOfFlaps);
    }

    private OptionalInt mininumNumberOfTotalFlaps(ImmutableCoordinate2D currentPosition,
                                                  NavigableMap<Integer, Wall> remainingWalls,
                                                  int currentNumberOfFlaps) {

        if (remainingWalls.isEmpty()) {
            return OptionalInt.of(currentNumberOfFlaps);
        }

        Map.Entry<Integer, Wall> firstWallEntry = remainingWalls.firstEntry();
        int nextWallXCoordinate = firstWallEntry.getKey();
        Wall nextWall = firstWallEntry.getValue();

        int horizontalDistanceToCover = nextWallXCoordinate - currentPosition.getX();

        for (int numberOfFlapsInThisInterval = 0; numberOfFlapsInThisInterval <= horizontalDistanceToCover; numberOfFlapsInThisInterval++) {

            int yAtWall = currentPosition.getY() + 2 * numberOfFlapsInThisInterval - horizontalDistanceToCover;
            if (nextWall.isOpenAtY(yAtWall)) {

                // Goes through the wall

                ImmutableCoordinate2D nextPosition = ImmutableCoordinate2D.of(nextWallXCoordinate, yAtWall);

                NavigableMap<Integer, Wall> nextRemainingWalls = remainingWalls.tailMap(nextWallXCoordinate, false);

                OptionalInt totalNumberOfFlaps = mininumNumberOfTotalFlaps(nextPosition,
                        nextRemainingWalls,
                        currentNumberOfFlaps + numberOfFlapsInThisInterval);

                if (totalNumberOfFlaps.isPresent()) {
                    return totalNumberOfFlaps;
                }

            }

        }

        return OptionalInt.empty();

    }

    private record Wall(int x, List<WallOpening> openings) {

        private boolean isOpenAtY(int y) {
            return openings.stream().anyMatch(wallOpening -> wallOpening.isOpenAtY(y));
        }

    }

    private record WallOpening(int yMin, int yMax) {
        private boolean isOpenAtY(int y) {
            return yMin <= y && y <= yMax;
        }
    }

}
