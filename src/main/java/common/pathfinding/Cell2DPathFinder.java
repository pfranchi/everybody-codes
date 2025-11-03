package common.pathfinding;

import com.google.common.collect.Table;
import common.geo.CardinalDirection2D;
import common.geo.Cell2D;
import common.geo.ImmutableCell2D;

import java.util.*;

abstract class Cell2DPathFinder implements PathFinder<Cell2D> {

    protected final int numberOfRows;
    protected final int numberOfColumns;

    protected Cell2DPathFinder(int numberOfRows, int numberOfColumns) {
        this.numberOfRows = numberOfRows;
        this.numberOfColumns = numberOfColumns;
    }

    @Override
    public int shortestDistance(Cell2D start, Cell2D end) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<Cell2D, Integer> shortestDistances(Cell2D start) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Table<Cell2D, Cell2D, Integer> shortestDistances() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Cell2D> shortestPath(Cell2D start, Cell2D end) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<Cell2D, List<Cell2D>> shortestPaths(Cell2D start) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Table<Cell2D, Cell2D, List<Cell2D>> shortestPaths() {
        throw new UnsupportedOperationException();
    }

    protected Set<ImmutableCell2D> getNeighbors(ImmutableCell2D cell) {
        Set<ImmutableCell2D> neighbors = new LinkedHashSet<>();
        for (CardinalDirection2D direction: CardinalDirection2D.values()) {
            ImmutableCell2D moved = cell.add(direction);
            if (isValid(moved)) {
                neighbors.add(moved);
            }
        }
        return neighbors;
    }

    protected boolean isValid(Cell2D cell) {
        return isValid(cell.getRow(), cell.getColumn());
    }

    protected boolean isValid(int rowIndex, int columnIndex) {
        return isInBounds(rowIndex, columnIndex);
    }

    private boolean isInBounds(int rowIndex, int columnIndex) {
        return 0 <= rowIndex && rowIndex < numberOfRows && 0 <= columnIndex && columnIndex < numberOfColumns;
    }


}
