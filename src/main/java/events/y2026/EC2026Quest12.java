package events.y2026;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import common.AbstractQuest;
import common.Graphs;
import common.geo.CardinalDirection2D;
import common.geo.ImmutableCell2D;
import common.geo.PrincipalDirection2D;
import common.grids.Grids;
import common.stats.Cell2DMinMaxStatistics;
import common.support.interfaces.MainEvent2026;
import common.support.interfaces.Quest12;
import common.support.params.ExecutionParameters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class EC2026Quest12 extends AbstractQuest implements MainEvent2026, Quest12 {

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

        ImmutableGraph.Builder<ImmutableCell2D> graphBuilder = GraphBuilder.undirected().immutable();

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        boolean[][] grid = new boolean[numberOfRows][numberOfColumns];

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] row =  inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                if (row[columnIndex] == '#') {
                    grid[rowIndex][columnIndex] = true;
                }
            }
        }

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                if (grid[rowIndex][columnIndex]) {

                    ImmutableCell2D cell = ImmutableCell2D.of(rowIndex, columnIndex);
                    graphBuilder.addNode(cell); // Possibly a 1x1 rectangle
                    for (CardinalDirection2D direction: CardinalDirection2D.values()) {
                        ImmutableCell2D neighbor = cell.add(direction);
                        if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, neighbor) && grid[neighbor.getRow()][neighbor.getColumn()]) {
                            graphBuilder.putEdge(cell, neighbor);
                        }
                    }

                }
            }
        }

        List<List<ImmutableCell2D>> connectedComponents = Graphs.getConnectedComponents(graphBuilder.build());

        int total = connectedComponents
                .stream()
                .mapToInt(connectedComponent -> minNumberOfSpells(new HashSet<>(connectedComponent)))
                .sum();

        return Integer.toString(total);

    }

    private int minNumberOfSpells(Set<ImmutableCell2D> block) {

        Cell2DMinMaxStatistics stats = new Cell2DMinMaxStatistics();
        block.forEach(stats);

        int minRow = stats.getRowStatistics().getMin();
        int maxRow = stats.getRowStatistics().getMax();
        int minColumn = stats.getColumnStatistics().getMin();
        int maxColumn = stats.getColumnStatistics().getMax();

        ImmutableCell2D topLeft = ImmutableCell2D.of(minRow, minColumn);
        ImmutableCell2D topRight = ImmutableCell2D.of(minRow, maxColumn);
        ImmutableCell2D bottomLeft = ImmutableCell2D.of(maxRow, minColumn);
        ImmutableCell2D bottomRight =  ImmutableCell2D.of(maxRow, maxColumn);

        int rectangleHeight = maxRow - minRow + 1;
        int rectangleWidth = maxColumn - minColumn + 1;

        List<ImmutableCell2D> corners = List.of(topLeft, topRight, bottomLeft, bottomRight);

        if (block.containsAll(corners)) {

            // It's a rectangle, possibly with a gap

            boolean hasGap = false;
            Cell2DMinMaxStatistics gapStats = new Cell2DMinMaxStatistics();

            for (int rowIndex = minRow; rowIndex <= maxRow; rowIndex++) {
                for (int columnIndex = minColumn; columnIndex <= maxColumn; columnIndex++) {
                    ImmutableCell2D cell = ImmutableCell2D.of(rowIndex, columnIndex);

                    if (!block.contains(cell)) {
                        hasGap = true;
                        gapStats.accept(cell);
                    }
                }
            }

            if (hasGap) {

                int gapWidth = gapStats.getColumnStatistics().getMax() - gapStats.getColumnStatistics().getMin() + 1;
                int gapHeight = gapStats.getRowStatistics().getMax() - gapStats.getRowStatistics().getMin() + 1;

                int numberOfFullRowsAboveGap = gapStats.getRowStatistics().getMin() - minRow;
                int numberOfFullRowsBelowGap = maxRow - gapStats.getRowStatistics().getMax();
                int numberOfFullColumnsLeft = gapStats.getColumnStatistics().getMin() - minColumn;
                int numberOfFullColumnsRight = maxColumn - gapStats.getColumnStatistics().getMax();

                int minForLeftRectangle = Integer.min(numberOfFullColumnsLeft, gapHeight);
                int minForRightRectangle = Integer.min(numberOfFullColumnsRight, gapHeight);
                int minNumberOfSpellsRemovingFullRowsFirst = numberOfFullRowsAboveGap + numberOfFullRowsBelowGap + minForLeftRectangle + minForRightRectangle;

                int minForTopRectangle = Integer.min(numberOfFullRowsAboveGap, gapWidth);
                int minForBottomRectangle = Integer.min(numberOfFullRowsBelowGap, gapWidth);
                int minNumberOfSpellsRemovingFullColumnsFirst = numberOfFullColumnsLeft + numberOfFullColumnsRight + minForTopRectangle + minForBottomRectangle;

                return Integer.min(minNumberOfSpellsRemovingFullRowsFirst, minNumberOfSpellsRemovingFullColumnsFirst);

            }

            // Regular rectangle
            return Integer.min(rectangleHeight, rectangleWidth);

        } else {

            // It's an L-shaped block

            ImmutableCell2D missingCorner;
            PrincipalDirection2D directionsToMoveTowardsTheBlock;
            if (!block.contains(topLeft)) {
                missingCorner = topLeft;
                directionsToMoveTowardsTheBlock = PrincipalDirection2D.SOUTH_EAST;
            } else if (!block.contains(topRight)) {
                missingCorner = topRight;
                directionsToMoveTowardsTheBlock =  PrincipalDirection2D.SOUTH_WEST;
            } else if (!block.contains(bottomLeft)) {
                missingCorner = bottomLeft;
                directionsToMoveTowardsTheBlock = PrincipalDirection2D.NORTH_EAST;
            } else if (!block.contains(bottomRight)) {
                missingCorner = bottomRight;
                directionsToMoveTowardsTheBlock = PrincipalDirection2D.NORTH_WEST;
            } else {
                throw new IllegalStateException();
            }

            // Move horizontally to find the width of the missing rectangle
            int columnDelta = directionsToMoveTowardsTheBlock.getUnitVectorCell().getColumn(); // either +1 or -1
            int rowIndex = missingCorner.getRow();
            int columnIndex = missingCorner.getColumn();
            int delta = 0;

            while (!block.contains(ImmutableCell2D.of(rowIndex, columnIndex + delta))) {
                delta += columnDelta;
            }

            int blankRectangleWidth = Math.abs(delta);

            // Move vertically from the missing corner to find the height of the blank rectangle
            int rowDelta = directionsToMoveTowardsTheBlock.getUnitVectorCell().getRow();
            delta = 0;

            while (!block.contains(ImmutableCell2D.of(rowIndex + delta, columnIndex))) {
                delta += rowDelta;
            }

            int blankRectangleHeight = Math.abs(delta);

            int smallWidthOfTheBlock = rectangleWidth - blankRectangleWidth;
            int smallHeightOfTheBlock = rectangleHeight - blankRectangleHeight;

            int minNumberOfSpells = smallWidthOfTheBlock + smallHeightOfTheBlock;

            return Integer.min(minNumberOfSpells, Integer.min(rectangleHeight, rectangleWidth));

        }

    }

}
