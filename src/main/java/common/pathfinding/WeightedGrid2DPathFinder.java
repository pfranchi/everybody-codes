package common.pathfinding;

import common.geo.Cell2D;
import common.geo.ImmutableCell2D;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntBiFunction;

public class WeightedGrid2DPathFinder extends Cell2DPathFinder {

    private final int[][] grid;

    WeightedGrid2DPathFinder(int[][] grid) {
        super(grid.length, grid[0].length);
        this.grid = grid;
    }

    @Override
    public int shortestDistance(Cell2D start, Cell2D end) {
        return shortestDistanceDijkstra(ImmutableCell2D.copyOf(start), ImmutableCell2D.copyOf(end));
    }

    private int shortestDistanceDijkstra(ImmutableCell2D start, ImmutableCell2D end) {
        return PathFindingAlgorithms.distanceDijkstra(start, end, this::getNeighbors, costOfMovingExtractor());
    }

    @Override
    public List<Cell2D> shortestPath(Cell2D start, Cell2D end) {
        return shortestPath(ImmutableCell2D.copyOf(start), ImmutableCell2D.copyOf(end));
    }

    private List<Cell2D> shortestPath(ImmutableCell2D start, ImmutableCell2D end) {
        List<ImmutableCell2D> l = PathFindingAlgorithms.pathDijkstra(start, end, this::getNeighbors, costOfMovingExtractor());
        return new ArrayList<>(l);
    }

    private ToIntBiFunction<ImmutableCell2D, ImmutableCell2D> costOfMovingExtractor() {
        return (_, cellEnd) -> grid[cellEnd.row()][cellEnd.column()];
    }

}
