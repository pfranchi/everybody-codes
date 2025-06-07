package events.y2024;

import common.geo.Cell2D;
import common.geo.ImmutableCell2D;
import common.pathfinding.PathFinder;
import common.stats.AbstractQuest;
import common.support.interfaces.MainEvent2024;
import common.support.interfaces.Quest18;
import common.support.params.ExecutionParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EC2024Quest18 extends AbstractQuest implements MainEvent2024, Quest18 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        boolean[][] grid = new boolean[numberOfRows][numberOfColumns];
        ImmutableCell2D startingPosition = null;
        List<ImmutableCell2D> palmPositions = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] chars = inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {

                char c = chars[columnIndex];

                if (c == '#') {
                    grid[rowIndex][columnIndex] = true;
                } else if (c == 'P') {
                    palmPositions.add(ImmutableCell2D.of(rowIndex, columnIndex));
                }

                if (c == '.' && (rowIndex == 0 || rowIndex == numberOfRows - 1 || columnIndex == 0 || columnIndex == numberOfColumns - 1)) {
                    startingPosition = ImmutableCell2D.of(rowIndex, columnIndex);
                }

            }
        }

        if (startingPosition == null) {
            throw new IllegalArgumentException("No starting position for this input");
        }

        log("Grid created, starting position is {}, there are {} palms at {}", startingPosition, palmPositions.size(), palmPositions);

        PathFinder<Cell2D> pathFinder = PathFinder.forSimpleMaze(grid);

        Map<Cell2D, Integer> shortestDistances = pathFinder.shortestDistances(startingPosition, palmPositions);

        return Integer.toString(shortestDistances.values().stream().mapToInt(i -> i).max().orElseThrow());

    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        boolean[][] grid = new boolean[numberOfRows][numberOfColumns];
        List<ImmutableCell2D> startingPositions = new ArrayList<>();
        List<ImmutableCell2D> palmPositions = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] chars = inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {

                char c = chars[columnIndex];

                if (c == '#') {
                    grid[rowIndex][columnIndex] = true;
                } else if (c == 'P') {
                    palmPositions.add(ImmutableCell2D.of(rowIndex, columnIndex));
                }

                if (c == '.' && (rowIndex == 0 || rowIndex == numberOfRows - 1 || columnIndex == 0 || columnIndex == numberOfColumns - 1)) {
                    startingPositions.add(ImmutableCell2D.of(rowIndex, columnIndex));
                }

            }
        }

        if (startingPositions.size() != 2) {
            throw new IllegalArgumentException("2 starting points required, found: " + startingPositions.size());
        }

        log("Grid created, starting positions are {}, there are {} palms", startingPositions, palmPositions.size(), palmPositions);

        PathFinder<Cell2D> pathFinder = PathFinder.forSimpleMaze(grid);

        ImmutableCell2D startingPosition1 = startingPositions.getFirst();
        ImmutableCell2D startingPosition2 = startingPositions.getLast();

        Map<Cell2D, Integer> shortestDistances1 = pathFinder.shortestDistances(startingPosition1, palmPositions);
        Map<Cell2D, Integer> shortestDistances2 = pathFinder.shortestDistances(startingPosition2, palmPositions);

        int max = Integer.MIN_VALUE;

        for (ImmutableCell2D palmPosition: palmPositions) {

            int minDistanceFrom1 = shortestDistances1.get(palmPosition);
            int minDistanceFrom2 = shortestDistances2.get(palmPosition);

            int minDistance = Integer.min(minDistanceFrom1, minDistanceFrom2);

            max = Integer.max(max, minDistance);

        }

        return Integer.toString(max);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        boolean[][] grid = new boolean[numberOfRows][numberOfColumns];
        List<ImmutableCell2D> palmPositions = new ArrayList<>();
        List<ImmutableCell2D> emptyPositions = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] chars = inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {

                char c = chars[columnIndex];

                if (c == '#') {
                    grid[rowIndex][columnIndex] = true;
                } else if (c == 'P') {
                    palmPositions.add(ImmutableCell2D.of(rowIndex, columnIndex));
                } else {
                    emptyPositions.add(ImmutableCell2D.of(rowIndex, columnIndex));
                }

            }
        }

        PathFinder<Cell2D> pathFinder = PathFinder.forSimpleMaze(grid);

        int evalCount = 0;
        int minValue = Integer.MAX_VALUE;

        for (ImmutableCell2D emptyPosition: emptyPositions) {

            evalCount++;

            Map<Cell2D, Integer> shortestDistances = pathFinder.shortestDistances(emptyPosition, palmPositions);

            int value = shortestDistances.values().stream().mapToInt(i -> i).sum();

            minValue = Integer.min(minValue, value);

            if (evalCount % 100 == 0) {
                log("Evaluated starting position {} / {}. Min so far {}", evalCount, emptyPositions.size(), minValue);
            }

        }

        return Integer.toString(minValue);
    }
}
