package events.y2024;

import common.Grids;
import common.geo.CardinalDirection2D;
import common.geo.ImmutableCell2D;
import common.pathfinding.PathFindingAlgorithms;
import common.stats.AbstractQuest;
import common.support.interfaces.MainEvent2024;
import common.support.interfaces.Quest13;
import common.support.params.ExecutionParameters;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;

public class EC2024Quest13 extends AbstractQuest implements MainEvent2024, Quest13 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(inputLines);
    }

    private String solve(List<String> inputLines) {
        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.stream().mapToInt(String::length).max().orElseThrow();
        Cell[][] grid = new Cell[numberOfRows][numberOfColumns];

        ImmutableCell2D startPosition = null;
        ImmutableCell2D endPosition = null;

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {

            char[] inputLineChars = inputLines.get(rowIndex).toCharArray();

            for (int columnIndex = 0; columnIndex < inputLineChars.length; columnIndex++) {

                char c = inputLineChars[columnIndex];
                if (c == '#' || c == ' ') {
                    grid[rowIndex][columnIndex] = new Unreachable(ImmutableCell2D.of(rowIndex, columnIndex));
                } else if (c == 'S') {
                    startPosition = ImmutableCell2D.of(rowIndex, columnIndex);
                    grid[rowIndex][columnIndex] = new Valued(ImmutableCell2D.of(rowIndex, columnIndex), 0);
                } else if (c == 'E') {
                    endPosition = ImmutableCell2D.of(rowIndex, columnIndex);
                    grid[rowIndex][columnIndex] = new Valued(ImmutableCell2D.of(rowIndex, columnIndex), 0);
                } else {
                    int value = Integer.parseInt(Character.toString(c));
                    grid[rowIndex][columnIndex] = new Valued(ImmutableCell2D.of(rowIndex, columnIndex), value);
                }

            }

        }

        if (startPosition == null || endPosition == null) {
            throw new IllegalArgumentException("Could not find start S or end E");
        }

        Function<ImmutableCell2D, Set<ImmutableCell2D>> neighborExtractor =
                immutableCell2D -> getNeighbors(numberOfRows, numberOfColumns, grid, immutableCell2D);

        ToIntBiFunction<ImmutableCell2D, ImmutableCell2D> costOfMovingExtractor = (start, end) -> costOfMoving(grid, start, end);

        int minDistance = PathFindingAlgorithms.distanceDijkstra(startPosition, endPosition, neighborExtractor, costOfMovingExtractor);

        return Integer.toString(minDistance);
    }

    private abstract static class Cell {
        private final ImmutableCell2D position;

        public Cell(ImmutableCell2D position) {
            this.position = position;
        }

        public abstract boolean isReachable();

        public ImmutableCell2D getPosition() {
            return position;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Cell.class.getSimpleName() + "[", "]")
                    .add("position=" + position)
                    .toString();
        }
    }

    private static class Unreachable extends Cell {
        public Unreachable(ImmutableCell2D position) {
            super(position);
        }

        @Override
        public boolean isReachable() {
            return false;
        }
    }

    private static class Valued extends Cell {
        private final int value;

        public Valued(ImmutableCell2D position, int value) {
            super(position);
            this.value = value;
        }

        @Override
        public boolean isReachable() {
            return true;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Valued.class.getSimpleName() + "[", "]")
                    .add("value=" + value)
                    .add("position=" + getPosition())
                    .toString();
        }
    }

    private Set<ImmutableCell2D> getNeighbors(int numberOfRows, int numberOfColumns, Cell[][] grid, ImmutableCell2D cell) {
        Set<ImmutableCell2D> neighbors = new LinkedHashSet<>();
        for (CardinalDirection2D direction: CardinalDirection2D.values()) {
            ImmutableCell2D moved = cell.add(direction);
            if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, moved.getRow(), moved.getColumn())) {
                Cell neighbor = grid[moved.getRow()][moved.getColumn()];
                if (neighbor.isReachable()) {
                    neighbors.add(moved);
                }
            }
        }
        return neighbors;
    }

    private int costOfMoving(Cell[][] grid, ImmutableCell2D start, ImmutableCell2D end) {
        Valued startCell = (Valued) grid[start.getRow()][start.getColumn()];
        Valued endCell = (Valued) grid[end.getRow()][end.getColumn()];
        return costOfMoving(startCell, endCell);
    }

    private int costOfMoving(Valued start, Valued end) {
        int valueDiff = Math.abs(start.getValue() - end.getValue());
        return Integer.min(valueDiff, 10 - valueDiff) + 1;
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(inputLines);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.stream().mapToInt(String::length).max().orElseThrow();
        Cell[][] grid = new Cell[numberOfRows][numberOfColumns];

        List<ImmutableCell2D> startPositions = new ArrayList<>();
        ImmutableCell2D endPosition = null;

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {

            char[] inputLineChars = inputLines.get(rowIndex).toCharArray();

            for (int columnIndex = 0; columnIndex < inputLineChars.length; columnIndex++) {

                char c = inputLineChars[columnIndex];
                if (c == '#' || c == ' ') {
                    grid[rowIndex][columnIndex] = new Unreachable(ImmutableCell2D.of(rowIndex, columnIndex));
                } else if (c == 'S') {
                    startPositions.add(ImmutableCell2D.of(rowIndex, columnIndex));
                    grid[rowIndex][columnIndex] = new Valued(ImmutableCell2D.of(rowIndex, columnIndex), 0);
                } else if (c == 'E') {
                    endPosition = ImmutableCell2D.of(rowIndex, columnIndex);
                    grid[rowIndex][columnIndex] = new Valued(ImmutableCell2D.of(rowIndex, columnIndex), 0);
                } else {
                    int value = Integer.parseInt(Character.toString(c));
                    grid[rowIndex][columnIndex] = new Valued(ImmutableCell2D.of(rowIndex, columnIndex), value);
                }

            }

        }

        if (startPositions.isEmpty() || endPosition == null) {
            throw new IllegalArgumentException("Could not find start S or end E");
        }

        Function<ImmutableCell2D, Set<ImmutableCell2D>> neighborExtractor =
                immutableCell2D -> getNeighbors(numberOfRows, numberOfColumns, grid, immutableCell2D);

        ToIntBiFunction<ImmutableCell2D, ImmutableCell2D> costOfMovingExtractor = (start, end) -> costOfMoving(grid, start, end);

        int minDistance = Integer.MAX_VALUE;

        for (ImmutableCell2D startPosition: startPositions) {
            int distance = PathFindingAlgorithms.distanceDijkstra(startPosition, endPosition, neighborExtractor, costOfMovingExtractor);
            minDistance = Integer.min(minDistance, distance);
        }

        return Integer.toString(minDistance);

    }
}
