package events.y2024;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import common.stats.AbstractQuest;
import common.support.interfaces.MainEvent2024;
import common.support.interfaces.Quest06;
import common.support.params.ExecutionParameters;

import java.util.*;
import java.util.stream.Collectors;

public class EC2024Quest06 extends AbstractQuest implements MainEvent2024, Quest06 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        List<String> pathWithUniquePathLength = getPathWithUniqueLengthFromInput(inputLines);
        return String.join("", pathWithUniquePathLength);
    }

    private ImmutableGraph<String> createTree(List<String> inputLines) {
        ImmutableGraph.Builder<String> graphBuilder = GraphBuilder.directed().immutable();

        for (String inputLine: inputLines) {

            String[] parts = inputLine.split(":");
            String start = parts[0];
            String[] ends = parts[1].split(",");

            for (String end: ends) {

                if (!end.equals("BUG") && !end.equals("ANT")) {
                    graphBuilder.putEdge(start, end);
                }
            }

        }

        return graphBuilder.build();
    }

    private void addPaths(Graph<String> graph, List<List<String>> paths, Deque<String> pathSoFar, String currentNode) {

        if (!currentNode.equals("BUG") && !currentNode.equals("ANT")) {
            if (currentNode.equals("@")) {
                // This is a fruit. Create the path and add it to the list
                List<String> path = pathSoFar.stream().toList();
                paths.add(path);
            } else {

                // This is an internal node, branch out to the

                Set<String> successors = graph.successors(currentNode);
                for (String successor: successors) {

                    pathSoFar.addLast(successor);
                    addPaths(graph, paths, pathSoFar, successor);
                    pathSoFar.removeLast();

                }

            }
        }

    }

    private List<String> getPathWithUniqueLength(List<List<String>> pathsToFruits) {
        Multiset<Integer> pathLengths = HashMultiset.create();
        Map<Integer, List<String>> pathLengthToPath = new HashMap<>();

        for (List<String> path: pathsToFruits) {
            int length = path.size();
            pathLengths.add(length);
            pathLengthToPath.put(length, path);
        }

        int uniquePathLength = -1;
        for (Multiset.Entry<Integer> entry: pathLengths.entrySet()) {
            if (entry.getCount() == 1) {
                // This is the unique length
                uniquePathLength = entry.getElement();
            }
        }

        return pathLengthToPath.get(uniquePathLength);
    }

    private List<String> getPathWithUniqueLengthFromInput(List<String> inputLines) {
        ImmutableGraph<String> graph = createTree(inputLines);

        Deque<String> pathSoFar = new ArrayDeque<>();
        String root = "RR";
        pathSoFar.addLast(root);
        List<List<String>> pathsToFruits = new ArrayList<>();
        addPaths(graph, pathsToFruits, pathSoFar, root);

        return getPathWithUniqueLength(pathsToFruits);
    }


    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        List<String> pathWithUniqueLength = getPathWithUniqueLengthFromInput(inputLines);
        return pathWithUniqueLength.stream().map(node -> node.substring(0, 1) ).collect(Collectors.joining());
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        List<String> pathWithUniqueLength = getPathWithUniqueLengthFromInput(inputLines);
        return pathWithUniqueLength.stream().map(node -> node.substring(0, 1) ).collect(Collectors.joining());
    }
}
