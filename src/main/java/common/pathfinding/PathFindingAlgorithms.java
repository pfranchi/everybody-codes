package common.pathfinding;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;

public final class PathFindingAlgorithms {

    private PathFindingAlgorithms() {}

    public static <N> int distanceBFS(N start, N end, Function<N, Set<? extends N>> neighborExtractor) {

        /*
            BFS
            The search is performed in waves, for each iteration the current boundary is expanded
            to a new boundary consisting of all unvisited nodes reachable from the current boundary.
            These waves have incremental distances from the start.
         */

        if (start.equals(end)) {
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

                    if (neighbor.equals(end)) {
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

    public static <N> Map<N, Integer> distanceBFS(N start, Collection<? extends N> ends, Function<N, Set<N>> neighborExtractor) {

        if (ends.isEmpty()) {
            return Collections.emptyMap();
        }

        Set<N> distinctUnvisitedEnds = new HashSet<>(ends);

        Set<N> visited = new HashSet<>();
        Set<N> boundary = new HashSet<>();
        int distance = 0;

        boundary.add(start);
        visited.add(start);

        Map<N, Integer> distancesToEnds = new HashMap<>();

        if (distinctUnvisitedEnds.contains(start)) {
            distancesToEnds.put(start, 0);
            distinctUnvisitedEnds.remove(start);

            if (distinctUnvisitedEnds.isEmpty()) {
                return distancesToEnds;
            }
        }

        while (!boundary.isEmpty() && !distinctUnvisitedEnds.isEmpty()) {

            distance++;

            Set<N> newBoundary = new HashSet<>();
            for (N cellInTheBoundary: boundary) {

                Set<? extends N> neighbors = neighborExtractor.apply(cellInTheBoundary);
                for (N neighbor: neighbors) {

                    if (distinctUnvisitedEnds.contains(neighbor)) {

                        distancesToEnds.put(neighbor, distance);
                        distinctUnvisitedEnds.remove(neighbor);

                    }

                    if (!visited.contains(neighbor)) {
                        newBoundary.add(neighbor);
                    }

                }

            }

            boundary = newBoundary;
            visited.addAll(boundary);

        }

        // Add distance of -1 to all non-reachable ends
        for (N unreachableEnd: distinctUnvisitedEnds) {
            distancesToEnds.put(unreachableEnd, -1);
        }

        return distancesToEnds;

    }

    public static <N extends Comparable<N>> int distanceDijkstra(N start, N end, Function<N, Set<N>> neighborExtractor, ToIntBiFunction<N, N> costOfMovingExtractor) {

        if (start.equals(end)) {
            return 0;
        }

        PriorityQueue<ReachableCell<N>> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(new ReachableCell<>(0, start));

        Map<N, Integer> confirmedMinimalCosts = new HashMap<>();
        confirmedMinimalCosts.put(start, 0);

        while (!priorityQueue.isEmpty()) {

            ReachableCell<N> reachableCell = priorityQueue.remove();

            N currentCell = reachableCell.node();
            int costToCurrentCell = reachableCell.cost();

            if (currentCell.equals(end)) {
                // Exit condition
                return costToCurrentCell;
            }

            if (confirmedMinimalCosts.containsKey(currentCell) && confirmedMinimalCosts.get(currentCell) < costToCurrentCell) {
                continue; // We already found a path that reaches this cell with a lower cost
            }

            Set<? extends N> neighbors = neighborExtractor.apply(currentCell);

            for (N neighbor: neighbors) {

                int costToEnterNeighbor = costOfMovingExtractor.applyAsInt(currentCell, neighbor);
                int totalCostToNeighbor = costToCurrentCell + costToEnterNeighbor;

                if (confirmedMinimalCosts.containsKey(neighbor)) {

                    // Neighbor already reached in another path. Compare costs
                    int costThroughOtherPath = confirmedMinimalCosts.get(neighbor);

                    if (totalCostToNeighbor < costThroughOtherPath) {
                        // Found better path. Update map of minimal costs and path to here
                        confirmedMinimalCosts.put(neighbor, totalCostToNeighbor);
                        priorityQueue.add(new ReachableCell<>(totalCostToNeighbor, neighbor)); // Neighbor is readded to the queue with a lower cost (= higher priority)
                    }

                } else {

                    // This is a new reachable cell
                    priorityQueue.add(new ReachableCell<>(totalCostToNeighbor, neighbor));
                    confirmedMinimalCosts.put(neighbor, totalCostToNeighbor);

                }

            }

        }

        return -1;
    }

    private record ReachableCell<N extends Comparable<N>>(int cost, N node) implements Comparable<ReachableCell<N>> {
        @Override
        public int compareTo(@Nonnull ReachableCell<N> o) {
            return Comparator
                    .<ReachableCell<N>>comparingInt(ReachableCell::cost)
                    .thenComparing(ReachableCell::node)
                    .compare(this, o);
        }
    }

    public static <N> int distanceDijkstra(N start, N end, Function<N, Set<N>> neighborExtractor, ToIntBiFunction<N, N> costOfMovingExtractor) {

        if (start.equals(end)) {
            return 0;
        }

        /*
            It is not necessary to store the set of fully processed cells in a separate object.
            However, it is possible to know exactly the set of already fully processed cells: a cell is
            fully processed if it is in the map of confirmed costs and not in the set of reachable cells.
         */

        Set<N> reachable = new HashSet<>();
        reachable.add(start);

        Map<N, Integer> confirmedMinimalCosts = new HashMap<>();
        confirmedMinimalCosts.put(start, 0);

        while (!reachable.isEmpty()) {

            N currentCell = cellWithMinimalConfirmedCost(reachable, confirmedMinimalCosts);
            int costToCurrentCell = confirmedMinimalCosts.get(currentCell);

            if (currentCell.equals(end)) {
                return costToCurrentCell;
            }

            reachable.remove(currentCell);

            Set<? extends N> neighbors = neighborExtractor.apply(currentCell);

            for (N neighbor: neighbors) {

                int costToEnterNeighbor = costOfMovingExtractor.applyAsInt(currentCell, neighbor);
                int totalCostToNeighbor = costToCurrentCell + costToEnterNeighbor;

                /*
                    Only the logic of updating the value associated to neighbor in confirmedMinimalCosts could
                    be written as confirmedMinimalCosts.merge(neighbor, totalCostToNeighbor, Integer::min)

                    The logic of adding neighbor to the set of reachable cells needs to be done regardless.
                 */

                if (confirmedMinimalCosts.containsKey(neighbor)) {

                    // Neighbor already reached in another path. Compare costs
                    int costThroughOtherPath = confirmedMinimalCosts.get(neighbor);

                    if (totalCostToNeighbor < costThroughOtherPath) {
                        // Found better path. Update map of minimal costs and path to here
                        confirmedMinimalCosts.put(neighbor, totalCostToNeighbor);
                    }

                } else {

                    // This is a new reachable cell
                    reachable.add(neighbor);
                    confirmedMinimalCosts.put(neighbor, totalCostToNeighbor);

                }

            }

        }

        // End was not reached
        return -1;
    }

    private static <N> N cellWithMinimalConfirmedCost(Set<N> reachable, Map<N, Integer> confirmedCosts) {

        Iterator<N> reachableItr = reachable.iterator();
        N cellWithMinimalCost = reachableItr.next();
        int currentMinimalCost = confirmedCosts.get(cellWithMinimalCost);

        while (reachableItr.hasNext()) {

            N otherCell = reachableItr.next();
            int otherCost = confirmedCosts.get(otherCell);
            if (otherCost < currentMinimalCost) {
                currentMinimalCost = otherCost;
                cellWithMinimalCost = otherCell;
            }

        }

        return cellWithMinimalCost;

    }

    public static <N> List<N> pathBFS(N start, N end, Function<N, Set<N>> neighborExtractor) {

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

    public static <N extends Comparable<N>> List<N> pathDijkstra(N start, N end,
                                                                 Function<N, Set<N>> neighborExtractor,
                                                                 ToIntBiFunction<N, N> costOfMovingExtractor) {

        if (start.equals(end)) {
            return Arrays.asList(start, end);
        }

        PriorityQueue<ReachableCell<N>> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(new ReachableCell<>(0, start));

        Map<N, Integer> confirmedMinimalCosts = new HashMap<>();
        confirmedMinimalCosts.put(start, 0);

        Map<N, N> targetToSource = new HashMap<>();

        while (!priorityQueue.isEmpty()) {

            ReachableCell<N> reachableCell = priorityQueue.remove();

            N currentCell = reachableCell.node();
            int costToCurrentCell = reachableCell.cost();

            if (currentCell.equals(end)) {
                // Found end, unroll the path

                List<N> path = new ArrayList<>();
                path.add(end);
                N currentNodeInThePath = currentCell;
                while (targetToSource.containsKey(currentNodeInThePath)) {
                    currentNodeInThePath = targetToSource.get(currentNodeInThePath);
                    path.add(currentNodeInThePath);
                }

                Collections.reverse(path);
                return path;
            }

            Set<? extends N> neighbors = neighborExtractor.apply(currentCell);

            for (N neighbor: neighbors) {

                int costToEnterNeighbor = costOfMovingExtractor.applyAsInt(currentCell, neighbor);
                int totalCostToNeighbor = costToCurrentCell + costToEnterNeighbor;

                if (confirmedMinimalCosts.containsKey(neighbor)) {

                    // Neighbor already reached in another path. Compare costs
                    int costThroughOtherPath = confirmedMinimalCosts.get(neighbor);

                    if (totalCostToNeighbor < costThroughOtherPath) {
                        // Found better path. Update map of minimal costs and path to here
                        confirmedMinimalCosts.put(neighbor, totalCostToNeighbor);
                        targetToSource.put(neighbor, currentCell);
                    }

                } else {

                    // This is a new reachable cell
                    priorityQueue.add(new ReachableCell<>(totalCostToNeighbor, neighbor));
                    confirmedMinimalCosts.put(neighbor, totalCostToNeighbor);
                    targetToSource.put(neighbor, currentCell);

                }

            }

        }

        return Collections.emptyList();

    }

    public static <N> List<N> pathDijkstra(N start, N end,
                                           Function<N, Set<N>> neighborExtractor,
                                           ToIntBiFunction<N, N> costOfMovingExtractor) {

        if (start.equals(end)) {
            return Arrays.asList(start, end);
        }

        Set<N> reachable = new HashSet<>();
        reachable.add(start);

        Map<N, Integer> confirmedMinimalCosts = new HashMap<>();
        confirmedMinimalCosts.put(start, 0);

        Map<N, N> targetToSource = new HashMap<>();

        while (!reachable.isEmpty()) {

            N currentCell = cellWithMinimalConfirmedCost(reachable, confirmedMinimalCosts);
            int costToCurrentCell = confirmedMinimalCosts.get(currentCell);

            if (currentCell.equals(end)) {

                // Found end, unroll the path

                List<N> path = new ArrayList<>();
                path.add(end);
                N currentNodeInThePath = currentCell;
                while (targetToSource.containsKey(currentNodeInThePath)) {
                    currentNodeInThePath = targetToSource.get(currentNodeInThePath);
                    path.add(currentNodeInThePath);
                }

                Collections.reverse(path);
                return path;

            }

            reachable.remove(currentCell);

            Set<N> neighbors = neighborExtractor.apply(currentCell);

            for (N neighbor: neighbors) {

                int costToEnterNeighbor = costOfMovingExtractor.applyAsInt(currentCell, neighbor);
                int totalCostToNeighbor = costToCurrentCell + costToEnterNeighbor;

                if (confirmedMinimalCosts.containsKey(neighbor)) {

                    // Neighbor already reached in another path. Compare costs
                    int costThroughOtherPath = confirmedMinimalCosts.get(neighbor);

                    if (totalCostToNeighbor < costThroughOtherPath) {
                        // Found better path. Update map of minimal costs and path to here
                        confirmedMinimalCosts.put(neighbor, totalCostToNeighbor);
                        targetToSource.put(neighbor, currentCell);
                    }

                } else {

                    // First time reaching this cell
                    reachable.add(neighbor);
                    confirmedMinimalCosts.put(neighbor, totalCostToNeighbor);
                    targetToSource.put(neighbor, currentCell);

                }

            }

        }

        return Collections.emptyList();

    }

}
