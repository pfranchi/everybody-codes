package common;

import com.google.common.collect.Lists;
import com.google.common.graph.Graph;
import com.google.common.graph.Traverser;
import com.google.common.graph.ValueGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Graphs {

    private Graphs() {}

    public static <N> int numberOfConnectedComponents(Graph<N> graph) {
        Set<N> unvisitedNodes = new HashSet<>(graph.nodes());
        int connectedComponents = 0;
        while (!unvisitedNodes.isEmpty()) {
            N unvisitedNode = unvisitedNodes.iterator().next();
            Iterable<N> reachableNodes = Traverser.forGraph(graph).breadthFirst(unvisitedNode);
            for (N reachableNode: reachableNodes) {
                unvisitedNodes.remove(reachableNode);
            }
            connectedComponents++;
        }
        return connectedComponents;
    }

    public static <N> int numberOfConnectedComponents(ValueGraph<N, ?> valueGraph) {
        Set<N> unvisitedNodes = new HashSet<>(valueGraph.nodes());
        int connectedComponents = 0;
        while (!unvisitedNodes.isEmpty()) {
            N unvisitedNode = unvisitedNodes.iterator().next();
            Iterable<N> reachableNodes = Traverser.forGraph(valueGraph).breadthFirst(unvisitedNode);
            for (N reachableNode : reachableNodes) {
                unvisitedNodes.remove(reachableNode);
            }
            connectedComponents++;
        }
        return connectedComponents;
    }

    public static <N> List<List<N>> getConnectedComponents(ValueGraph<N, ?> valueGraph) {
        List<List<N>> connectedComponents = new ArrayList<>();

        Set<N> unvisitedNodes = new HashSet<>(valueGraph.nodes());

        while (!unvisitedNodes.isEmpty()) {

            N unvisitedNode = unvisitedNodes.iterator().next();
            Iterable<N> reachableNodes = Traverser.forGraph(valueGraph).breadthFirst(unvisitedNode);
            List<N> connectedComponent = Lists.newArrayList(reachableNodes);
            connectedComponent.forEach(unvisitedNodes::remove);
            connectedComponents.add(connectedComponent);

        }

        return connectedComponents;

    }


}
