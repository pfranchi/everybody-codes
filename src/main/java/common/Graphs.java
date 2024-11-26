package common;

import com.google.common.graph.Graph;
import com.google.common.graph.Traverser;

import java.util.HashSet;
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

}
