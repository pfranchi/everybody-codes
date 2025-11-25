package events.y2025;

import common.AbstractQuest;
import common.Strings;
import common.support.interfaces.MainEvent2025;
import common.support.interfaces.Quest16;
import common.support.params.ExecutionParameters;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.stream.IntStream;

public class EC2025Quest16 extends AbstractQuest implements MainEvent2025, Quest16 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int[] values = Arrays.stream(Strings.firstRow(input).split(",")).mapToInt(Integer::parseInt).toArray();
        int numberOfColumns = 90;

        long total = IntStream
                .rangeClosed(1, numberOfColumns)
                .mapToLong(finalColumn -> Arrays.stream(values).filter(value -> finalColumn % value == 0).count())
                .sum();

        return Long.toString(total);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int[] wallFragmentValues = Arrays.stream(Strings.firstRow(input).split(",")).mapToInt(Integer::parseInt).toArray();
        NavigableSet<Integer> currentSpellValues = findSpells(wallFragmentValues);

        long product = currentSpellValues
                .stream()
                .mapToLong(i -> i)
                .reduce(1, (a, b) -> a*b);

        return Long.toString(product);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int[] wallFragmentValues = Arrays.stream(Strings.firstRow(input).split(",")).mapToInt(Integer::parseInt).toArray();
        NavigableSet<Integer> currentSpellValues = findSpells(wallFragmentValues);

        BigDecimal sumOfReciprocals = BigDecimal.ZERO;

        for (int spell: currentSpellValues) {
            BigDecimal bdValue = BigDecimal.valueOf(spell);
            BigDecimal reciprocal = BigDecimal.ONE.divide(bdValue, MathContext.DECIMAL128);
            sumOfReciprocals = sumOfReciprocals.add(reciprocal);
        }

        BigDecimal inverseOfSumOfReciprocals = BigDecimal.ONE.divide(sumOfReciprocals, MathContext.DECIMAL128);

        long numberOfAvailableBlocks = 202520252025000L;
        BigDecimal bdNumberOfAvailableBlocks = BigDecimal.valueOf(numberOfAvailableBlocks);

        long wallLength = inverseOfSumOfReciprocals.multiply(bdNumberOfAvailableBlocks).longValue();

        while (numberOfUsedBlocks(currentSpellValues, wallLength) <= numberOfAvailableBlocks) {
            wallLength++;
        }

        return Long.toString(wallLength - 1);

    }

    private NavigableSet<Integer> findSpells(int[] wallFragmentValues) {
        NavigableSet<Integer> currentSpellValues = new TreeSet<>();

        int wallLength = wallFragmentValues.length;

        for (int wallValueIndex = 0; wallValueIndex < wallLength; wallValueIndex++) {

            int number = wallValueIndex + 1;
            int value = wallFragmentValues[wallValueIndex];

            boolean isCovered = currentSpellValues
                                        .stream()
                                        .mapToInt(spell -> spell)
                                        .filter(spell -> number % spell == 0)
                                        .count()
                                == value;

            if (!isCovered) {
                currentSpellValues.add(number);
            }

        }

        return currentSpellValues;
    }

    private long numberOfUsedBlocks(Set<Integer> spells, long wallLength) {
        long numberOfUsedBlocks = 0;
        for (long spell: spells) {
            numberOfUsedBlocks += wallLength / spell;
        }
        return numberOfUsedBlocks;
    }

}
