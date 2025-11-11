package events.y2025;

import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import common.AbstractQuest;
import common.Sections;
import common.support.interfaces.MainEvent2025;
import common.support.interfaces.Quest07;
import common.support.params.ExecutionParameters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@SuppressWarnings("UnstableApiUsage")
public class EC2025Quest07 extends AbstractQuest implements MainEvent2025, Quest07 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Quest07Input quest07Input = parse(input);

        Graph<Character> graph = quest07Input.graph();
        List<String> words = quest07Input.words();

        for (String word: words) {
            if (isValid(graph, word)) {
                return word;
            }
        }

        throw new IllegalArgumentException("No valid word in the input");
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Quest07Input quest07Input = parse(input);

        Graph<Character> graph = quest07Input.graph();
        List<String> words = quest07Input.words();

        int total = IntStream
                .range(0, words.size())
                .filter(wordIndex -> isValid(graph, words.get(wordIndex)))
                .map(wordIndex -> wordIndex + 1)
                .sum();

        return Integer.toString(total);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Quest07Input quest07Input = parse(input);

        Graph<Character> graph = quest07Input.graph();
        List<String> prefixes = quest07Input.words();

        Set<String> allWords = new HashSet<>();

        prefixes.stream()
                .filter(prefix -> isValid(graph, prefix))
                .forEach(prefix -> addWords(graph, allWords, prefix));

        long numberOfValidWords = allWords.stream().filter(word -> word.length() >= 7).count();

        return Long.toString(numberOfValidWords);
    }

    private record Quest07Input(ImmutableGraph<Character> graph, List<String> words) {}

    private Quest07Input parse(String input) {

        List<List<String>> sections = Sections.splitAtBlankLines(input);

        List<String> words = Arrays.asList(sections.getFirst().getFirst().split(","));

        ImmutableGraph.Builder<Character> graphBuilder = GraphBuilder
                .directed()
                .allowsSelfLoops(true)
                .immutable();

        for (String rule: sections.getLast()) {
            String[] parts = rule.split(" > ");
            char origin = parts[0].charAt(0);

            String[] destinations = parts[1].split(",");

            for (String destination: destinations) {
                graphBuilder.putEdge(origin, destination.charAt(0));
            }
        }

        return new Quest07Input(graphBuilder.build(), words);

    }

    private boolean isValid(Graph<Character> graph, String word) {

        char[] chars = word.toCharArray();
        int length = chars.length;

        for (int index = 0; index < length - 1; index++) {
            char c1 = chars[index];
            char c2 = chars[index + 1];

            if (!graph.hasEdgeConnecting(c1, c2)) {
                return false;
            }

        }

        return true;

    }

    private void addWords(Graph<Character> graph, Set<String> allWords, String currentWord) {

        if (allWords.add(currentWord) && currentWord.length() < 11) {

            // True if this is a new element. Then we must explore next nodes

            Set<Character> possibleNextChars = graph.successors(currentWord.charAt(currentWord.length() - 1));

            for (char possibleNextChar: possibleNextChars) {
                addWords(graph, allWords, currentWord + possibleNextChar);
            }

        }

    }

}
