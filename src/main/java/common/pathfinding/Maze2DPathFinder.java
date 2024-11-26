package common.pathfinding;

import common.geo.Cell2D;
import common.geo.ImmutableCell2D;

import java.util.*;

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

    private int shortestDistanceBFS(ImmutableCell2D start, ImmutableCell2D end) {
        return PathFindingAlgorithms.distanceBFS(start, end, this::getNeighbors);
    }

    @Override
    public Map<Cell2D, Integer> shortestDistances(Cell2D start, Collection<? extends Cell2D> ends) {

        List<ImmutableCell2D> immutableEnds = ends.stream().map(ImmutableCell2D::copyOf).toList();
        return shortestDistances(ImmutableCell2D.copyOf(start), immutableEnds);

    }

    private Map<Cell2D, Integer> shortestDistances(ImmutableCell2D start, Collection<ImmutableCell2D> ends) {
        Map<ImmutableCell2D, Integer> map = PathFindingAlgorithms.distanceBFS(start, ends, this::getNeighbors);
        return new HashMap<>(map);
    }

    @Override
    public List<Cell2D> shortestPath(Cell2D start, Cell2D end) {
        return shortestPath(ImmutableCell2D.copyOf(start), ImmutableCell2D.copyOf(end));
    }

    private List<Cell2D> shortestPath(ImmutableCell2D start, ImmutableCell2D end) {
        List<ImmutableCell2D> l = PathFindingAlgorithms.pathBFS(start, end, this::getNeighbors);
        return new ArrayList<>(l);
    }

    @Override
    protected boolean isValid(int rowIndex, int columnIndex) {
        return super.isValid(rowIndex, columnIndex) && !maze[rowIndex][columnIndex];
    }

    
}
