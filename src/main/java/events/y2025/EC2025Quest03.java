package events.y2025;

import common.AbstractQuest;
import common.support.interfaces.MainEvent2025;
import common.support.interfaces.Quest03;
import common.support.params.ExecutionParameters;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static common.Counts.byFrequencyCount;

public class EC2025Quest03 extends AbstractQuest implements MainEvent2025, Quest03 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        // Computes the sum of the distinct values
        return Integer.toString(
                toIntStream(inputLines)
                        .distinct()
                        .sum()
        );

    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        // Computes the sum of the first 20 distinct values (starting from the smallest)
        return Integer.toString(
                toIntStream(inputLines)
                        .distinct()
                        .sorted()
                        .limit(20)
                        .sum()
        );

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        // Returns the max number of occurrences of the values

        return Long.toString(
                toIntStream(inputLines)
                        .boxed()
                        .collect(byFrequencyCount())
                        .values()
                        .stream()
                        .mapToLong(l -> l)
                        .max()
                        .orElseThrow()
        );

    }

    private IntStream toIntStream(List<String> inputLines) {
        return Arrays.stream(inputLines.getFirst().split(",")).mapToInt(Integer::parseInt);
    }

}
