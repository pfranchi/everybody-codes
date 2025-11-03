package common.pathfinding;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.*;

abstract class BaseGraphPathFinder<N> implements PathFinder<N> {

    protected abstract Set<N> nodes();

    @Override
    public Map<N, Integer> shortestDistances(N start) {

        // Suboptimal (but working) implementation. Defers to shortestDistance(N, N).
        Map<N, Integer> shortestDistances = new HashMap<>();
        Set<N> nodes = nodes();

        for (N end: nodes) {
            shortestDistances.put(end, shortestDistance(start, end));
        }

        return shortestDistances;
    }

    @Override
    public Table<N, N, Integer> shortestDistances() {

        // Suboptimal (but working) implementation. Defers to shortestDistances(N).
        Table<N, N, Integer> shortestDistances = HashBasedTable.create();
        Set<N> nodes = nodes();

        for (N start: nodes) {

            Map<N, Integer> shortestDistancesFromNode = shortestDistances(start);
            for (Map.Entry<N, Integer> entry: shortestDistancesFromNode.entrySet()) {
                N destination = entry.getKey();
                int distance = entry.getValue();
                shortestDistances.put(start, destination, distance);
            }

        }

        return shortestDistances;
    }

    @Override
    public Map<N, List<N>> shortestPaths(N start) {

        Map<N, List<N>> shortestPaths = new HashMap<>();
        Set<N> nodes = nodes();

        for (N end: nodes) {
            shortestPaths.put(end, shortestPath(start, end));
        }

        return shortestPaths;
    }

    @Override
    public Table<N, N, List<N>> shortestPaths() {

        Table<N, N, List<N>> shortestPaths = HashBasedTable.create();
        Set<N> nodes = nodes();

        for (N start: nodes) {
            Map<N, List<N>> shortestPathsFromNode = shortestPaths(start);
            for (Map.Entry<N, List<N>> entry: shortestPathsFromNode.entrySet()) {
                N destination = entry.getKey();
                List<N> path = entry.getValue();
                shortestPaths.put(start, destination, path);
            }
        }

        return shortestPaths;
    }
}
