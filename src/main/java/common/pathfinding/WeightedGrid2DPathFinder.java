package common.pathfinding;

import common.geo.Cell2D;
import common.geo.ImmutableCell2D;
import common.pathfinding.algorithms.PathFindingAlgorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
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

    @Override
    public int shortestDistance(Cell2D start, Predicate<? super Cell2D> stoppingCondition) {

        // TODO
        throw new UnsupportedOperationException();
    }

    private int shortestDistanceDijkstra(ImmutableCell2D start, ImmutableCell2D end) {
        return PathFindingAlgorithms.minDistanceVariableCost(this::getNeighbors, costOfMovingExtractor(), start, end);
    }

    @Override
    public List<Cell2D> shortestPath(Cell2D start, Cell2D end) {
        return shortestPath(ImmutableCell2D.copyOf(start), ImmutableCell2D.copyOf(end));
    }

    private List<Cell2D> shortestPath(ImmutableCell2D start, ImmutableCell2D end) {
        List<ImmutableCell2D> l = PathFindingAlgorithms.minPathVariableCost(this::getNeighbors, costOfMovingExtractor(), start, end);
        return new ArrayList<>(l);
    }

    @Override
    public int maxDistance(Cell2D start) {
        throw new UnsupportedOperationException();
    }

    private ToIntBiFunction<ImmutableCell2D, ImmutableCell2D> costOfMovingExtractor() {
        return (_, cellEnd) -> grid[cellEnd.row()][cellEnd.column()];
    }

}
