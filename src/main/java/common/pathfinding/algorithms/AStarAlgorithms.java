package common.pathfinding.algorithms;

import common.pathfinding.NeighborProducer;
import common.pathfinding.SimpleNodeWithCost;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.*;

public class AStarAlgorithms {

    private AStarAlgorithms() {}

    public static <N> int minDistance(NeighborProducer<N> neighborProducer, ToIntFunction<N> costToTargetEstimator, N start, N end) {
        return -1;
    }

    private static <N, T> T minimize(NeighborProducer<N> neighborProducer, ToIntFunction<N> costToTargetEstimator,
                                     N start, Predicate<? super N> stoppingCondition,
                                     Function<SimpleNodeWithCost<?>, T> standardFinisher,
                                     Supplier<T> finisherIfStartIsEnd,
                                     Supplier<T> finisherIfNoSolutionIsFound,
                                     BiConsumer<Object, N> targetAndSourceOperatorIfEquallyGoodPath,
                                     BiConsumer<Object, N> targetAndSourceOperatorIfBetterPath,
                                     BiConsumer<Object, N> targetAndSourceOperatorIfNewPath) {

        if (stoppingCondition.test(start)) {
            return finisherIfStartIsEnd.get();
        }

        Map<Object, Integer> costToTargetCache = new HashMap<>();
        int estimatedCostFromStart = costToTargetEstimator.applyAsInt(start);
        costToTargetCache.put(start, estimatedCostFromStart);

        Object finalNode = new Object();
        costToTargetCache.put(finalNode, 0);

        return null;

    }

    private static <N> Comparator<SimpleNodeWithCost<?>> comparatorWithFinalObject(ToIntFunction<N> costToTargetEstimator,
                                                                                   Map<N, Integer> costToTargetCache,
                                                                                   Object finalNode) {

        return null;
        /*
        return (o1, o2) -> {

            int costComparison = Comparator.<NodeWithCost<?>>comparingInt(nodeWithCost ->
                    nodeWithCost.cost() + getFromCache(costToTargetCache, nodeWithCost.node(), costToTargetEstimator));

        };

         */
    }

    private static <N> int getFromCache(Map<Object, Integer> cache, Object node, ToIntFunction<N> costFunction) {

        // The final node is present in the cache
        if (cache.containsKey(node)) {
            return cache.get(node);
        }

        @SuppressWarnings("unchecked")
        int cost = costFunction.applyAsInt((N) node);
        cache.put(node, cost);
        return cost;
    }

}
