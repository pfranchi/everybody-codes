package events.y2024;

import common.AbstractQuest;
import common.support.interfaces.MainEvent2024;
import common.support.interfaces.Quest09;
import common.support.params.ExecutionParameters;

import java.util.*;

public class EC2024Quest09 extends AbstractQuest implements MainEvent2024, Quest09 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        List<Integer> stamps = List.of(1, 3, 5, 10);
        return computeResult(inputLines, stamps);

    }

    private String computeResult(List<String> inputLines, List<Integer> stamps) {
        NavigableSet<Integer> sortedStamps = new TreeSet<>(stamps);
        int result = inputLines
                .stream().mapToInt(Integer::parseInt)
                .map(brightness -> computeNumberOfBeetles(sortedStamps, brightness))
                .sum();
        return Integer.toString(result);
    }

    private int computeNumberOfBeetles(NavigableSet<Integer> stamps, int value) {

        /*
            Suppose the available stamps are 1, 16, 25, 30.
            To reach a total of 41, this method first uses a 30 and then eleven 1s, using twelve stamps.
            However, the minimal solution is to use two stamps, one 16 and one 25.
         */
        int total = 0;
        for (int stamp: stamps.reversed()) {
            int quotient = value / stamp;
            total += quotient;
            value = value % stamp;
        }
        return total;

    }

    private int computeNumberOfBeetlesV2(NavigableSet<Integer> stamps, Map<Integer, Integer> cachedResults, int value) {

        if (cachedResults.containsKey(value)) {
            return cachedResults.get(value);
        }

        if (stamps.contains(value)) {
            return 1;
        }

        int minNumberOfBeetles = Integer.MAX_VALUE;

        for (int stamp: stamps) {

            if (value > stamp) {
                minNumberOfBeetles = Integer.min(minNumberOfBeetles, computeNumberOfBeetlesV2(stamps, cachedResults, value - stamp));
            }

        }

        if (minNumberOfBeetles == Integer.MAX_VALUE) {
            throw new IllegalStateException("Min has remained equal to MAX_VALUE");
        }

        // Add 1
        minNumberOfBeetles++;

        // Cache the result
        cachedResults.put(value, minNumberOfBeetles);

        return minNumberOfBeetles;

    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        List<Integer> stamps = List.of(1, 3, 5, 10, 15, 16, 20, 24, 25, 30);
        NavigableSet<Integer> sortedStamps = new TreeSet<>(stamps);

        List<Integer> inputValues = inputLines
                .stream().mapToInt(Integer::parseInt).boxed().toList();

        int maxInputValue = inputValues.stream().mapToInt(i -> i).max().orElseThrow();

        Map<Integer, Integer> cachedResults = new HashMap<>();

        for (int value = 1; value <= maxInputValue; value++) {
            computeNumberOfBeetlesV2(sortedStamps, cachedResults, value);
        }

        int total = inputValues.stream().mapToInt(inputValue -> inputValue).map(cachedResults::get).sum();
        return Integer.toString(total);

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        List<Integer> stamps = List.of(1, 3, 5, 10, 15, 16, 20, 24, 25, 30, 37, 38, 49, 50, 74, 75, 100, 101);

        NavigableSet<Integer> sortedStamps = new TreeSet<>(stamps);

        List<Integer> brightnesses = inputLines
                .stream().mapToInt(Integer::parseInt).boxed().toList();

        int maxInputValue = brightnesses.stream().mapToInt(i -> i).max().orElseThrow();

        Map<Integer, Integer> cachedResults = new HashMap<>();

        for (int value = 1; value <= maxInputValue; value++) {
            computeNumberOfBeetlesV2(sortedStamps, cachedResults, value);
        }

        int total = 0;

        for (int brightness: brightnesses) {

            int brightnessParity = brightness % 2;

            int minNumberOfBeetles = Integer.MAX_VALUE;

            for (int difference = brightnessParity; difference <= 100; difference += 2) {

                int brightness1 = (brightness - difference) / 2;
                int brightness2 = brightness - brightness1;

                int numberOfBeetles1 = computeNumberOfBeetlesV2(sortedStamps, cachedResults, brightness1);
                int numberOfBeetles2 = computeNumberOfBeetlesV2(sortedStamps, cachedResults, brightness2);

                int totalNumberOfBeetlesForThisDifference = numberOfBeetles1 + numberOfBeetles2;

                minNumberOfBeetles = Integer.min(minNumberOfBeetles, totalNumberOfBeetlesForThisDifference);

            }

            total += minNumberOfBeetles;

        }

        return Integer.toString(total);
    }
}
