package events.y2025;

import common.AbstractQuest;
import common.geo.Cell2D;
import common.geo.ImmutableCell2D;
import common.geo.PrincipalDirection2D;
import common.grids.Grids;
import common.support.interfaces.MainEvent2025;
import common.support.interfaces.Quest14;
import common.support.params.ExecutionParameters;

import java.util.List;

public class EC2025Quest14 extends AbstractQuest implements MainEvent2025, Quest14 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(inputLines, 10);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(inputLines, 2025);
    }

    private static final List<PrincipalDirection2D> DIAGONAL_DIRECTIONS = List.of(
            PrincipalDirection2D.NORTH_EAST, PrincipalDirection2D.SOUTH_EAST, PrincipalDirection2D.NORTH_WEST, PrincipalDirection2D.SOUTH_WEST
    );

    private String solve(List<String> inputLines, int numberOfRounds) {

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        boolean[][] grid = new boolean[numberOfRows][numberOfColumns];

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] row = inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                if (row[columnIndex] == '#') {
                    grid[rowIndex][columnIndex] = true;
                }
            }
        }

        long total = 0L;

        for (int round = 1; round <= numberOfRounds; round++) {

            boolean[][] newGrid = new boolean[numberOfRows][numberOfColumns];

            for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
                boolean[] row = grid[rowIndex];
                for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {

                    boolean currentCell = row[columnIndex];

                    int numberOfTrueDiagonalNeighbors = countTrueDiagonalNeighbors(numberOfRows, numberOfColumns, grid, ImmutableCell2D.of(rowIndex, columnIndex));

                    if ((currentCell && numberOfTrueDiagonalNeighbors % 2 == 1) || (!currentCell && numberOfTrueDiagonalNeighbors % 2 == 0)) {
                        newGrid[rowIndex][columnIndex] = true;
                    }
                }
            }

            long val = Grids.countTrueCells(newGrid);

            total += val;
            grid = newGrid;

        }

        return Long.toString(total);

    }

    private int countTrueDiagonalNeighbors(int numberOfRows, int numberOfColumns, boolean[][] grid, Cell2D location) {
        return (int) DIAGONAL_DIRECTIONS.stream()
                .map(location::add)
                .filter(diagonalNeighbor -> Grids.isPositionInGrid(numberOfRows, numberOfColumns, diagonalNeighbor) && get(grid, diagonalNeighbor))
                .count();
    }

    private boolean get(boolean[][] grid, Cell2D cell) {
        return grid[cell.getRow()][cell.getColumn()];
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        // TODO This solution is already fast but it can be polished up.
        // Do not rely on the number 4095 as an outside source of the period length. Compute by actually comparing the grids
        // In the first pass, record the rounds in which matching grids appear, so that it is not necessary to run the simulation
        // again for the last range of rounds.

        long numberOfRounds = 1000000000L;

        int globalNumberOfRows = 34;
        int globalNumberOfColumns = 34;

        int targetAreaNumberOfRows = inputLines.size();
        int targetAreaNumberOfColumns = inputLines.getFirst().length();

        int rowOffset = (globalNumberOfRows - targetAreaNumberOfRows) / 2;
        int columnOffset = (globalNumberOfColumns - targetAreaNumberOfColumns) / 2;

        log("Target area has {} rows and {} columns. Row offset = {}, column offset = {}",
                targetAreaNumberOfRows, targetAreaNumberOfColumns, rowOffset, columnOffset);

        boolean[][] targetGrid = new boolean[targetAreaNumberOfRows][targetAreaNumberOfColumns];

        for (int rowIndex = 0; rowIndex < targetAreaNumberOfRows; rowIndex++) {
            char[] row = inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < targetAreaNumberOfColumns; columnIndex++) {
                if (row[columnIndex] == '#') {
                    targetGrid[rowIndex][columnIndex] = true;
                }
            }
        }

        boolean[][] grid = new boolean[globalNumberOfRows][globalNumberOfColumns];

        int matchCount = 0;

        long totalActiveCellsInOnePeriod = 0L;

        for (int round = 1; round <= 4095; round++) {

            boolean[][] newGrid = new boolean[globalNumberOfRows][globalNumberOfColumns];

            for (int rowIndex = 0; rowIndex < globalNumberOfRows; rowIndex++) {
                boolean[] row = grid[rowIndex];
                for (int columnIndex = 0; columnIndex < globalNumberOfColumns; columnIndex++) {

                    boolean currentCell = row[columnIndex];

                    int numberOfTrueDiagonalNeighbors = countTrueDiagonalNeighbors(globalNumberOfRows, globalNumberOfColumns, grid, ImmutableCell2D.of(rowIndex, columnIndex));

                    if ((currentCell && numberOfTrueDiagonalNeighbors % 2 == 1) || (!currentCell && numberOfTrueDiagonalNeighbors % 2 == 0)) {
                        newGrid[rowIndex][columnIndex] = true;
                    }
                }
            }

            if (matches(targetAreaNumberOfRows, targetAreaNumberOfColumns, targetGrid, rowOffset, columnOffset, newGrid)) {

                long numberOfActiveCells = Grids.countTrueCells(newGrid);

                log("Found match #{} at round {}. Active cells = {}", ++matchCount, round, numberOfActiveCells);

                totalActiveCellsInOnePeriod += numberOfActiveCells;

            }

            grid = newGrid;

        }

        log("After 4095 rounds, the grid has {} active cells", Grids.countTrueCells(grid));

        log("In one cycle there will be {} active cells in total", totalActiveCellsInOnePeriod);

        long numberOfFullCycles = numberOfRounds / 4095;

        long grandTotal = numberOfFullCycles * totalActiveCellsInOnePeriod;

        log("After {} full cycles, at round {}, {} cells have been active", numberOfFullCycles,
                4095 * numberOfFullCycles, grandTotal);

        for (long round = 4095 * numberOfFullCycles + 1; round <= numberOfRounds; round++) {

            boolean[][] newGrid = new boolean[globalNumberOfRows][globalNumberOfColumns];

            for (int rowIndex = 0; rowIndex < globalNumberOfRows; rowIndex++) {
                boolean[] row = grid[rowIndex];
                for (int columnIndex = 0; columnIndex < globalNumberOfColumns; columnIndex++) {

                    boolean currentCell = row[columnIndex];

                    int numberOfTrueDiagonalNeighbors = countTrueDiagonalNeighbors(globalNumberOfRows, globalNumberOfColumns, grid, ImmutableCell2D.of(rowIndex, columnIndex));

                    if ((currentCell && numberOfTrueDiagonalNeighbors % 2 == 1) || (!currentCell && numberOfTrueDiagonalNeighbors % 2 == 0)) {
                        newGrid[rowIndex][columnIndex] = true;
                    }
                }
            }

            if (matches(targetAreaNumberOfRows, targetAreaNumberOfColumns, targetGrid, rowOffset, columnOffset, newGrid)) {

                long numberOfActiveCells = Grids.countTrueCells(newGrid);

                log("Found match #{} at round {}. Active cells = {}", ++matchCount, round, numberOfActiveCells);

                grandTotal += numberOfActiveCells;

            }

            grid = newGrid;

        }

        return Long.toString(grandTotal);
    }

    private boolean matches(int targetAreaNumberOfRows, int targetAreaNumberOfColumns, boolean[][] targetGrid, int rowOffset, int columnOffset, boolean[][] grid) {
        for (int rowIndex = 0; rowIndex < targetAreaNumberOfRows; rowIndex++) {
            boolean[] targetRow = targetGrid[rowIndex];
            boolean[] rowToCheck = grid[rowIndex + rowOffset]; // This is a longer array (it has more columns)
            for (int columnIndex = 0; columnIndex < targetAreaNumberOfColumns; columnIndex++) {

                boolean targetCell = targetRow[columnIndex];
                boolean cellToCheck = rowToCheck[columnIndex + columnOffset];

                if (targetCell != cellToCheck) {
                    return false;
                }

            }
        }

        return true;
    }

}
