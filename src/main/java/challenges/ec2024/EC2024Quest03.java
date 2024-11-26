package challenges.ec2024;

import challenges.Quest;
import challenges.interfaces.ECEvent2024;
import challenges.interfaces.Quest03;
import challenges.params.ExecutionParameters;
import common.Grids;
import common.VisualSolutions;
import common.geo.CardinalDirection2D;
import common.geo.Direction2D;
import common.geo.ImmutableCell2D;
import common.geo.PrincipalDirection2D;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class EC2024Quest03 extends Quest implements ECEvent2024, Quest03 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return countTotal(input, CardinalDirection2D::values);
    }

    private String countTotal(String input, Supplier<Direction2D[]> directionSupplier) {
        boolean[][] grid = Grids.createGrid(input);
        int numberOfRows = grid.length;
        int numberOfColumns = grid[0].length;

        long total = 0;

        while (Grids.hasTrueCells(grid)) {

            total += Grids.countTrueCells(grid);

            boolean[][] tempGrid = new boolean[numberOfRows][numberOfColumns];

            for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
                for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                    if (grid[rowIndex][columnIndex]) {

                        int finalRow = rowIndex;
                        int finalCol = columnIndex;

                        // Check if this is a cell completely surrounded by other true cells (cells with '#')

                        if (Arrays.stream(directionSupplier.get())
                                .allMatch(direction -> Grids.getStateOfCell(grid, numberOfRows, numberOfColumns,
                                        finalRow + direction.getUnitVectorCell().getRow(), finalCol + direction.getUnitVectorCell().getColumn()))) {
                            tempGrid[rowIndex][columnIndex] = true;
                        }

                    }
                }
            }

            // Update the grid
            for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
                System.arraycopy(tempGrid[rowIndex], 0, grid[rowIndex], 0, numberOfColumns);
            }

        }

        return Long.toString(total);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return countTotal(input, CardinalDirection2D::values);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return countTotal(input, PrincipalDirection2D::values);
    }
}
