package challenges.ec2024;

import challenges.Quest;
import challenges.interfaces.ECEvent2024;
import challenges.interfaces.Quest10;
import challenges.interfaces.Quest15;
import challenges.params.ExecutionParameters;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ListMultimap;
import common.Grids;
import common.VisualSolutions;
import common.geo.Cell2D;
import common.geo.ImmutableCell2D;
import common.pathfinding.PathFinder;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EC2024Quest15 extends Quest implements ECEvent2024, Quest15 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        boolean[][] maze = new boolean[numberOfRows][numberOfColumns];

        Cell2D startPosition = findStartPosition(numberOfColumns, inputLines.getFirst());

        List<Cell2D> herbPositions = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] chars = inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                char c = chars[columnIndex];

                if (c == '#') {
                    maze[rowIndex][columnIndex] = true; // poison bush
                } else if (c == 'H') {
                    herbPositions.add(ImmutableCell2D.of(rowIndex, columnIndex));
                }
            }
        }

        log("Start position is {}, herb positions are {}", startPosition, herbPositions);

        PathFinder<Cell2D> pathFinder = PathFinder.forSimpleMaze(maze);

        int minDistanceToHerb = Integer.MAX_VALUE;

        for (Cell2D herbPosition: herbPositions) {
            int distanceToHerb = pathFinder.shortestDistance(startPosition, herbPosition);
            minDistanceToHerb = Integer.min(minDistanceToHerb, distanceToHerb);
        }

        log("Herb reachable in {} steps", minDistanceToHerb);

        return Integer.toString(2 * minDistanceToHerb);
    }

    private ImmutableCell2D findStartPosition(int numberOfColumns, String topLine) {
        for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
            if (topLine.charAt(columnIndex) == '.') {
                return ImmutableCell2D.of(0, columnIndex);
            }
        }
        throw new IllegalArgumentException("Top line does not have a start position");
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        boolean[][] maze = new boolean[numberOfRows][numberOfColumns];

        Cell2D startPosition = findStartPosition(numberOfColumns, inputLines.getFirst());
        ListMultimap<String, Cell2D> herbPositions = ArrayListMultimap.create();

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] chars = inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                char c = chars[columnIndex];

                if (c != '.') {

                    if (c == '#' || c == '~') {
                        maze[rowIndex][columnIndex] = true; // poison bush
                    } else {
                        // Herb
                        herbPositions.put(Character.toString(c), ImmutableCell2D.of(rowIndex, columnIndex));
                    }

                }
            }
        }

        log("Start position is {}", startPosition);
        for (Map.Entry<String, Collection<Cell2D>> entry: herbPositions.asMap().entrySet()) {
            String herb = entry.getKey();
            Collection<Cell2D> positions = entry.getValue();
            log("There are {} {} herbs, at positions: {}", positions.size(), herb, positions);
        }

        VisualSolutions.print(maze, this::log);

        PathFinder<Cell2D> pathFinder = PathFinder.forSimpleMaze(maze);

        Map<Cell2D, Integer> distancesToHerbs = pathFinder.shortestDistances(startPosition, herbPositions.values());

        log("Distances to herbs are: {}", distancesToHerbs);

        Set<String> herbNames = herbPositions.keySet();
        int numberOfHerbs = herbNames.size();

        Collection<List<String>> permutations = Collections2.permutations(herbNames);

        AtomicInteger minSeen = new AtomicInteger(Integer.MAX_VALUE);

        for (List<String> herbVisitOrder: permutations) {
            log("Trying to visit herbs in the following order: {}", herbVisitOrder);
            setMinNumberOfSteps(numberOfHerbs, herbPositions, pathFinder, minSeen, herbVisitOrder, startPosition, 0, startPosition, 0);

            log("Min seen so far is {}", minSeen.get());

        }

        return Integer.toString(minSeen.get());
    }

    private void setMinNumberOfSteps(int numberOfHerbs, ListMultimap<String, Cell2D> herbPositions,
                                     PathFinder<Cell2D> pathFinder, AtomicInteger minSeen, List<String> herbVisitingOrder, Cell2D overallStart,
                                     int nextHerbIndex, Cell2D lastPosition, int numberOfStepsSoFar) {

        if (nextHerbIndex == numberOfHerbs) {

            // All herbs visited. Find distance to origin and update the min if required
            int totalNumberOfSteps = numberOfStepsSoFar + pathFinder.shortestDistance(lastPosition, overallStart);

            minSeen.accumulateAndGet(totalNumberOfSteps, Integer::min);
            return;

        }

        String nextHerbToVisit = herbVisitingOrder.get(nextHerbIndex);
        List<Cell2D> nextHerbPositions = herbPositions.get(nextHerbToVisit);

        Map<Cell2D, Integer> distancesToNextHerbPositions = pathFinder.shortestDistances(lastPosition, nextHerbPositions);

        for (Cell2D nextHerbPosition: nextHerbPositions) {

            int distanceToNextHerb = distancesToNextHerbPositions.get(nextHerbPosition);

            if (distanceToNextHerb != -1) {
                setMinNumberOfSteps(numberOfHerbs, herbPositions, pathFinder, minSeen, herbVisitingOrder, overallStart,
                        nextHerbIndex + 1, nextHerbPosition, numberOfStepsSoFar + distanceToNextHerb);
            }

        }

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return NOT_IMPLEMENTED;
    }
}
