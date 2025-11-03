package common.pathfinding.algorithms;

import common.pathfinding.NeighborProducer;
import common.pathfinding.SimpleNodeWithCost;
import common.pathfinding.StateRelevancePredicate;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

public final class PathFindingAlgorithms {

    private PathFindingAlgorithms() {}



    // BFS. The cost of moving to a neighbor is always 1.

    public static <N> int minDistanceSimpleCost(Function<N, ? extends Set<? extends N>> neighborExtractor, N start, N end) {
        return BFSAlgorithms.minDistance(neighborExtractor, start, end);
    }

    public static <N> int minDistanceSimpleCost(Function<N, ? extends Set<? extends N>> neighborExtractor, N start, Predicate<? super N> stoppingCondition) {
        return BFSAlgorithms.minDistance(neighborExtractor, start, stoppingCondition);
    }

    public static <N> int maxDistanceSimpleCost(Function<N, Set<? extends N>> neighborExtractor, N start) {
        return BFSAlgorithms.maxDistance(neighborExtractor, start);
    }

    public static <N> List<N> minPathSimpleCost(Function<N, Set<N>> neighborExtractor, N start, N end) {
        return BFSAlgorithms.minPath(neighborExtractor, start, end);
    }



    // DIJKSTRA. Moving to a neighbor has a variable cost.

    public static <N> int minDistanceVariableCost(NeighborProducer<N> neighborProducer, N start, N end) {
        return DijkstraAlgorithms.minDistance(neighborProducer, start, end);
    }

    public static <N> int minDistanceVariableCost(NeighborProducer<N> neighborProducer, N start, Predicate<? super N> stoppingCondition) {
        return DijkstraAlgorithms.minDistance(neighborProducer, start, stoppingCondition);
    }

    public static <N> int minDistanceVariableCost(NeighborProducer<N> neighborProducer, StateRelevancePredicate<N> stateRelevancePredicate,
                                                  N start, Predicate<? super N> stoppingCondition) {
        return DijkstraAlgorithms.minDistance(neighborProducer, stateRelevancePredicate, start, stoppingCondition);
    }

    public static <N> int minDistanceVariableCost(
            Function<N, Set<N>> neighborExtractor, ToIntBiFunction<N, N> costOfMovingExtractor,
            N start, N end) {
        return DijkstraAlgorithms.minDistance(neighborExtractor, costOfMovingExtractor, start, end);
    }

    public static <N> List<N> minPathVariableCost(NeighborProducer<N> neighborProducer, N start, N end) {
        return DijkstraAlgorithms.minPath(neighborProducer, start, end);
    }

    public static <N> List<N> minPathVariableCost(NeighborProducer<N> neighborProducer, N start, Predicate<? super N> stoppingCondition) {
        return DijkstraAlgorithms.minPath(neighborProducer, start, stoppingCondition);
    }

    public static <N> List<N> minPathVariableCost(
            Function<N, Set<N>> neighborExtractor, ToIntBiFunction<N, N> costOfMovingExtractor,
            N start, N end) {
        return DijkstraAlgorithms.minPath(start, end, neighborExtractor, costOfMovingExtractor);
    }

    /*
        Returns all paths that reach the end state which can be reached at the smallest cost.
        More explicitly, for the end state that has the smallest cost (or one of the end states with
        the smallest cost if there are multiple), finds and returns all the paths from the starting
        state to that end state.
     */
    public static <N> List<List<N>> minPaths(NeighborProducer<N> neighborProducer, N start, Predicate<? super N> stoppingCondition) {
        return DijkstraAlgorithms.minPaths(neighborProducer, start, stoppingCondition);
    }



    // A*. Moving to a neighbor has a variable cost. The algorithm is more efficient than Dijkstra's
    // because there is a function that estimates the cost to reach the target

    public static <N> int minDistanceVariableCost(NeighborProducer<N> neighborProducer, N start, N end, ToIntFunction<N> costToTargetEstimator) {

        // TODO
        return 0;

    }

