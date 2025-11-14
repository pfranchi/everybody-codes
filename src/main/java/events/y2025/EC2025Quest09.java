package events.y2025;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import common.AbstractQuest;
import common.Graphs;
import common.support.interfaces.MainEvent2025;
import common.support.interfaces.Quest09;
import common.support.params.ExecutionParameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class EC2025Quest09 extends AbstractQuest implements MainEvent2025, Quest09 {

    private record DNASequence(int id, String sequence) {
    }

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        List<DNASequence> dnaSequences = createSequences(inputLines);
        int numberOfSequences = 3;
        int length = dnaSequences.getFirst().sequence().length();

        for (int childIndex = 0; childIndex < numberOfSequences; childIndex++) {

            DNASequence child = dnaSequences.get(childIndex);

            DNASequence parent1 = dnaSequences.get((childIndex + 1) % numberOfSequences);
            DNASequence parent2 = dnaSequences.get((childIndex + 2) % numberOfSequences);

            if (isChild(length, child, parent1, parent2)) {
                return Long.toString(computeSimilarity(length, child, parent1, parent2));
            }

        }

        throw new IllegalArgumentException("No child");

    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        List<DNASequence> dnaSequences = createSequences(inputLines);
        int length = dnaSequences.getFirst().sequence().length();

        long similaritySum = 0L;

        for (DNASequence parent1 : dnaSequences) {
            for (DNASequence parent2 : dnaSequences) {
                if (parent1.id() < parent2.id()) {
                    for (DNASequence child : dnaSequences) {
                        if (child.id() != parent1.id() && child.id() != parent2.id() && isChild(length, child, parent1, parent2)) {
                            similaritySum += computeSimilarity(length, child, parent1, parent2);
                        }
                    }
                }
            }
        }

        return Long.toString(similaritySum);

    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        List<DNASequence> dnaSequences = createSequences(inputLines);
        int numberOfSequences = dnaSequences.size();
        int length = dnaSequences.getFirst().sequence().length();

        ImmutableGraph.Builder<DNASequence> graphBuilder = GraphBuilder.undirected().immutable();

        for (int parent1Index = 0; parent1Index < numberOfSequences; parent1Index++) {

            DNASequence parent1 = dnaSequences.get(parent1Index);

            for (int parent2Index = parent1Index + 1; parent2Index < numberOfSequences; parent2Index++) {
                DNASequence parent2 = dnaSequences.get(parent2Index);

                for (DNASequence child : dnaSequences) {
                    if (child.id() != parent1.id() && child.id() != parent2.id() && isChild(length, child, parent1, parent2)) {
                        graphBuilder.putEdge(child, parent1);
                        graphBuilder.putEdge(child, parent2);
                    }
                }

            }

        }

        List<List<DNASequence>> connectedComponents = Graphs.getConnectedComponents(graphBuilder.build());

        List<DNASequence> connectedComponentWithMaxSize = Collections.max(connectedComponents, Comparator.comparingInt(List::size));

        int sumOfIds = connectedComponentWithMaxSize.stream().mapToInt(DNASequence::id).sum();

        return Integer.toString(sumOfIds);

    }

    private List<DNASequence> createSequences(List<String> inputLines) {

        List<DNASequence> dnaSequences = new ArrayList<>();

        for (String inputLine : inputLines) {
            String[] parts = inputLine.split(":");

            int id = Integer.parseInt(parts[0]);
            String sequence = parts[1];

            dnaSequences.add(new DNASequence(id, sequence));
        }

        return dnaSequences;

    }

    private boolean isChild(int length, DNASequence child, DNASequence parent1, DNASequence parent2) {
        return IntStream.range(0, length)
                .noneMatch(i -> child.sequence().charAt(i) != parent1.sequence().charAt(i)
                                && child.sequence().charAt(i) != parent2.sequence().charAt(i));
    }

    private long computeSimilarity(int length, DNASequence child, DNASequence parent1, DNASequence parent2) {
        return computeSimilarity(length, child, parent1) * computeSimilarity(length, child, parent2);
    }

    private long computeSimilarity(int length, DNASequence child, DNASequence parent) {
        return IntStream.range(0, length).filter(i -> child.sequence().charAt(i) == parent.sequence().charAt(i)).count();
    }

}
