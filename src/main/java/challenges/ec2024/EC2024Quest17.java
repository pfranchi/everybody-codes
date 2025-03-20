package challenges.ec2024;

import challenges.AbstractQuest;
import challenges.interfaces.ECEvent2024;
import challenges.interfaces.Quest17;
import challenges.params.ExecutionParameters;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.graph.*;
import common.Graphs;
import common.geo.ImmutableCoordinate2D;

import java.util.*;

public class EC2024Quest17 extends AbstractQuest implements ECEvent2024, Quest17 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(inputLines);
    }

    private String solve(List<String> inputLines) {
        int fieldHeight = inputLines.size();
        int fieldWidth = inputLines.getFirst().length();

        NavigableSet<ImmutableCoordinate2D> starCoordinates = new TreeSet<>();

        for (int rowIndex = 0; rowIndex < fieldHeight; rowIndex++) {
            char[] chars = inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < fieldWidth; columnIndex++) {

                if (chars[columnIndex] == '*') {

                    int y = fieldHeight - rowIndex;

                    starCoordinates.add(ImmutableCoordinate2D.of( columnIndex + 1 , y));

                }

            }
        }

        int numberOfStars = starCoordinates.size();

        Table<ImmutableCoordinate2D, ImmutableCoordinate2D, Integer> pairwiseDistances = HashBasedTable.create();
        ImmutableValueGraph.Builder<ImmutableCoordinate2D, Integer> graphBuilder = ValueGraphBuilder.undirected().immutable();

        for (ImmutableCoordinate2D star1: starCoordinates) {

            pairwiseDistances.put(star1, star1, 0);
            SortedSet<ImmutableCoordinate2D> tailSet = starCoordinates.tailSet(star1, false);

            for (ImmutableCoordinate2D star2: tailSet) {

                int distance = star1.taxicabDistance(star2);
                pairwiseDistances.put(star1, star2, distance);
                pairwiseDistances.put(star2, star1, distance);

                graphBuilder.putEdgeValue(star1, star2, distance);

            }

        }

        ImmutableValueGraph<ImmutableCoordinate2D, Integer> valueGraph = graphBuilder.build();

        return Integer.toString(sizeOfMinimumSpanningTree(valueGraph));

    }

    private int sizeOfMinimumSpanningTree(ValueGraph<ImmutableCoordinate2D, Integer> valueGraph) {

        Set<ImmutableCoordinate2D> nodes = valueGraph.nodes();
        int numberOfNodes = nodes.size();

        int costOfMinimumSpanningTree = 0;

        ImmutableCoordinate2D startVertex = nodes.iterator().next(); // the first vertex is chosen arbitrarily
        Set<ImmutableCoordinate2D> coveredByMST = new HashSet<>();
        coveredByMST.add(startVertex);

        for (int step = 1; step < numberOfNodes; step++) {

            List<EndpointPair<ImmutableCoordinate2D>> edgesAtBoundary = valueGraph.edges().stream().filter(endpointPair -> {

                ImmutableCoordinate2D star1 = endpointPair.nodeU();
                ImmutableCoordinate2D star2 = endpointPair.nodeV();

                boolean star1IsCovered = coveredByMST.contains(star1);
                boolean star2IsCovered = coveredByMST.contains(star2);

                return star1IsCovered != star2IsCovered;
            }).toList();

            int minEdgeCost = Integer.MAX_VALUE;
            EndpointPair<ImmutableCoordinate2D> edgeWithMinCost = null;

            for (EndpointPair<ImmutableCoordinate2D> edgeAtBoundary: edgesAtBoundary) {
                int edgeCost = valueGraph.edgeValue(edgeAtBoundary).orElseThrow();
                if (edgeCost < minEdgeCost) {
                    minEdgeCost = edgeCost;
                    edgeWithMinCost = edgeAtBoundary;
                }
            }

            // This edge with min cost is added to the MST

            coveredByMST.add(edgeWithMinCost.nodeU());
            coveredByMST.add(edgeWithMinCost.nodeV());

            costOfMinimumSpanningTree += minEdgeCost;

        }

        return numberOfNodes + costOfMinimumSpanningTree;
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(inputLines);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int fieldHeight = inputLines.size();
        int fieldWidth = inputLines.getFirst().length();

        NavigableSet<ImmutableCoordinate2D> starCoordinates = new TreeSet<>();

        for (int rowIndex = 0; rowIndex < fieldHeight; rowIndex++) {
            char[] chars = inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < fieldWidth; columnIndex++) {

                if (chars[columnIndex] == '*') {

                    int y = fieldHeight - rowIndex;

                    starCoordinates.add(ImmutableCoordinate2D.of( columnIndex + 1 , y));

                }

            }
        }

        MutableValueGraph<ImmutableCoordinate2D, Integer> valueGraph = ValueGraphBuilder.undirected().build();
        for (ImmutableCoordinate2D star1: starCoordinates) {
            SortedSet<ImmutableCoordinate2D> tailSet = starCoordinates.tailSet(star1, false);
            for (ImmutableCoordinate2D star2: tailSet) {
                int distance = star1.taxicabDistance(star2);

                if (distance < 6) {
                    // We are only interested in pairs of stars that have a distance less than 6 units
                    valueGraph.putEdgeValue(star1, star2, distance);
                }
            }
        }

        List<List<ImmutableCoordinate2D>> connectedComponents = Graphs.getConnectedComponents(valueGraph);

        List<ConnectedComponent> connectedComponents1 = new ArrayList<>();

        for (List<ImmutableCoordinate2D> nodes: connectedComponents) {

            MutableValueGraph<ImmutableCoordinate2D, Integer> inducedSubgraph = com.google.common.graph.Graphs.inducedSubgraph(valueGraph, nodes);
            int size = sizeOfMinimumSpanningTree(inducedSubgraph);

            connectedComponents1.add(new ConnectedComponent(nodes, size));

        }

        connectedComponents1.sort(Comparator.comparingInt(ConnectedComponent::size).reversed());

        long result = 1L;

        for (int connCompIndex = 0; connCompIndex <= 2; connCompIndex++) {
            result *= connectedComponents1.get(connCompIndex).size();
        }

        return Long.toString(result);
    }

    private record ConnectedComponent(List<ImmutableCoordinate2D> nodes, int size) {}

}
