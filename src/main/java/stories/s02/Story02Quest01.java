package stories.s02;

import common.AbstractQuest;
import common.Sections;
import common.geo.CardinalDirection2D;
import common.geo.ImmutableCell2D;
import common.support.interfaces.Quest01;
import common.support.interfaces.Story02;
import common.support.params.ExecutionParameters;

import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Set;

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
                int coinsWon = findCoinsWon(numberOfRows, numberOfColumns, nails, numberOfTossSlots, instructionSequence, tossSlot);
                max = Integer.max(max, coinsWon);
            }

            total += max;
        }

        return Integer.toString(total);

    }

    private int findCoinsWon(int numberOfRows, int numberOfColumns, Set<ImmutableCell2D> nails,
                             int numberOfTossSlots, String instructionSequence, int tossSlot) {

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

        inputLines.forEach(this::log);

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

        log("There are {} toss slots", numberOfTossSlots);

        int theoreticalMin = 0;
        int theoreticalMax = 0;

        for (String instructionSequence: section2) {

            emptyLine();
            log("Instruction sequence {}", instructionSequence);

            IntSummaryStatistics stats = new IntSummaryStatistics();

            for (int tossSlot = 1; tossSlot <= numberOfTossSlots; tossSlot++) {

                int coinsWon = findCoinsWon(numberOfRows, numberOfColumns, nails, numberOfTossSlots, instructionSequence, tossSlot);
                log("\tSlot {}, coins won {}", tossSlot, coinsWon);
                stats.accept(coinsWon);

            }

            theoreticalMin += stats.getMin();
            theoreticalMax += stats.getMax();

        }

        log("Min {}, max {}", theoreticalMin, theoreticalMax);

        return NOT_IMPLEMENTED;
    }

}
