package common.pathfinding.algorithms;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class BFSAlgorithms {

    private BFSAlgorithms() {}

    public static <N> int minDistance(Function<N, ? extends Set<? extends N>> neighborExtractor, N start, N end) {

        Predicate<? super N> stoppingCondition = node -> node.equals(end);
        return minDistance(neighborExtractor, start, stoppingCondition);

    }

    public static <N> int minDistance(Function<N, ? extends Set<? extends N>> neighborExtractor, N start, Predicate<? super N> stoppingCondition) {

        /*
            BFS
            We perform the search in waves, for each iteration we expand the current boundary
            to a new boundary consisting of all unvisited nodes reachable from the current boundary.
            These waves have incremental distances from the start.
         */

        if (stoppingCondition.test(start)) {
            return 0;
        }

        Set<N> visited = new HashSet<>();
        Set<N> boundary = new HashSet<>();
        int distance = 0;

        boundary.add(start);
        visited.add(start);

        while (!boundary.isEmpty()) {

            distance++;

            Set<N> newBoundary = new HashSet<>();
            for (N cellInTheBoundary: boundary) {

                Set<? extends N> neighbors = neighborExtractor.apply(cellInTheBoundary);
                for (N neighbor: neighbors) {

                    if (stoppingCondition.test(neighbor)) {
                        return distance; // Exit condition
                    }

                    if (!visited.contains(neighbor)) {
                        newBoundary.add(neighbor);
                    }

                }

            }

            boundary = newBoundary;
            visited.addAll(boundary);

        }

        // End was not reached
        return -1;

    }

    public static <N> int maxDistance(Function<N, Set<? extends N>> neighborExtractor, N start) {

        Set<N> visited = new HashSet<>();
        Set<N> boundary = new HashSet<>();
        int distance = 0;

        boundary.add(start);
        visited.add(start);

        while (!boundary.isEmpty()) {

            Set<N> newBoundary = new HashSet<>();
            for (N cellInTheBoundary: boundary) {

                Set<? extends N> neighbors = neighborExtractor.apply(cellInTheBoundary);

                for (N neighbor: neighbors) {
                    if (!visited.contains(neighbor)) {
                        newBoundary.add(neighbor);
                    }
                }

            }

            boundary = newBoundary;
            visited.addAll(boundary);

            if (!boundary.isEmpty()) {
                distance++;
            }

        }

        return distance;

    }

    public static <N> List<N> minPath(Function<N, Set<N>> neighborExtractor, N start, N end) {

        /*
            BFS
            The algorithm analyzes one cell for each iteration, in the order of their discovery (FIFO).
         */

        if (start.equals(end)) {
            return Arrays.asList(start, end);
        }

        Set<N> visited = new HashSet<>();
        Queue<N> visitQueue = new ArrayDeque<>();
        visited.add(start);
        visitQueue.add(start);

        Map<N, N> targetToSource = new HashMap<>();

        while (!visitQueue.isEmpty()) {

            N currentNode = visitQueue.remove();
            Set<? extends N> neighbors = neighborExtractor.apply(currentNode);

            for (N neighbor: neighbors) {

                if (neighbor.equals(end)) {

                    // Found end. Unroll the path
                    List<N> path = new ArrayList<>();
                    path.add(end);
                    path.add(currentNode);
                    N currentNodeInThePath = currentNode;
                    while (targetToSource.containsKey(currentNodeInThePath)) {
                        currentNodeInThePath = targetToSource.get(currentNodeInThePath);
                        path.add(currentNodeInThePath);
                    }

                    Collections.reverse(path);
                    return path;

                }

                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    visitQueue.add(neighbor);
                    targetToSource.put(neighbor, currentNode);
                }

            }

        }

        return Collections.emptyList();

    }

}
