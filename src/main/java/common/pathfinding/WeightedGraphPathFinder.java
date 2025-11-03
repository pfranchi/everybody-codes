package common.pathfinding;

import com.google.common.graph.ValueGraph;
import common.pathfinding.algorithms.PathFindingAlgorithms;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;

public class WeightedGraphPathFinder<N> extends BaseGraphPathFinder<N> {

    private final ValueGraph<N, Integer> graph;

    public WeightedGraphPathFinder(ValueGraph<N, Integer> graph) {
        this.graph = graph;
    }

    @Override
    protected Set<N> nodes() {
        return graph.nodes();
    }

    @Override
    public int shortestDistance(N start, N end) {

        Function<N, Set<N>> neighborExtractor = graph::adjacentNodes;
        ToIntBiFunction<N, N> costOfMovingExtractor = costOfMovingExtractor();

        return PathFindingAlgorithms.minDistanceVariableCost(neighborExtractor, costOfMovingExtractor, start, end);
    }

    @Override
    public int shortestDistance(N start, Predicate<? super N> stoppingCondition) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<N> shortestPath(N start, N end) {

        Function<N, Set<N>> neighborExtractor = graph::adjacentNodes;
        ToIntBiFunction<N, N> costOfMovingExtractor = costOfMovingExtractor();

        return PathFindingAlgorithms.minPathVariableCost(neighborExtractor, costOfMovingExtractor, start, end);
    }

    private ToIntBiFunction<N, N> costOfMovingExtractor() {
        return (nodeU, nodeV) -> graph.edgeValue(nodeU, nodeV).orElseThrow();
    }

    @Override
    public int maxDistance(N start) {
        throw new UnsupportedOperationException();
    }

}
