package events.y2025;

import common.AbstractQuest;
import common.support.interfaces.MainEvent2025;
import common.support.interfaces.Quest11;
import common.support.params.ExecutionParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class EC2025Quest11 extends AbstractQuest implements MainEvent2025, Quest11 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfRounds = 10;

        List<Integer> l = new ArrayList<>(inputLines.stream().map(Integer::parseInt).toList());

        int size = l.size();

        int round = 0;

        // Phase 1
        boolean somethingChanged;
        do {

            somethingChanged = false;

            for (int i = 0; i < size - 1; i++) {

                if (l.get(i) > l.get(i + 1)) {
                    somethingChanged = true;
                    l.set(i, l.get(i) - 1);
                    l.set(i + 1, l.get(i + 1) + 1);
                }

            }

            if (somethingChanged) {
                round++;
            }

            if (round == numberOfRounds) {
                return checksum(size, l);
            }

        } while (somethingChanged);

        // Phase 2
        do {

            somethingChanged = false;

            for (int i = 0; i < size - 1; i++) {

                if (l.get(i) < l.get(i + 1)) {
                    somethingChanged = true;
                    l.set(i, l.get(i) + 1);
                    l.set(i + 1, l.get(i + 1) - 1);
                }

            }

            if (somethingChanged) {
                round++;
            }

            if (round == numberOfRounds) {
                return checksum(size, l);
            }

        } while (somethingChanged);

        throw new IllegalStateException("The list was balanced before the expected number of rounds");
    }

    private String checksum(int size, List<Integer> l) {
        long result = IntStream.range(0, size).mapToLong(index -> (long) (index + 1) * l.get(index)).sum();
        return Long.toString(result);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        List<Integer> l = new ArrayList<>(inputLines.stream().map(Integer::parseInt).toList());

        int globalSum = l.stream().mapToInt(i -> i).sum();
        int size = l.size();

        int balancedValue = globalSum / size;

        int round = 0;

        // Phase 1
        boolean somethingChanged;
        do {

            somethingChanged = false;

            for (int i = 0; i < size - 1; i++) {

                if (l.get(i) > l.get(i + 1)) {
                    somethingChanged = true;
                    l.set(i, l.get(i) - 1);
                    l.set(i + 1, l.get(i + 1) + 1);
                }

            }

            if (somethingChanged) {
                round++;
            }

            if (isBalanced(l, balancedValue)) {
                return Integer.toString(round);
            }

        } while (somethingChanged);

        // Phase 2
        do {

            somethingChanged = false;

            for (int i = 0; i < size - 1; i++) {

                if (l.get(i) < l.get(i + 1)) {
                    somethingChanged = true;
                    l.set(i, l.get(i) + 1);
                    l.set(i + 1, l.get(i + 1) - 1);
                }

            }

            if (somethingChanged) {
                round++;
            }

            if (isBalanced(l, balancedValue)) {
                return Integer.toString(round);
            }

        } while (somethingChanged);

        return NOT_IMPLEMENTED;

    }

    private boolean isBalanced(List<Integer> l, int balancedValue) {
        return l.stream().allMatch(i -> i == balancedValue);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        // This solution assumes that the given list of numbers is sorted in increasing order. There is no move possible in phase 1.

        List<Long> columnValues = new ArrayList<>(inputLines.stream().map(Long::parseLong).toList());

        long globalSum = columnValues.stream().mapToLong(i -> i).sum();
        int size = columnValues.size();

        long balancedValue = globalSum / size;

        long result = columnValues.stream().mapToLong(n -> n).filter(n -> n > balancedValue).map(n -> (n - balancedValue)).sum();

        return Long.toString(result);

    }

}
