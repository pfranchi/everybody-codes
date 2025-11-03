package common.pathfinding;

import com.google.common.collect.Table;
import com.google.common.graph.Graph;
import com.google.common.graph.ValueGraph;
import common.geo.Cell2D;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface PathFinder<N> {

    int shortestDistance(N start, N end);

    int shortestDistance(N start, Predicate<? super N> stoppingCondition);

    Map<N, Integer> shortestDistances(N start);

    Table<N, N, Integer> shortestDistances();

    List<N> shortestPath(N start, N end);

    Map<N, List<N>> shortestPaths(N start);

    Table<N, N, List<N>> shortestPaths();

    int maxDistance(N start);

    static PathFinder<Cell2D> forSimpleMaze(boolean[][] maze) {
        return new Maze2DPathFinder(maze);
    }

    static PathFinder<Cell2D> forWeightedGrid(int[][] grid) {
        return new WeightedGrid2DPathFinder(grid);
    }

    static <N> PathFinder<N> forUnweightedGraph(Graph<N> graph) {
        return new UnweightedGraphPathFinder<>(graph);
    }

    static <N> PathFinder<N> forWeightedGraph(ValueGraph<N, Integer> weightedGraph) {
        return new WeightedGraphPathFinder<>(weightedGraph);
    }

}
