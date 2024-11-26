package common.pathfinding;

import com.google.common.graph.ValueGraph;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
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

        return PathFindingAlgorithms.distanceDijkstra(start, end, neighborExtractor, costOfMovingExtractor);
    }

    @Override
    public List<N> shortestPath(N start, N end) {

        Function<N, Set<N>> neighborExtractor = graph::adjacentNodes;
        ToIntBiFunction<N, N> costOfMovingExtractor = costOfMovingExtractor();

        return PathFindingAlgorithms.pathDijkstra(start, end, neighborExtractor, costOfMovingExtractor);
    }

    private ToIntBiFunction<N, N> costOfMovingExtractor() {
        return (nodeU, nodeV) -> graph.edgeValue(nodeU, nodeV).orElseThrow();
    }

}
