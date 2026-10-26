package stories.s04;

import common.AbstractQuest;
import common.geo.CardinalDirection2D;
import common.geo.Cell2D;
import common.geo.ImmutableCell2D;
import common.support.interfaces.Quest03;
import common.support.interfaces.Story04;
import common.support.params.ExecutionParameters;

import java.math.BigInteger;
import java.util.List;

public class Story04Quest03 extends AbstractQuest implements Story04, Quest03 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int width = Integer.parseInt(inputLines.getFirst().replace("width=", ""));
        int height = Integer.parseInt(inputLines.get(1).replace("height=", ""));
        char[] horizontalOffsetsChars = inputLines.get(2).replace("horizontal-offsets=", "").toCharArray();
        char[] verticalOffsetsChars = inputLines.get(3).replace("vertical-offsets=", "").toCharArray();

        int count = 0;

        for (int rowIndex = 0; rowIndex < height; rowIndex++) {
            for (int columnIndex = 0; columnIndex < width; columnIndex++) {
                if (isIsolated(horizontalOffsetsChars, verticalOffsetsChars, ImmutableCell2D.of(rowIndex, columnIndex))) {
                    count++;
                }
            }
        }

        return Integer.toString(count);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int width = Integer.parseInt(inputLines.getFirst().replace("width=", ""));
        int height = Integer.parseInt(inputLines.get(1).replace("height=", ""));
        char[] horizontalOffsetsChars = inputLines.get(2).replace("horizontal-offsets=", "").toCharArray();
        char[] verticalOffsetsChars = inputLines.get(3).replace("vertical-offsets=", "").toCharArray();

        Counts isolatedCellCounts = countIsolatedCells(horizontalOffsetsChars, verticalOffsetsChars, height, width);
        return isolatedCellCounts.countOfSameColor().max(isolatedCellCounts.countOfOppositeColor()).toString();

    }

    private boolean isIsolated(char[] horizontalOffsetsChars, char[] verticalOffsetsChars, Cell2D cell) {
        for (CardinalDirection2D direction: CardinalDirection2D.values()) {
            if (!hasStitchInDirection(horizontalOffsetsChars, verticalOffsetsChars, cell, direction)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasStitchInDirection(char[] horizontalOffsetsChars, char[] verticalOffsetsChars, Cell2D cell, CardinalDirection2D direction) {

        if (direction == CardinalDirection2D.NORTH || direction == CardinalDirection2D.SOUTH) {

            // Look at horizontal stitches
            int relevantRowIndex = cell.getRow();
            if (direction == CardinalDirection2D.SOUTH) {
                relevantRowIndex++;
            }

            char horizontalOffsetChar = horizontalOffsetsChars[relevantRowIndex % horizontalOffsetsChars.length];

            return (cell.getColumn() % 2 == 0) == (horizontalOffsetChar == '0');

        }

        // Look at vertical stitches
        int relevantColumnIndex = cell.getColumn();
        if (direction == CardinalDirection2D.EAST) {
            relevantColumnIndex++;
        }

        char verticalOffsetChar = verticalOffsetsChars[relevantColumnIndex % verticalOffsetsChars.length];

        return (cell.getRow() % 2 == 0) == (verticalOffsetChar == '0');

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        BigInteger width = new BigInteger(inputLines.getFirst().replace("width=", ""));
        BigInteger height = new BigInteger(inputLines.get(1).replace("height=", ""));
        char[] horizontalOffsetsChars = inputLines.get(2).replace("horizontal-offsets=", "").toCharArray();
        char[] verticalOffsetsChars = inputLines.get(3).replace("vertical-offsets=", "").toCharArray();

        /*
            Each horizontal offset char describes how one row of stitches is formed, so the length of the sequence corresponds
            to the number of rows (= height) after which the horizontal pattern repeats. Actually we consider the
            lcm(horizontalOffsetsSequenceLength, 4) in order to get a full pattern (horizontal + vertical stitches) repeat.

            Each vertical offset char describes how one column of stitches is formed, so the length of the sequence corresponds
            to the number of columns (width). Again consider the lcm(verticalOffsetsSequenceLength, 4).

            If we take a block with:
            * lcm(horizontalOffsetsSequenceLength, 4) rows -> this is called blockHeight
            * lcm(verticalOffsetsSequenceLength, 4) columns -> this is called blockWidth
            we have a full pattern repeat in every block.

                +-------------+-------------+-------------+----
                |    block    |    block    |    block    |
                +-------------+-------------+-------------+----
                |    block    |    block    |    block    |
                +-------------+-------------+-------------+----
                |    block    |    block    |    block    |
                +-------------+-------------+-------------+----
                |             |             |             |

         */

        BigInteger four = BigInteger.valueOf(4);

        BigInteger horizontalSequenceLength = BigInteger.valueOf(horizontalOffsetsChars.length);
        BigInteger blockHeight = lcm(horizontalSequenceLength, four).min(height);
        BigInteger[] blockHeightRepetitionResult = height.divideAndRemainder(blockHeight);
        BigInteger blockVerticalOccurrences = blockHeightRepetitionResult[0];
        BigInteger verticalRemainder = blockHeightRepetitionResult[1];

        BigInteger verticalSequenceLength =  BigInteger.valueOf(verticalOffsetsChars.length);
        BigInteger blockWidth = lcm(verticalSequenceLength, four).min(width);
        BigInteger[] blockWidthRepetitionResult = width.divideAndRemainder(blockWidth);
        BigInteger blockHorizontalOccurrences =  blockWidthRepetitionResult[0];
        BigInteger horizontalRemainder =  blockWidthRepetitionResult[1];

        BigInteger totalBlockOccurrences = blockVerticalOccurrences.multiply(blockHorizontalOccurrences);

        Counts countsForASingleBlock = countIsolatedCells(horizontalOffsetsChars, verticalOffsetsChars, blockHeight, blockWidth);

        Counts countsForAllBlocks = countsForASingleBlock.multiply(totalBlockOccurrences);

        Counts countsBelowTheLastBlock = countIsolatedCells(horizontalOffsetsChars, verticalOffsetsChars, verticalRemainder, blockWidth);
        Counts countsBelowAllBlocks = countsBelowTheLastBlock.multiply(blockHorizontalOccurrences);

        Counts countsToTheRightOfTheLastBlock = countIsolatedCells(horizontalOffsetsChars, verticalOffsetsChars, blockHeight, horizontalRemainder);
        Counts countsToTheRightOfAllTheBlocks = countsToTheRightOfTheLastBlock.multiply(blockVerticalOccurrences);

        Counts countsInTheBottomRightCorner = countIsolatedCells(horizontalOffsetsChars, verticalOffsetsChars, verticalRemainder, horizontalRemainder);

        Counts totalCounts = countsForAllBlocks.add(countsBelowAllBlocks).add(countsToTheRightOfAllTheBlocks).add(countsInTheBottomRightCorner);

        return totalCounts.countOfSameColor().max(totalCounts.countOfOppositeColor()).toString();
    }

    private static BigInteger lcm(BigInteger a, BigInteger b) {
        return a.multiply(b).divide(a.gcd(b));
    }

    private record Counts(BigInteger countOfSameColor, BigInteger countOfOppositeColor) {

        public Counts multiply(BigInteger occurrences) {
            return new Counts(countOfSameColor.multiply(occurrences), countOfOppositeColor.multiply(occurrences));
        }

        public Counts add(Counts other) {
            return new Counts(countOfSameColor.add(other.countOfSameColor()), countOfOppositeColor.add(other.countOfOppositeColor()));
        }

    }

    private Counts countIsolatedCells(char[] horizontalOffsetsChars, char[] verticalOffsetsChars, Number height, Number width) {

        long countOfSameColor = 0;
        long countOfOppositeColor = 0;

        boolean startOfRowSameColor = true;
        for (int rowIndex = 0; rowIndex < height.intValue(); rowIndex++) {

            boolean currentCellSameColor = startOfRowSameColor;

            for (int columnIndex = 0; columnIndex < width.intValue(); columnIndex++) {

                ImmutableCell2D cell = ImmutableCell2D.of(rowIndex, columnIndex);

                if (isIsolated(horizontalOffsetsChars, verticalOffsetsChars, cell)) {
                    // Increment the correct counter
                    if (currentCellSameColor) {
                        countOfSameColor++;
                    } else {
                        countOfOppositeColor++;
                    }

                }

                if (hasStitchInDirection(horizontalOffsetsChars, verticalOffsetsChars, ImmutableCell2D.of(rowIndex, columnIndex), CardinalDirection2D.EAST)) {
                    currentCellSameColor = !currentCellSameColor;
                }

            }

            if (hasStitchInDirection(horizontalOffsetsChars, verticalOffsetsChars, ImmutableCell2D.of(rowIndex, 0), CardinalDirection2D.SOUTH)) {
                // Change the color
                startOfRowSameColor = !startOfRowSameColor;
            }

        }

        return new Counts(BigInteger.valueOf(countOfSameColor), BigInteger.valueOf(countOfOppositeColor));

    }

}
