package events.y2026;

import com.google.common.collect.Ordering;
import com.google.common.graph.*;
import common.AbstractQuest;
import common.Sections;
import common.pathfinding.PathFinder;
import common.support.interfaces.MainEvent2026;
import common.support.interfaces.Quest11;
import common.support.params.ExecutionParameters;

import java.util.*;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@SuppressWarnings("UnstableApiUsage")
public class EC2026Quest11 extends AbstractQuest implements MainEvent2026, Quest11 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(input, 1, this::computeStrain);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        ToIntFunction<MutableGraph<Integer>> strainComputer = graph -> {

            int numberOfPortals = graph.nodes().size();
            int minStrain = Integer.MAX_VALUE;

            for (int endpoint1 = 1; endpoint1 < numberOfPortals; endpoint1++) {
                for (int endpoint2 = endpoint1 + 1; endpoint2 <= numberOfPortals; endpoint2++) {

                    if (!graph.hasEdgeConnecting(endpoint1, endpoint2)) {
                        // This is a new edge

                        graph.putEdge(endpoint1, endpoint2);
                        int strain = computeStrain(graph);
                        minStrain = Math.min(minStrain, strain);
                        graph.removeEdge(endpoint1, endpoint2);


                    }

                }
            }

            return minStrain;

        };

        return solve(input, 3, strainComputer);

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Set<NavigableSet<NavigableSet<Integer>>> pairings = new HashSet<>();

        continueRecursion(pairings, 0, new ArrayList<>());

        ToIntFunction<MutableGraph<Integer>> strainComputer = graph -> {

            int numberOfPortals = graph.nodes().size();
            int jump = numberOfPortals / 8;

            return IntStream.range(1, jump + 1)
                    .parallel()
                    .map(startingNode -> minForStartingNode(pairings, jump, graph, startingNode))
                    .min()
                    .orElse(Integer.MAX_VALUE);

        };

        return solve(input, 3, strainComputer);

    }

    private static final Pattern PATTERN = Pattern.compile("^portals: (?<numberOfPortals>\\d+)$");

    private String solve(String input, int neighboringConnectionLimit, ToIntFunction<MutableGraph<Integer>> strainComputer) {

        List<List<String>> sections = Sections.splitAtBlankLines(input);

        String firstLine = sections.getFirst().getFirst();

        Matcher matcher = PATTERN.matcher(firstLine);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid first line");
        }

        int numberOfPortals = Integer.parseInt(matcher.group("numberOfPortals"));

        MutableGraph<Integer> graph = GraphBuilder.undirected().build();

        for (int i = 1; i <= numberOfPortals; i++) {

            for (int delta = 0; delta < neighboringConnectionLimit; delta++) {

                int endpoint2 = ((i + delta) % numberOfPortals) + 1;
                graph.putEdge(i, endpoint2);

            }

        }

        int standardStrain = computeStrain(graph);

        // Add the additional connections
        for (String line : sections.get(1)) {
            String[] parts = line.split(" ");
            int endpoint1 = Integer.parseInt(parts[0]);
            int endpoint2 = Integer.parseInt(parts[1]);
            graph.putEdge(endpoint1, endpoint2);
        }

        int strain = strainComputer.applyAsInt(graph);

        return Integer.toString(standardStrain - strain);

    }

    private int computeStrain(Graph<Integer> graph) {
        PathFinder<Integer> pathFinder = PathFinder.forUnweightedGraph(graph);

        int portalStrain = 0;

        for (int startingNode : graph.nodes()) {
            Map<Integer, Integer> shortestDistances = pathFinder.shortestDistances(startingNode);
            int totalDistance = shortestDistances.values().stream().mapToInt(i -> i).sum();
            portalStrain += totalDistance;

        }

        return portalStrain;
    }

    private void continueRecursion(Set<NavigableSet<NavigableSet<Integer>>> pairings, int depth, List<Integer> valuesInOrderSoFar) {
        if (depth == 4) {
            NavigableSet<Integer> pair1 = new TreeSet<>(Set.of(valuesInOrderSoFar.get(0), valuesInOrderSoFar.get(1)));
            NavigableSet<Integer> pair2 = new TreeSet<>(Set.of(valuesInOrderSoFar.get(2), valuesInOrderSoFar.get(3)));
            NavigableSet<Integer> pair3 = new TreeSet<>(Set.of(valuesInOrderSoFar.get(4), valuesInOrderSoFar.get(5)));
            NavigableSet<Integer> pair4 = new TreeSet<>(Set.of(valuesInOrderSoFar.get(6), valuesInOrderSoFar.get(7)));

            Set<NavigableSet<Integer>> pairs = Set.of(pair1, pair2, pair3, pair4);

            NavigableSet<NavigableSet<Integer>> sortedPairs = new TreeSet<>(Ordering.natural().lexicographical());
            sortedPairs.addAll(pairs);

            pairings.add(sortedPairs);
        }

        for (int i1 = 0; i1 < 7; i1++) {

            if (!valuesInOrderSoFar.contains(i1)) {

                for (int i2 = i1 + 1; i2 < 8; i2++) {

                    if (!valuesInOrderSoFar.contains(i2)) {

                        valuesInOrderSoFar.add(i1);
                        valuesInOrderSoFar.add(i2);

                        continueRecursion(pairings, depth + 1, valuesInOrderSoFar);

                        valuesInOrderSoFar.removeLast();
                        valuesInOrderSoFar.removeLast();

                    }

                }

            }
        }

    }

    private int minForStartingNode(Set<NavigableSet<NavigableSet<Integer>>> pairings, int jump, MutableGraph<Integer> graph, int startingNode) {

        int minStrain = Integer.MAX_VALUE;

        List<Integer> crystalNodes = new ArrayList<>();
        for (int coeff = 0; coeff < 8; coeff++) {
            crystalNodes.add( coeff * jump + startingNode );
        }

        for (Set<NavigableSet<Integer>> pairing: pairings) {

            List<EndpointPair<Integer>> endpointPairs = new  ArrayList<>();

            for (NavigableSet<Integer> pair: pairing) {

                int indexOfFirstCrystal = pair.first();
                int indexOfSecondCrystal = pair.last();

                endpointPairs.add(EndpointPair.unordered(crystalNodes.get(indexOfFirstCrystal), crystalNodes.get(indexOfSecondCrystal)));
            }

            if (endpointPairs.stream().noneMatch(graph::hasEdgeConnecting)) {

                MutableGraph<Integer> copy = Graphs.copyOf(graph);

                // Add all 4 new connections
                endpointPairs.forEach(copy::putEdge);

                // Compute strain
                int strain = computeStrain(copy);
                minStrain = Math.min(minStrain, strain);

            }

        }

        return minStrain;

    }

}
