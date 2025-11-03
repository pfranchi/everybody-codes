package common.pathfinding;

import common.geo.Cell2D;
import common.geo.ImmutableCell2D;
import common.pathfinding.algorithms.PathFindingAlgorithms;

import java.util.*;
import java.util.function.Predicate;

class Maze2DPathFinder extends Cell2DPathFinder {

    private final boolean[][] maze;

    Maze2DPathFinder(boolean[][] maze) {
        super(maze.length, maze[0].length);
        this.maze = maze;
    }

    @Override
    public int shortestDistance(Cell2D start, Cell2D end) {
        return shortestDistanceBFS(ImmutableCell2D.copyOf(start), ImmutableCell2D.copyOf(end));
    }

    @Override
    public int shortestDistance(Cell2D start, Predicate<? super Cell2D> stoppingCondition) {
        return shortestDistanceBFS(ImmutableCell2D.copyOf(start), stoppingCondition);
    }

    private int shortestDistanceBFS(ImmutableCell2D start, ImmutableCell2D end) {
        return PathFindingAlgorithms.minDistanceSimpleCost(this::getNeighbors, start, end);
    }

    private int shortestDistanceBFS(ImmutableCell2D start, Predicate<? super ImmutableCell2D> stoppingCondition) {
        return PathFindingAlgorithms.minDistanceSimpleCost(this::getNeighbors, start, stoppingCondition);
    }

    @Override
    public List<Cell2D> shortestPath(Cell2D start, Cell2D end) {
        return shortestPath(ImmutableCell2D.copyOf(start), ImmutableCell2D.copyOf(end));
    }

    private List<Cell2D> shortestPath(ImmutableCell2D start, ImmutableCell2D end) {
        List<ImmutableCell2D> l = PathFindingAlgorithms.minPathSimpleCost(this::getNeighbors, start, end);
        return new ArrayList<>(l);
    }

    @Override
    public int maxDistance(Cell2D start) {
        return PathFindingAlgorithms.maxDistanceSimpleCost(this::getNeighbors, ImmutableCell2D.copyOf(start));
    }

    @Override
    protected boolean isValid(int rowIndex, int columnIndex) {
        return super.isValid(rowIndex, columnIndex) && !maze[rowIndex][columnIndex];
    }

    
}
