package common;

import common.geo.PrincipalDirection2D;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public final class Grids {

    private Grids() {
    }

    public static boolean[][] createGrid(String input) {
        List<String> lines = Strings.splitByRow(input);
        int numberOfRows = lines.size();
        int numberOfColumns = lines.getFirst().length();
        boolean[][] grid = new boolean[numberOfRows][numberOfColumns];

        for (int row = 0; row < numberOfRows; row++) {
            char[] charArray = lines.get(row).toCharArray();
            for (int column = 0; column < numberOfColumns; column++) {
                char c = charArray[column];
                if (c == '#') {
                    grid[row][column] = true;
                }
            }
        }
        return grid;
    }

    public static boolean isPositionInGrid(int numberOfRows, int numberOfColumns, int row, int col) {
        return 0 <= row && row < numberOfRows && 0 <= col && col < numberOfColumns;
    }

    public static int getNumberOfTrueNeighbors(boolean[][] grid, int numberOfRows, int numberOfColumns, int row, int col) {
        return (int) Arrays.stream(PrincipalDirection2D.values())
                .filter(dir -> getStateOfCell(grid, numberOfRows, numberOfColumns,
                        row + dir.getUnitVectorCell().getRow(),
                        col + dir.getUnitVectorCell().getColumn())).count();
    }

    public static boolean getStateOfCell(boolean[][] grid, int numberOfRows, int numberOfColumns, int row, int col) {
        return isPositionInGrid(numberOfRows, numberOfColumns, row, col) && grid[row][col];
    }

    public static long countTrueCells(boolean[][] grid) {
        long cont = 0;
        for (boolean[] row : grid) {
            for (boolean cell : row) {
                if (cell) cont++;
            }
        }
        return cont;
    }

    public static boolean hasTrueCells(boolean[][] grid) {
        for (boolean[] row: grid) {
            for (boolean cell: row) {
                if (cell) return true;
            }
        }
        return false;
    }

    public static <T> int getNumberOfNeighbors(T[][] grid, int numberOfRows, int numberOfColumns, int row, int col, Predicate<? super T> condition) {
        return (int) Arrays.stream(PrincipalDirection2D.values())
                .filter(dir -> getStateOfCell(grid, numberOfRows, numberOfColumns,
                        row + dir.getUnitVectorCell().getRow(),
                        col + dir.getUnitVectorCell().getColumn(),
                        condition)).count();

    }

    public static <T> boolean getStateOfCell(T[][] grid, int numberOfRows, int numberOfColumns, int row, int col, Predicate<? super T> condition) {
        return isPositionInGrid(numberOfRows, numberOfColumns, row, col) && condition.test(grid[row][col]);
    }

    public static <T> long countCells(T[][] grid, Predicate<? super T> condition) {
        long cont = 0;
        for (T[] row : grid) {
            for (T cell : row) {
                if (condition.test(cell)) {
                    cont++;
                }
            }
        }
        return cont;
    }

}
