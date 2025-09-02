package stories.s02;

import common.AbstractQuest;
import common.Sections;
import common.geo.CardinalDirection2D;
import common.geo.ImmutableCell2D;
import common.support.interfaces.Quest01;
import common.support.interfaces.Story02;
import common.support.params.ExecutionParameters;

import java.util.*;
import java.util.stream.IntStream;

public class Story02Quest01 extends AbstractQuest implements Story02, Quest01 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        List<List<String>> sections = Sections.splitAtBlankLines(input);

        List<String> section1 = sections.getFirst();
        List<String> section2 = sections.get(1);

        int numberOfRows = section1.size();
        int numberOfColumns = section1.getFirst().length();

        Set<ImmutableCell2D> nails = new HashSet<>();

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            String row = section1.get(rowIndex);
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                if (row.charAt(columnIndex) == '*') {
                    nails.add(ImmutableCell2D.of(rowIndex, columnIndex));
                }
            }
        }

        int numberOfTosses = section2.size();

        int total = 0;

        for (int tossSlot = 1; tossSlot <= numberOfTosses; tossSlot++) {

            int startColumnIndex = (tossSlot - 1) * 2;
            String instructionSequence = section2.get(tossSlot - 1);
            int nextInstructionIndex = 0;

            ImmutableCell2D currentTokenPosition = ImmutableCell2D.of(0, startColumnIndex);

            while (currentTokenPosition.getRow() < numberOfRows) {

                if (nails.contains(currentTokenPosition)) {
                    // Bounce

                    char instruction = instructionSequence.charAt(nextInstructionIndex);
                    nextInstructionIndex++;

                    CardinalDirection2D bouncingDirection = switch (instruction) {
                        case 'R' -> CardinalDirection2D.EAST;
                        case 'L' -> CardinalDirection2D.WEST;
                        default -> throw new IllegalArgumentException();
                    };

                    currentTokenPosition = currentTokenPosition.add(bouncingDirection);

                    if (currentTokenPosition.getColumn() < 0 || currentTokenPosition.getColumn() >= numberOfColumns) {
                        // Out of bounds
                        currentTokenPosition = currentTokenPosition
                                .add(bouncingDirection.inverse())
                                .add(bouncingDirection.inverse());
                    }

                } else {
                    // Move down
                    currentTokenPosition = currentTokenPosition.add(CardinalDirection2D.SOUTH);
                }

            }

            int finalSlot = (currentTokenPosition.getColumn() / 2) + 1;

            int coinsEarned = (finalSlot * 2) - tossSlot;

            total += Integer.max(coinsEarned, 0);

        }

        return Integer.toString(total);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        List<List<String>> sections = Sections.splitAtBlankLines(input);

        List<String> section1 = sections.getFirst();
        List<String> section2 = sections.get(1);

        int numberOfRows = section1.size();
        int numberOfColumns = section1.getFirst().length();

        Set<ImmutableCell2D> nails = new HashSet<>();

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            String row = section1.get(rowIndex);
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                if (row.charAt(columnIndex) == '*') {
                    nails.add(ImmutableCell2D.of(rowIndex, columnIndex));
                }
            }
        }

        int numberOfTossSlots = (numberOfColumns + 1) / 2;

        int total = 0;

        for (String instructionSequence: section2) {

            int max = Integer.MIN_VALUE;

            for (int tossSlot = 1; tossSlot <= numberOfTossSlots; tossSlot++) {
                int coinsWon = findCoinsWon(numberOfRows, numberOfColumns, nails, instructionSequence, tossSlot);
                max = Integer.max(max, coinsWon);
            }

            total += max;
        }

        return Integer.toString(total);

    }

    private int findCoinsWon(int numberOfRows, int numberOfColumns, Set<ImmutableCell2D> nails,
                             String instructionSequence, int tossSlot) {

        int startColumnIndex = (tossSlot - 1) * 2;
        int nextInstructionIndex = 0;

        ImmutableCell2D currentTokenPosition = ImmutableCell2D.of(0, startColumnIndex);

        while (currentTokenPosition.getRow() < numberOfRows) {

            if (nails.contains(currentTokenPosition)) {
                // Bounce

                char instruction = instructionSequence.charAt(nextInstructionIndex);
                nextInstructionIndex++;

                CardinalDirection2D bouncingDirection = switch (instruction) {
                    case 'R' -> CardinalDirection2D.EAST;
                    case 'L' -> CardinalDirection2D.WEST;
                    default -> throw new IllegalArgumentException();
                };

                currentTokenPosition = currentTokenPosition.add(bouncingDirection);

                if (currentTokenPosition.getColumn() < 0 || currentTokenPosition.getColumn() >= numberOfColumns) {
                    // Out of bounds
                    currentTokenPosition = currentTokenPosition
                            .add(bouncingDirection.inverse())
                            .add(bouncingDirection.inverse());
                }

            } else {
                // Move down
                currentTokenPosition = currentTokenPosition.add(CardinalDirection2D.SOUTH);
            }

        }

        int finalSlot = (currentTokenPosition.getColumn() / 2) + 1;

        return Integer.max((finalSlot * 2) - tossSlot, 0);

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        // Solved by brute force, which takes approximately 5 seconds

        List<List<String>> sections = Sections.splitAtBlankLines(input);

        List<String> section1 = sections.getFirst();
        List<String> section2 = sections.get(1);

        int numberOfRows = section1.size();
        int numberOfColumns = section1.getFirst().length();

        Set<ImmutableCell2D> nails = new HashSet<>();

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            String row = section1.get(rowIndex);
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                if (row.charAt(columnIndex) == '*') {
                    nails.add(ImmutableCell2D.of(rowIndex, columnIndex));
                }
            }
        }

        int numberOfTossSlots = (numberOfColumns + 1) / 2;

        int numberOfSequences = section2.size();

        NavigableMap<Key, Integer> coinsWon = new TreeMap<>();

        for (int sequenceIndex = 0; sequenceIndex < numberOfSequences; sequenceIndex++) {

            for (int tossSlot = 1; tossSlot <= numberOfTossSlots; tossSlot++) {

                Key key = new Key(sequenceIndex, tossSlot);
                coinsWon.put(key, findCoinsWon(numberOfRows, numberOfColumns, nails, section2.get(sequenceIndex), tossSlot));

            }

        }

        for (String instructionSequence: section2) {

            IntSummaryStatistics stats = new IntSummaryStatistics();

            for (int tossSlot = 1; tossSlot <= numberOfTossSlots; tossSlot++) {

                int coinsWonForThisToss = findCoinsWon(numberOfRows, numberOfColumns, nails, instructionSequence, tossSlot);
                stats.accept(coinsWonForThisToss);

            }

        }

        int[] selectedTossSlotIndexes = new int[numberOfSequences];

        IntSummaryStatistics stats = new IntSummaryStatistics();

        processAllCombinations(numberOfSequences, numberOfTossSlots, stats, coinsWon, selectedTossSlotIndexes, 0);

        return stats.getMin() + " " + stats.getMax();

    }

    private record Key(int sequenceIndex, int tossSlot) implements Comparable<Key> {

        @Override
        public int compareTo(Key o) {
            return Comparator.comparingInt(Key::sequenceIndex).thenComparing(Key::tossSlot).compare(this, o);
        }
    }

    private void processAllCombinations(int numberOfSequences, int numberOfTossSlots, IntSummaryStatistics stats,
                                        Map<Key, Integer> coinsWon, int[] selectedTossSlots, int currentSequenceIndex) {

        if (currentSequenceIndex == numberOfSequences) {
            // If we get here it means that the array selectedTossSlots is filled up and contains distinct numbers.
            // Using those toss slots, compute the total amount of coins won and add it to the stats

            int totalCoins = IntStream
                    .range(0, numberOfSequences)
                    .map(sequenceIndex -> coinsWon.get(new Key(sequenceIndex, selectedTossSlots[sequenceIndex])))
                    .sum();

            stats.accept(totalCoins);

        } else {

            // Iterate over the possible toss slots for this sequence index. Recursively call this method only if the selected
            // toss slot is different to all the other selected toss slots

            for (int tossSlot = 1; tossSlot <= numberOfTossSlots; tossSlot++) {

                if (isNewTossSlot(selectedTossSlots, currentSequenceIndex, tossSlot)) {
                    selectedTossSlots[currentSequenceIndex] = tossSlot;
                    processAllCombinations(numberOfSequences, numberOfTossSlots, stats, coinsWon, selectedTossSlots, currentSequenceIndex + 1);
                }

            }

        }

    }

    private boolean isNewTossSlot(int[] selectedTossSlots, int currentSequenceIndex, int candidateValue) {
        return IntStream.range(0, currentSequenceIndex).noneMatch(i -> selectedTossSlots[i] == candidateValue);
    }

}
