package events.y2026;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import common.AbstractQuest;
import common.support.interfaces.MainEvent2026;
import common.support.interfaces.Quest02;
import common.support.params.ExecutionParameters;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.ToLongFunction;

@SuppressWarnings("UnstableApiUsage")
public class EC2026Quest02 extends AbstractQuest implements MainEvent2026, Quest02 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(inputLines);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(inputLines);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(inputLines);
    }

    private String solve(List<String> inputLines) {

        ToLongFunction<String> function = this::toSortedLongWithGraph;

        long total = inputLines.stream().mapToLong(function).sum();
        return Long.toString(total);
    }

    private record Pair(int smaller, int greater) {}

    private long toSortedLongWithComparator(String line) {

        String[] parts = line.split(" ");

        Set<Pair> originalSet = new HashSet<>();
        Set<Integer> distinctValues = new HashSet<>();

        for (String part: parts) {

            int smaller;
            int greater;

            if (part.contains("<")) {
                String[] components = part.split("<");
                smaller = Integer.parseInt(components[0]);
                greater = Integer.parseInt(components[1]);
            } else if (part.contains(">")) {
                String[] components = part.split(">");
                smaller = Integer.parseInt(components[1]);
                greater = Integer.parseInt(components[0]);
            } else {
                throw new IllegalArgumentException("Invalid input");
            }

            originalSet.add(new Pair(smaller, greater));

            distinctValues.add(smaller);
            distinctValues.add(greater);

        }

        Set<Pair> set = toFullSet(originalSet);

        Comparator<Integer> cmp = (o1, o2) -> {

            if (o1.equals(o2)) {
                throw new IllegalStateException("Cannot compare equal values");
            }
            return set.contains(new Pair(o1, o2)) ? 1 : -1;

        };

        List<Integer> sorted = new ArrayList<>(distinctValues);
        sorted.sort(cmp);

        return toLong(sorted);

    }

    private Set<Pair> toFullSet(Set<Pair> originalSet) {
        boolean anyChange = true;

        Set<Pair> currentSet = originalSet;

        while (anyChange) {
            anyChange = false;

            Set<Pair> newSet = new HashSet<>(currentSet);

            for (Pair pair: currentSet) {
                for (Pair otherPair: currentSet) {
                    if (pair.smaller == otherPair.greater()) {
                        newSet.add(new Pair(otherPair.smaller(), pair.greater));
                    }

                    if (pair.greater == otherPair.smaller()) {
                        newSet.add(new Pair(pair.smaller, otherPair.greater()));
                    }
                }
            }

            if (newSet.size() != currentSet.size()) {
                anyChange = true;
            }

            currentSet = newSet;

        }

        return currentSet;
    }

    private static long toLong(Collection<Integer> digits) {
        long res = 0;
        for (int digit : digits) {
            res = res * 10 + digit;
        }
        return res;
    }

    private record NodeWithDegrees(int node, int outDegree, int inDegree) implements Comparable<NodeWithDegrees> {
        @Override
        public int compareTo(@Nonnull NodeWithDegrees o) {
            return Comparator.comparingInt(NodeWithDegrees::inDegree).compare(this, o);
        }
    }

    private long toSortedLongWithGraph(String line) {

        String[] parts = line.split(" ");

        MutableGraph<Integer> graph = GraphBuilder.directed().build();

        for (String part: parts) {

            int smaller;
            int greater;

            if (part.contains("<")) {
                String[] components = part.split("<");
                smaller = Integer.parseInt(components[0]);
                greater = Integer.parseInt(components[1]);
            } else if (part.contains(">")) {
                String[] components = part.split(">");
                smaller = Integer.parseInt(components[1]);
                greater = Integer.parseInt(components[0]);
            } else {
                throw new IllegalArgumentException("Invalid input");
            }

            graph.putEdge(greater, smaller);

        }

        // Complete the graph by transitively adding the missing edges

        boolean anyChange;

        do {

            anyChange = false;

            for (int node : graph.nodes()) {
                Set<Integer> firstGenerationSuccessors = new HashSet<>(graph.successors(node));

                for (int firstGenerationSuccessor : firstGenerationSuccessors) {
                    Set<Integer> secondGenerationSuccessors = new HashSet<>(graph.successors(firstGenerationSuccessor));
                    for (int secondGenerationSuccessor : secondGenerationSuccessors) {
                        anyChange |= graph.putEdge(node, secondGenerationSuccessor);
                    }
                }

            }

        } while (anyChange);

        // Sort the nodes by their indegrees (smaller indegree comes first)

        NavigableSet<NodeWithDegrees> sortedNodesWithDegrees = new TreeSet<>();

        for (int node: graph.nodes()) {
            int outDegree = graph.outDegree(node);
            int inDegree = graph.inDegree(node);
            sortedNodesWithDegrees.add(new NodeWithDegrees(node, outDegree, inDegree));
        }

        List<Integer> sortedNodes = sortedNodesWithDegrees.stream().map(NodeWithDegrees::node).toList();

        return toLong(sortedNodes);

    }

    private long toSortedLongWithPath(String line) {
        // TODO
        /*
            Build the directed graph and find the longest path. Read the path in order to get the result
         */
        throw new UnsupportedOperationException("Not implemented");
    }

}
