package common.pathfinding.algorithms;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import common.pathfinding.NeighborProducer;
import common.pathfinding.SimpleNodeWithCost;
import common.pathfinding.StateRelevancePredicate;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class DijkstraAlgorithms {

    public static <N> int minDistance(
            NeighborProducer<N> neighborProducer, StateRelevancePredicate<N> stateRelevancePredicate,
            N start, Predicate<? super N> stoppingCondition) {

        return minimize(
                neighborProducer, stateRelevancePredicate, start, stoppingCondition,
                SimpleNodeWithCost::cost,
                () -> 0, // the distance from the start node to itself if it already satisfies the stopping condition
                () -> -1, // value to be returned if no path is found to nodes satisfying the stopping condition
                (_, _) -> {},
                (_, _) -> {},
                (_, _) -> {}
        );

    }

    public static <N> int minDistance(
            NeighborProducer<N> neighborProducer,
            N start, Predicate<? super N> stoppingCondition) {

        return minDistance(neighborProducer, (_, _, _) -> false, start, stoppingCondition);

    }

    public static <N> int minDistance(NeighborProducer<N> neighborProducer, N start, N end) {
        return minDistance(neighborProducer, start, (Predicate<N>) n -> n.equals(end));
    }

    public static <N> int minDistance(
            Function<N, Set<N>> neighborExtractor, ToIntBiFunction<N, N> costOfMovingExtractor,
            N start, N end) {
        return minDistance(toNeighborProducer(neighborExtractor, costOfMovingExtractor), start, end);
    }

    public static <N> List<N> minPath(NeighborProducer<N> neighborProducer, StateRelevancePredicate<N> stateRelevancePredicate,
                                      N start, Predicate<? super N> stoppingCondition) {

        Map<Object, N> targetToSource = new HashMap<>();

        return minimize(
                neighborProducer,
                stateRelevancePredicate,
                start, stoppingCondition,
                nodeWithCost -> {

                    Object currentNodeObj = nodeWithCost.node();

                    List<N> path = new ArrayList<>();
                    N end = targetToSource.get(currentNodeObj);

                    path.add(end);
                    N currentNodeInThePath = end;

                    while (targetToSource.containsKey(currentNodeInThePath)) {
                        currentNodeInThePath = targetToSource.get(currentNodeInThePath);
                        path.add(currentNodeInThePath);
                    }

                    Collections.reverse(path);
                    return path;

                },
                () -> List.of(start, start),
                List::of,
                (_, _) -> {},
                targetToSource::put,
                targetToSource::put
        );

    }

    public static <N> List<N> minPath(NeighborProducer<N> neighborProducer, N start, Predicate<? super N> stoppingCondition) {
        return minPath(neighborProducer, (_, _, _) -> false, start, stoppingCondition);
    }

    public static <N> List<N> minPath(NeighborProducer<N> neighborProducer, N start, N end) {
        return minPath(neighborProducer, start, (Predicate<N>) n -> n.equals(end));
    }

    public static <N> List<N> minPath(N start, N end,
                                      Function<N, Set<N>> neighborExtractor,
                                      ToIntBiFunction<N, N> costOfMovingExtractor) {
        return minPath(toNeighborProducer(neighborExtractor, costOfMovingExtractor), start, end);
    }

    private static <N> NeighborProducer<N> toNeighborProducer(Function<N, Set<N>> neighborExtractor,
                                                              ToIntBiFunction<N, N> costOfMovingExtractor) {

        return stateWithCost -> {

            N currentState = stateWithCost.node();
            int costToCurrentState = stateWithCost.cost();

            return neighborExtractor.apply(currentState)
                    .stream()
                    .map(neighboringState -> new SimpleNodeWithCost<>(neighboringState,
                            costToCurrentState + costOfMovingExtractor.applyAsInt(currentState, neighboringState)))
                    .collect(Collectors.toSet());
        };
    }

    public static <N> List<List<N>> minPaths(NeighborProducer<N> neighborProducer, StateRelevancePredicate<N> stateRelevancePredicate,
                                             N start, Predicate<? super N> stoppingCondition) {

        Multimap<Object, N> targetToSources = MultimapBuilder.hashKeys().arrayListValues().build();

        return minimize(
                neighborProducer, stateRelevancePredicate, start, stoppingCondition,
                nodeWithCost -> {

                    List<List<N>> paths = new ArrayList<>();

                    Collection<N> finalStates = targetToSources.get(nodeWithCost.node());

                    for (N finalState: finalStates) {

                        List<N> pathSoFar = new ArrayList<>(List.of(finalState));
                        addPaths(start, targetToSources, paths, finalState, pathSoFar);

                    }

                    return paths;

                },
                () -> List.of(List.of(start, start)),
                List::of,
                targetToSources::put,
                (target, source) -> {
                    targetToSources.removeAll(target);
                    targetToSources.put(target, source);
                },
                targetToSources::put
        );

    }

    public static <N> List<List<N>> minPaths(NeighborProducer<N> neighborProducer, N start, Predicate<? super N> stoppingCondition) {
        return minPaths(neighborProducer, (_, _, _) -> false, start, stoppingCondition);
    }

    private static <N> void addPaths(N start, Multimap<Object, N> targetToSources, List<List<N>> allPaths, N currentState, List<N> pathSoFar) {

        if (currentState.equals(start)) {

            Collections.reverse(pathSoFar);

            allPaths.add(pathSoFar);
            return;
        }

        Collection<N> sources = targetToSources.get(currentState);
        int numberOfSources = sources.size();

        while (numberOfSources == 1) {

            N source = sources.iterator().next();
            pathSoFar.add(source);

            if (source.equals(start)) {

                Collections.reverse(pathSoFar);
                allPaths.add(pathSoFar);
                return;
            }

            // Recompute sources
            sources = targetToSources.get(source);
            numberOfSources = sources.size();

        }

        // Several sources
        for (N source: sources) {

            List<N> pathSoFarCopy = new ArrayList<>(pathSoFar);
            pathSoFarCopy.add(source);

            addPaths(start, targetToSources, allPaths, source, pathSoFarCopy);

        }

    }

    private static Comparator<SimpleNodeWithCost<?>> comparatorWithFinalObject(Object finalNode) {
        return (o1, o2) -> {

            int costComparison = SimpleNodeWithCost.CMP.compare(o1, o2);

            if (costComparison != 0) {
                return costComparison;
            }

            Object node1 = o1.node();
            Object node2 = o2.node();

            if (node1 == node2) {
                return 0;
            }

            // The final node is greater than any other node of type N

            if (node1 == finalNode) {
                return 1;
            }

            if (node2 == finalNode) {
                return -1;
            }

            // No need to distinguish the two nodes
            return 0;
        };
    }

    /*
        This is the actual algorithm.
        It finds the minimum value for a particular object T from a starting state to
        an end state that satisfies the given stopping condition. The object T being
        minimized can be the total cost to that end state or the path to that end state
        where two paths are compared according to their total cost.
     */

    private static <N, T> T minimize(
            NeighborProducer<N> neighborProducer, StateRelevancePredicate<N> stateRelevancePredicate,
            N start, Predicate<? super N> stoppingCondition,
            Function<SimpleNodeWithCost<?>, T> standardFinisher,
            Supplier<T> finisherIfStartIsEnd, Supplier<T> finisherIfNoSolutionFound,
            BiConsumer<Object, N> targetAndSourceOperatorIfEquallyGoodPath,
            BiConsumer<Object, N> targetAndSourceOperatorIfBetterPath,
            BiConsumer<Object, N> targetAndSourceOperatorIfNewPath) {

        if (stoppingCondition.test(start)) {
            return finisherIfStartIsEnd.get();
        }

        Object finalNode = new Object();

        Comparator<SimpleNodeWithCost<?>> cmp = comparatorWithFinalObject(finalNode);

        PriorityQueue<SimpleNodeWithCost<?>> priorityQueue = new PriorityQueue<>(cmp);
        priorityQueue.add(new SimpleNodeWithCost<>(start, 0));

        Map<Object, Integer> confirmedMinimalCosts = new HashMap<>();
        confirmedMinimalCosts.put(start, 0);

        while (!priorityQueue.isEmpty()) {

            SimpleNodeWithCost<?> currentNodeObjWithCost = priorityQueue.remove();

            Object currentNodeObj = currentNodeObjWithCost.node();
            int costToCurrentNode = currentNodeObjWithCost.cost();

            if (currentNodeObj == finalNode) {
                return standardFinisher.apply(currentNodeObjWithCost);
            }

            if (confirmedMinimalCosts.containsKey(currentNodeObj) && confirmedMinimalCosts.get(currentNodeObj) < costToCurrentNode) {
                continue;
            }

            @SuppressWarnings("unchecked")
            N node = (N) currentNodeObj;

            @SuppressWarnings("unchecked")
            SimpleNodeWithCost<N> nodeWithCost = (SimpleNodeWithCost<N>) currentNodeObjWithCost;

            //StateRelevancePredicate<N> stateRelevancePredicate = (_, _, _) -> false;

            // If this state (with associated cost) is not relevant, stop processing it

            // Check if this state (with associated cost) is not relevant, because there is a better
            // node with a lower cost that has already been processed

            /*
            if (  confirmedMinimalCosts.entrySet().stream()
                    .filter(previousStateEntry -> previousStateEntry.getKey() != finalNode)
                    .anyMatch(previousStateEntry ->  stateRelevancePredicate.isCurrentStateNotRelevant(nodeWithCost, (N) previousStateEntry.getKey(), previousStateEntry.getValue()))
            ) {
                continue;
            }

             */

            Set<? extends SimpleNodeWithCost<?>> neighbors;
            if (stoppingCondition.test(node)) {
                neighbors = Set.of(new SimpleNodeWithCost<>(finalNode, costToCurrentNode));
            } else {
                neighbors = neighborProducer.produceNeighborsWithCosts(nodeWithCost);
            }

            for (SimpleNodeWithCost<?> neighborWithCost: neighbors) {

                Object neighbor = neighborWithCost.node();
                int totalCostToNeighbor = neighborWithCost.cost();

                if (confirmedMinimalCosts.containsKey(neighbor)) {

                    // Neighbor already reached in another path. Compare costs
                    int costThroughOtherPath = confirmedMinimalCosts.get(neighbor);

                    if (totalCostToNeighbor == costThroughOtherPath) {

                        targetAndSourceOperatorIfEquallyGoodPath.accept(neighbor, node);

                    } else if (totalCostToNeighbor < costThroughOtherPath) {
                        // Found better path. Update map of minimal costs and path to here

                        confirmedMinimalCosts.put(neighbor, totalCostToNeighbor);
                        priorityQueue.add(new SimpleNodeWithCost<>(neighbor, totalCostToNeighbor));

                        targetAndSourceOperatorIfBetterPath.accept(neighbor, node);

                    }

                } else {

                    // This is a new reachable cell

                    priorityQueue.add(new SimpleNodeWithCost<>(neighbor, totalCostToNeighbor));
                    confirmedMinimalCosts.put(neighbor, totalCostToNeighbor);

                    targetAndSourceOperatorIfNewPath.accept(neighbor, node);

                }

            }

        }

        return finisherIfNoSolutionFound.get();

    }

}