    public static <N> int minDistanceVariableCost(N start, N end, Function<SimpleNodeWithCost<N>, Set<SimpleNodeWithCost<N>>> neighborExtractor, ToIntFunction<N> costToTargetEstimator) {

        // THE COST IN ALL NodeWithCost INSTANCES IS ONLY g(N).
        // THE COMPARISON BETWEEN TWO DIFFERENT NodeWithCost INSTANCES IS DONE BY COMPUTING THE RESPECTIVE VALUES OF f(N) = g(N) + h(N).
        // g(N) is the cost as given by the neighborExtractor, h(N) is the cost given by the costToTargetEstimator.

        if (start.equals(end)) {
            return 0;
        }

        Map<N, Integer> costToTargetCache = new HashMap<>();
        int estimatedCostFromStart = costToTargetEstimator.applyAsInt(start);
        costToTargetCache.put(start, estimatedCostFromStart);

        Comparator<SimpleNodeWithCost<N>> comparator = Comparator.comparingInt(nodeWithCost ->
                nodeWithCost.cost() + getFromCache(costToTargetCache, nodeWithCost.node(), costToTargetEstimator));

        PriorityQueue<SimpleNodeWithCost<N>> priorityQueue = new PriorityQueue<>(comparator);
        priorityQueue.add(new SimpleNodeWithCost<>(start, 0));

        Map<N, Integer> confirmedMinimalCosts = new HashMap<>();
        confirmedMinimalCosts.put(start, 0);

        while (!priorityQueue.isEmpty()) {

            SimpleNodeWithCost<N> currentNodeWithCost = priorityQueue.remove();

            N currentNode = currentNodeWithCost.node();
            int costToCurrentNode = currentNodeWithCost.cost();

            if (currentNode.equals(end)) {
                return costToCurrentNode;
            }

            if (confirmedMinimalCosts.containsKey(currentNode) && confirmedMinimalCosts.get(currentNode) < costToCurrentNode) {
                continue;
            }

            Set<SimpleNodeWithCost<N>> neighbors = neighborExtractor.apply(currentNodeWithCost);

            for (SimpleNodeWithCost<N> neighborWithCost: neighbors) {

                N neighbor = neighborWithCost.node();
                int totalCostToNeighbor = neighborWithCost.cost(); // cost from start to neighbor

                if (confirmedMinimalCosts.containsKey(neighbor)) {

                    // Neighbor already reached in another path. Compare costs
                    int costThroughOtherPath = confirmedMinimalCosts.get(neighbor);

                    if (totalCostToNeighbor < costThroughOtherPath) {
                        // Found better path. Update map of minimal costs and path to here
                        confirmedMinimalCosts.put(neighbor, totalCostToNeighbor);
                        priorityQueue.add(new SimpleNodeWithCost<>(neighbor, totalCostToNeighbor));
                    }

                } else {

                    costToTargetCache.putIfAbsent(neighbor, costToTargetEstimator.applyAsInt(neighbor));

                    priorityQueue.add(new SimpleNodeWithCost<>(neighbor, totalCostToNeighbor));
                    confirmedMinimalCosts.put(neighbor, totalCostToNeighbor);

                }

            }

        }

        return -1;

    }

    public static <N> List<N> minPathVariableCost(N start, N end, Function<SimpleNodeWithCost<N>, Set<SimpleNodeWithCost<N>>> neighborExtractor, ToIntFunction<N> costToTargetEstimator) {

        if (start.equals(end)) {
            return List.of(start, end);
        }

        Map<N, Integer> costToTargetCache = new HashMap<>();
        int estimatedCostFromStart = costToTargetEstimator.applyAsInt(start);
        costToTargetCache.put(start, estimatedCostFromStart);

        Comparator<SimpleNodeWithCost<N>> comparator = Comparator.comparingInt(nodeWithCost ->
                nodeWithCost.cost() + getFromCache(costToTargetCache, nodeWithCost.node(), costToTargetEstimator));

        PriorityQueue<SimpleNodeWithCost<N>> priorityQueue = new PriorityQueue<>(comparator);
        priorityQueue.add(new SimpleNodeWithCost<>(start, 0));

        Map<N, Integer> confirmedMinimalCosts = new HashMap<>();
        confirmedMinimalCosts.put(start, 0);

        Map<N, N> targetToSource = new HashMap<>();

        while (!priorityQueue.isEmpty()) {

            SimpleNodeWithCost<N> currentNodeWithCost = priorityQueue.remove();

            N currentNode = currentNodeWithCost.node();

            int costToCurrentNode = currentNodeWithCost.cost();

            if (currentNode.equals(end)) {

                List<N> path = new ArrayList<>();
                path.add(end);
                N currentNodeInThePath = currentNode;
                while (targetToSource.containsKey(currentNodeInThePath)) {
                    currentNodeInThePath = targetToSource.get(currentNodeInThePath);
                    path.add(currentNodeInThePath);
                }

                Collections.reverse(path);
                return path;

            }

            if (confirmedMinimalCosts.containsKey(currentNode) && confirmedMinimalCosts.get(currentNode) < costToCurrentNode) {
                continue;
            }

            Set<SimpleNodeWithCost<N>> neighbors = neighborExtractor.apply(currentNodeWithCost);

            for (SimpleNodeWithCost<N> neighborWithCost: neighbors) {

                N neighbor = neighborWithCost.node();
                int totalCostToNeighbor = neighborWithCost.cost(); // cost from start to neighbor

                if (confirmedMinimalCosts.containsKey(neighbor)) {

                    // Neighbor already reached in another path. Compare costs
                    int costThroughOtherPath = confirmedMinimalCosts.get(neighbor);

                    if (totalCostToNeighbor < costThroughOtherPath) {
                        // Found better path. Update map of minimal costs and path to here
                        confirmedMinimalCosts.put(neighbor, totalCostToNeighbor);
                        priorityQueue.add(new SimpleNodeWithCost<>(neighbor, totalCostToNeighbor));
                        targetToSource.put(neighbor, currentNode);
                    }

                } else {

                    costToTargetCache.putIfAbsent(neighbor, costToTargetEstimator.applyAsInt(neighbor));

                    priorityQueue.add(new SimpleNodeWithCost<>(neighbor, totalCostToNeighbor));
                    confirmedMinimalCosts.put(neighbor, totalCostToNeighbor);
                    targetToSource.put(neighbor, currentNode);

                }

            }

        }

        return List.of();

    }

    private static <N> int getFromCache(Map<N, Integer> cache, N node, ToIntFunction<N> costFunction) {
        if (cache.containsKey(node)) {
            return cache.get(node);
        }
        int cost = costFunction.applyAsInt(node);
        cache.put(node, cost);
        return cost;
    }

}
