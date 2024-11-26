package common.pathfinding;

import com.google.common.collect.Table;
import com.google.common.graph.Graph;

import java.util.*;

class UnweightedGraphPathFinder<N> extends BaseGraphPathFinder<N> {
    
    private final Graph<N> graph;
    
    UnweightedGraphPathFinder(Graph<N> graph) {
        this.graph = graph;
    }

    @Override
    protected Set<N> nodes() {
        return graph.nodes();
    }

    @Override
    public int shortestDistance(N start, N end) {

        // BFS
        return PathFindingAlgorithms.distanceBFS(start, end, graph::adjacentNodes);

    }

    @Override
    public Map<N, Integer> shortestDistances(N start) {

        Map<N, Integer> shortestDistances = new HashMap<>();

        Set<N> visited = new HashSet<>();
        Set<N> boundary = new HashSet<>();
        int distance = 0;

        shortestDistances.put(start, 0);
        boundary.add(start);
        visited.add(start);

        while (!boundary.isEmpty()) {

            distance++;

            Set<N> newBoundary = new HashSet<>();
            for (N cellInTheOldBoundary: boundary) {
                Set<N> neighbors = graph.successors(cellInTheOldBoundary);
                neighbors.stream().filter(neighbor -> !visited.contains(neighbor)).forEach(newBoundary::add);
            }

            for (N cellInTheNewBoundary: newBoundary) {
                shortestDistances.put(cellInTheNewBoundary, distance);
            }

            boundary = newBoundary;
            visited.addAll(boundary);

        }

        return shortestDistances;
    }

    @Override
    public Table<N, N, Integer> shortestDistances() {
        /*
            TODO There is probably a better implementation
         */
        return super.shortestDistances();
    }

    @Override
    public List<N> shortestPath(N start, N end) {
        List<N> l = PathFindingAlgorithms.pathBFS(start, end, graph::adjacentNodes);
        return new ArrayList<>(l);
    }

}
