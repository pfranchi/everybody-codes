package events.y2025;

import common.AbstractQuest;
import common.Grids;
import common.geo.CardinalDirection2D;
import common.geo.Cell2D;
import common.geo.ImmutableCell2D;
import common.support.interfaces.MainEvent2025;
import common.support.interfaces.Quest12;
import common.support.params.ExecutionParameters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EC2025Quest12 extends AbstractQuest implements MainEvent2025, Quest12 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        ParsedInput parsedInput = parseInput(inputLines);

        int numberOfRows = parsedInput.numberOfRows();
        int numberOfColumns = parsedInput.numberOfColumns();
        int[][] grid = parsedInput.grid();

        ImmutableCell2D start = ImmutableCell2D.of(0, 0);

        Set<ImmutableCell2D> visited = performChainReaction(numberOfRows, numberOfColumns, grid, Set.of(), Set.of(start));

        return Integer.toString(visited.size());
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        ParsedInput parsedInput = parseInput(inputLines);

        int numberOfRows = parsedInput.numberOfRows();
        int numberOfColumns = parsedInput.numberOfColumns();
        int[][] grid = parsedInput.grid();

        ImmutableCell2D start1 = ImmutableCell2D.of(0, 0);
        ImmutableCell2D start2 = ImmutableCell2D.of(numberOfRows - 1, numberOfColumns - 1);

        Set<ImmutableCell2D> visited = performChainReaction(numberOfRows, numberOfColumns, grid, Set.of(), Set.of(start1, start2));

        return Integer.toString(visited.size());

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        // TODO Can be optimized

        ParsedInput parsedInput = parseInput(inputLines);

        int numberOfRows = parsedInput.numberOfRows();
        int numberOfColumns = parsedInput.numberOfColumns();
        int[][] grid = parsedInput.grid();

        Set<ImmutableCell2D> alreadyDestroyed = new HashSet<>();
        for (int i = 1; i <= 3; i++) {

            Set<ImmutableCell2D> bestChainReactionVisitedCells = findBestChainReaction(numberOfRows, numberOfColumns, grid, alreadyDestroyed);
            alreadyDestroyed.addAll(bestChainReactionVisitedCells);

        }

        return Integer.toString(alreadyDestroyed.size());

    }

    private record ParsedInput(int numberOfRows, int numberOfColumns, int[][] grid) {}

    private ParsedInput parseInput(List<String> inputLines) {
        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        int[][] grid = new int[numberOfRows][numberOfColumns];

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] row = inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                grid[rowIndex][columnIndex] = Integer.parseInt(Character.toString(row[columnIndex]));
            }
        }

        return new ParsedInput(numberOfRows, numberOfColumns, grid);
    }

    private Set<ImmutableCell2D> performChainReaction(int numberOfRows, int numberOfColumns,
                                                      int[][] grid, Set<ImmutableCell2D> invalidCells,
                                                      Set<ImmutableCell2D> chainReactionStartingLocations) {

        Set<ImmutableCell2D> boundary = new HashSet<>(chainReactionStartingLocations);

        Set<ImmutableCell2D> visited = new HashSet<>(chainReactionStartingLocations);

        while (!boundary.isEmpty()) {

            Set<ImmutableCell2D> newBoundary = new HashSet<>();

            for (ImmutableCell2D cellInTheBoundary: boundary) {

                int currentValue = get(grid, cellInTheBoundary);

                for (CardinalDirection2D direction: CardinalDirection2D.values()) {

                    ImmutableCell2D neighbor = cellInTheBoundary.add(direction);

                    if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, neighbor)
                        && !invalidCells.contains(neighbor)
                        && get(grid, neighbor) <= currentValue
                        && !visited.contains(neighbor)) {
                        newBoundary.add(neighbor);
                    }

                }

            }

            visited.addAll(newBoundary);
            boundary = newBoundary;

        }

        return visited;

    }

    private int get(int[][] grid, Cell2D cell2D) {
        return grid[cell2D.getRow()][cell2D.getColumn()];
    }

    private Set<ImmutableCell2D> findBestChainReaction(int numberOfRows, int numberOfColumns,
                                                      int[][] grid, Set<ImmutableCell2D> invalidCells) {

        int maxSize = Integer.MIN_VALUE;
        Set<ImmutableCell2D> candidateVisitedCells = null;

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {

                Set<ImmutableCell2D> chainReactionResult = performChainReaction(numberOfRows, numberOfColumns, grid, invalidCells, Set.of(ImmutableCell2D.of(rowIndex, columnIndex)));

                if (chainReactionResult.size() > maxSize) {
                    maxSize = chainReactionResult.size();
                    candidateVisitedCells = chainReactionResult;
                }

            }
        }

        return candidateVisitedCells;

    }

}
