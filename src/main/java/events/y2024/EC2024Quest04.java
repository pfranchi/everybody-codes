package events.y2024;

import common.stats.AbstractQuest;
import common.support.interfaces.MainEvent2024;
import common.support.interfaces.Quest04;
import common.support.params.ExecutionParameters;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.LongConsumer;

public class EC2024Quest04 extends AbstractQuest implements MainEvent2024, Quest04 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return computeMinimumNumberOfStrikes(inputLines);
    }

    private static class Stats implements LongConsumer {
        private long count = 0;
        private long sum = 0;
        private long min = Integer.MAX_VALUE;
        private long max = Integer.MIN_VALUE;

        @Override
        public void accept(long value) {
            this.count++;
            this.sum += value;
            this.min = Long.min(min, value);
            this.max = Long.max(max, value);
        }

        public long getCount() {
            return count;
        }

        public long getSum() {
            return sum;
        }

        public long getMin() {
            return min;
        }

        public long getMax() {
            return max;
        }
    }

    private String computeMinimumNumberOfStrikes(List<String> inputLines) {
        Stats stats = new Stats();
        inputLines.stream().mapToLong(Long::parseLong).forEach(stats);
        return Long.toString(stats.getSum() - stats.getMin() * stats.getCount());
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return computeMinimumNumberOfStrikes(inputLines);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        List<Long> numbers = inputLines.stream().mapToLong(Long::parseLong).boxed().toList();

        Stats stats = new Stats();
        numbers.forEach(stats::accept);

        long count = stats.getCount();
        long sum = stats.getSum();

        long min = stats.getMin();
        long max = stats.getMax();

        NavigableMap<Long, Long> setOfValues = new TreeMap<>();

        for (long height = min; height <= max; height++) {
            setOfValues.put(getNumberOfStrikes(numbers, height), height);
        }

        Map.Entry<Long, Long> lowestEntry = setOfValues.firstEntry();

        log("Lowest entry is {}", lowestEntry);

        return Long.toString(lowestEntry.getKey());
    }

    private long getNumberOfStrikes(List<Long> numbers, long height) {
        return numbers.stream().mapToLong(number -> number).map(number -> Math.abs(number - height)).sum();
    }

}
