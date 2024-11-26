package common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class VisualSolutions {

    private VisualSolutions() {
        throw new AssertionError("Not instantiable");
    }

    private static final int PRINT_WIDTH = 1;

    private static final Map<Character, boolean[][]> dictionary = Map.ofEntries(

            Map.entry('A', new boolean[][]{
                    {false, true, true, false}, {true, false, false, true}, {true, false, false, true},
                    {true, true, true, true}, {true, false, false, true}, {true, false, false, true}
            }),

            Map.entry('B', new boolean[][]{
                    {true, true, true, false}, {true, false, false, true}, {true, true, true, false},
                    {true, false, false, true}, {true, false, false, true}, {true, true, true, false}
            }),

            Map.entry('C', new boolean[][]{
                    {false, true, true, false}, {true, false, false, true}, {true, false, false, false},
                    {true, false, false, false}, {true, false, false, true}, {false, true, true, false}
            }),

            Map.entry('D', new boolean[][]{{true, false}, {false, true}}),

            Map.entry('E', new boolean[][]{
                    {true, true, true, true}, {true, false, false, false}, {true, true, true, false},
                    {true, false, false, false}, {true, false, false, false}, {true, true, true, true}
            }),

            Map.entry('F', new boolean[][]{
                    {true, true, true, true}, {true, false, false, false}, {true, true, true, false},
                    {true, false, false, false}, {true, false, false, false}, {true, false, false, false}
            }),

            Map.entry('G', new boolean[][]{{true, false}, {false, true}}),

            Map.entry('H', new boolean[][]{
                    {true, false, false, true}, {true, false, false, true}, {true, true, true, true},
                    {true, false, false, true}, {true, false, false, true}, {true, false, false, true}
            }),

            Map.entry('I', new boolean[][]{
                    {true, true, true}, {false, true, false}, {false, true, false},
                    {false, true, false}, {false, true, false}, {true, true, true}
            }),

            Map.entry('J', new boolean[][]{
                    {false, false, true, true}, {false, false, false, true}, {false, false, false, true},
                    {false, false, false, true}, {true, false, false, true}, {false, true, true, false}
            }),

            Map.entry('K', new boolean[][]{{true, false}, {false, true}}),

            Map.entry('L', new boolean[][]{
                    {true, false, false, false}, {true, false, false, false}, {true, false, false, false},
                    {true, false, false, false}, {true, false, false, false}, {true, true, true, true}
            }),

            Map.entry('M', new boolean[][]{{true, false}, {false, true}}),

            Map.entry('N', new boolean[][]{{true, false}, {false, true}}),

            Map.entry('O', new boolean[][]{
                    {false, true, true, false}, {true, false, false, true}, {true, false, false, true},
                    {true, false, false, true}, {true, false, false, true}, {false, true, true, false}
            }),

            Map.entry('P', new boolean[][]{
                    {true, true, true, false}, {true, false, false, true}, {true, false, false, true},
                    {true, true, true, false}, {true, false, false, false}, {true, false, false, false}
            }),

            Map.entry('Q', new boolean[][]{{true, false}, {false, true}}),

            Map.entry('R', new boolean[][]{
                    {true, true, true, false}, {true, false, false, true}, {true, false, false, true},
                    {true, true, true, false}, {true, false, true, false}, {true, false, false, true}
            }),

            Map.entry('S', new boolean[][]{{true, false}, {false, true}}),

            Map.entry('T', new boolean[][]{{true, false}, {false, true}}),

            Map.entry('U', new boolean[][]{
                    {true, false, false, true}, {true, false, false, true}, {true, false, false, true},
                    {true, false, false, true}, {true, false, false, true}, {false, true, true, false}
            }),

            Map.entry('V', new boolean[][]{{true, false}, {false, true}}),

            Map.entry('W', new boolean[][]{{true, false}, {false, true}}),

            Map.entry('X', new boolean[][]{{true, false}, {false, true}}),

            Map.entry('Y', new boolean[][]{{true, false}, {false, true}}),

            Map.entry('Z', new boolean[][]{
                    {true, true, true, true}, {false, false, false, true}, {false, false, true, false},
                    {false, true, false, false}, {true, false, false, false}, {true, true, true, true}
            })

    );

    public static String decode(boolean[][] grid) {
        List<boolean[][]> letterGrids = new ArrayList<>();
        int originalGridNumberOfRows = grid.length;
        int originalGridNumberOfColumns = grid[0].length;
        int startColumn = 0;

        while (startColumn < originalGridNumberOfColumns) {

            int indexOfFirstAllBlankColumn = indexOfFirstAllBlankColumn(grid, originalGridNumberOfRows, originalGridNumberOfColumns, startColumn);

            int endColumn = indexOfFirstAllBlankColumn == -1 ? originalGridNumberOfColumns : indexOfFirstAllBlankColumn;

            boolean[][] subgrid = subgrid(grid, originalGridNumberOfRows, startColumn, endColumn);
            letterGrids.add(subgrid);

            startColumn = endColumn + 1;

        }

        letterGrids.removeIf(lg -> Grids.countTrueCells(lg) == 0); // remove blank subgrids
        return letterGrids.stream().map(letterGrid -> String.valueOf(toLetter(letterGrid))).collect(Collectors.joining());
    }

    private static int indexOfFirstAllBlankColumn(boolean[][] grid, int originalGridNumberOfRows, int originalGridNumberOfColumns, int startColumn) {
        for (int columnIndex = startColumn; columnIndex < originalGridNumberOfColumns; columnIndex++) {

            int currentColumn = columnIndex;
            if (IntStream.range(0, originalGridNumberOfRows).noneMatch(row -> grid[row][currentColumn])) {
                return columnIndex;
            }

        }

        return -1;
    }

    private static boolean[][] subgrid(boolean[][] grid, int originalGridNumberOfRows, int startColumnInclusive, int endColumnExclusive) {
        boolean[][] newGrid = new boolean[originalGridNumberOfRows][endColumnExclusive - startColumnInclusive];

        for (int rowIndex = 0; rowIndex < originalGridNumberOfRows; rowIndex++) {
            if (endColumnExclusive - startColumnInclusive >= 0) {
                System.arraycopy(grid[rowIndex], startColumnInclusive, newGrid[rowIndex], 0, endColumnExclusive - startColumnInclusive);
            }
        }

        return newGrid;

    }

    private static char toLetter(boolean[][] letterGrid) {
        for (Map.Entry<Character, boolean[][]> entry : dictionary.entrySet()) {
            if (Objects.deepEquals(letterGrid, entry.getValue())) {
                return entry.getKey();
            }
        }
        return '?';
    }

    public static void print(boolean[][] grid, Consumer<String> singleLinePrinter) {
        print(grid, singleLinePrinter, false);
    }

    public static void printSpaced(boolean[][] grid, Consumer<String> singleLinePrinter) {
        print(grid, singleLinePrinter, true);
    }

    public static void print(boolean[][] grid, Consumer<String> singleLinePrinter, boolean spaced) {
        for (boolean[] row : grid) {
            StringBuilder sb = new StringBuilder();
            for (boolean cell : row) {
                String content = cell ? "#" : ".";
                sb.append(content.repeat(PRINT_WIDTH));
                if (spaced) {
                    sb.append(' ');
                }
            }
            singleLinePrinter.accept(sb.toString());
        }
    }

}
