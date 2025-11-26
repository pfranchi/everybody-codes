package events.y2025;

import common.AbstractQuest;
import common.geo.CardinalDirection2D;
import common.geo.ImmutableCell2D;
import common.grids.Grids;
import common.pathfinding.NeighborProducer;
import common.pathfinding.SimpleNodeWithCost;
import common.pathfinding.algorithms.PathFindingAlgorithms;
import common.support.interfaces.MainEvent2025;
import common.support.interfaces.Quest17;
import common.support.params.ExecutionParameters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EC2025Quest17 extends AbstractQuest implements MainEvent2025, Quest17 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        int[][] grid = new int[numberOfRows][numberOfColumns];

        ImmutableCell2D craterLocation = null;

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] row = inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                char c = row[columnIndex];

                if (c == '@') {
                    craterLocation = ImmutableCell2D.of(rowIndex, columnIndex);
                } else {
                    grid[rowIndex][columnIndex] = Integer.parseInt(Character.toString(c));
                }
            }
        }

        if (craterLocation == null) {
            throw new IllegalArgumentException();
        }

        long total = 0L;

        int radius = 10;
        int radiusSquared = radius * radius;

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            int[] row = grid[rowIndex];
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {

                int rowDelta = Math.abs(rowIndex - craterLocation.getRow());
                int columnDelta = Math.abs(columnIndex - craterLocation.getColumn());

                if (rowDelta * rowDelta + columnDelta * columnDelta <= radiusSquared) {
                    total += row[columnIndex];
                }

            }
        }

        return Long.toString(total);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        int[][] grid = new int[numberOfRows][numberOfColumns];

        ImmutableCell2D craterLocation = null;

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] row = inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                char c = row[columnIndex];

                if (c == '@') {
                    craterLocation = ImmutableCell2D.of(rowIndex, columnIndex);
                } else {
                    grid[rowIndex][columnIndex] = Integer.parseInt(Character.toString(c));
                }
            }
        }

        if (craterLocation == null) {
            throw new IllegalArgumentException();
        }

        int maxRadius = (numberOfRows - 1) / 2;

        int maxDestruction = Integer.MIN_VALUE;
        int radiusOfMaxDestruction = -1;

        for (int radius = 1; radius <= maxRadius; radius++) {

            int radiusSquared = radius * radius;

            int totalDestruction = 0;

            for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
                int[] row = grid[rowIndex];
                for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {

                    int rowDelta = Math.abs(rowIndex - craterLocation.getRow());
                    int columnDelta = Math.abs(columnIndex - craterLocation.getColumn());

                    if (rowDelta * rowDelta + columnDelta * columnDelta <= radiusSquared) {
                        totalDestruction += row[columnIndex];
                        row[columnIndex] = 0;
                    }

                }
            }

            if (totalDestruction > maxDestruction) {
                maxDestruction = totalDestruction;
                radiusOfMaxDestruction = radius;
            }

        }

        return Integer.toString(maxDestruction * radiusOfMaxDestruction);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        int[][] grid = new int[numberOfRows][numberOfColumns];

        ImmutableCell2D tempCraterLocation = null;
        ImmutableCell2D tempStartingLocation = null;

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] row = inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                char c = row[columnIndex];

                if (c == '@') {
                    tempCraterLocation = ImmutableCell2D.of(rowIndex, columnIndex);
                } else if (c == 'S') {
                    tempStartingLocation = ImmutableCell2D.of(rowIndex, columnIndex);
                } else {
                    grid[rowIndex][columnIndex] = Integer.parseInt(Character.toString(c));
                }
            }
        }

        if (tempCraterLocation == null || tempStartingLocation == null) {
            throw new IllegalArgumentException();
        }

        final ImmutableCell2D craterLocation = tempCraterLocation;
        final ImmutableCell2D startingLocation = tempStartingLocation;

        // This implementation relies on the assumption (which happens to be true for all the example inputs and the
        // real input) that the starting location is above the crater, in the same column of the grid.

        int maxRadius = (numberOfRows - 1) / 2;

        for (int radius = 0; radius < maxRadius; radius++) {

            int radiusSquared = radius * radius;
            int timeAvailableUpperBound = 30 * (radius + 1) - 1;

            Set<ImmutableCell2D> invalidCells = new HashSet<>();

            for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
                for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {

                    int rowDelta = Math.abs(rowIndex - craterLocation.getRow());
                    int columnDelta = Math.abs(columnIndex - craterLocation.getColumn());

                    if (rowDelta * rowDelta + columnDelta * columnDelta <= radiusSquared) {
                        invalidCells.add(ImmutableCell2D.of(rowIndex, columnIndex));
                    }

                }
            }

            State initialState = new State(startingLocation, false, false, false);
            State endState = new State(startingLocation, true, true, true);

            NeighborProducer<State> neighborProducer = stateWithCost -> {

                State currentState = stateWithCost.node();
                ImmutableCell2D currentLocation = currentState.currentLocation();
                boolean currentHasCrossedLeft = currentState.hasCrossedLeft();
                boolean currentHasCrossedBelow = currentState.hasCrossedBelow();
                boolean currentHasCrossedRight = currentState.hasCrossedRight();
                int cost = stateWithCost.cost();

                Set<SimpleNodeWithCost<State>> nextStatesWithCosts = new HashSet<>();

                for (CardinalDirection2D direction: CardinalDirection2D.values()) {
                    ImmutableCell2D nextLocation = currentLocation.add(direction);

                    if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, nextLocation) && !invalidCells.contains(nextLocation)) {

                        // We can move to this location. Check if any line has been crossed

                        boolean nextHasCrossedLeft = currentHasCrossedLeft;
                        boolean nextHasCrossedBelow = currentHasCrossedBelow;
                        boolean nextHasCrossedRight = currentHasCrossedRight;

                        if (!currentHasCrossedLeft
                            && nextLocation.getRow() == craterLocation.getRow()
                            && nextLocation.getColumn() < craterLocation.getColumn()) {
                            nextHasCrossedLeft = true;
                        }

                        if (currentHasCrossedLeft && !currentHasCrossedBelow
                            && nextLocation.getColumn() == craterLocation.getColumn()
                            && nextLocation.getRow() > craterLocation.getRow()) {
                            nextHasCrossedBelow = true;
                        }

                        if (currentHasCrossedLeft && currentHasCrossedBelow && !currentHasCrossedRight
                            && nextLocation.getRow() == craterLocation.getRow()
                            && nextLocation.getColumn() > craterLocation.getColumn()) {
                            nextHasCrossedRight = true;
                        }

                        int nextCost = cost + grid[nextLocation.getRow()][nextLocation.getColumn()];

                        State nextState = new State(nextLocation, nextHasCrossedLeft, nextHasCrossedBelow, nextHasCrossedRight);
                        SimpleNodeWithCost<State> nextNodeWithCost = new SimpleNodeWithCost<>(nextState, nextCost);

                        nextStatesWithCosts.add(nextNodeWithCost);

                    }

                }

                return nextStatesWithCosts;
            };

            int minTimeToLoop = PathFindingAlgorithms.minDistanceVariableCost(neighborProducer, initialState, endState);

            if (minTimeToLoop <= timeAvailableUpperBound) {
                int result = minTimeToLoop * radius;
                return Integer.toString(result);
            }

        }

        return NOT_IMPLEMENTED;
    }

    private record State(ImmutableCell2D currentLocation, boolean hasCrossedLeft,
                         boolean hasCrossedBelow, boolean hasCrossedRight) {}

}
