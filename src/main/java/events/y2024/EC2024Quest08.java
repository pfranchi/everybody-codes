package events.y2024;

import common.AbstractQuest;
import common.Strings;
import common.support.interfaces.MainEvent2024;
import common.support.interfaces.Quest08;
import common.support.params.ExecutionParameters;
import common.support.params.GenericExecutionParameter;

import java.util.ArrayList;
import java.util.List;

public class EC2024Quest08 extends AbstractQuest implements MainEvent2024, Quest08 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int inputNumber = Integer.parseInt(input);
        long sqrt = (long) Math.sqrt(inputNumber);
        long numberOfMissingBlocks = (sqrt + 1) * (sqrt + 1) - inputNumber;
        long baseWidth = 2 * sqrt + 1;

        return Long.toString(numberOfMissingBlocks * baseWidth);
    }

    public record Quest8Input(int numberOfPriestAcolytes, int numberOfAvailableBlocks) {}

    private static final Quest8Input ACTUAL_INPUT = new Quest8Input(1111, 20240000);

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Quest8Input questInput = ACTUAL_INPUT;
        if (executionParameters instanceof GenericExecutionParameter<?>(Object value)) {
            if (value instanceof Quest8Input quest8Input) {
                questInput = quest8Input;
            }
        }

        int numberOfPriestAcolytes = questInput.numberOfPriestAcolytes();
        int numberOfAvailableBlocks = questInput.numberOfAvailableBlocks();

        int inputNumber = Integer.parseInt(input);

        int totalNumberOfUsedBlocks = 0;
        int currentLayerNumber = 0;
        int currentLayerThickness = 1;

        while (totalNumberOfUsedBlocks < numberOfAvailableBlocks) {

            currentLayerNumber++;

            int currentLayerWidth = currentLayerNumber * 2 - 1;
            int numberOfBlocksInThisLayer = currentLayerWidth * currentLayerThickness;

            totalNumberOfUsedBlocks += numberOfBlocksInThisLayer;

            currentLayerThickness = (currentLayerThickness * inputNumber) % numberOfPriestAcolytes;

        }

        int numberOfMissingBlocks = totalNumberOfUsedBlocks - numberOfAvailableBlocks;
        long currentLayerWidth = currentLayerNumber * 2L - 1;

        return Long.toString(numberOfMissingBlocks * currentLayerWidth);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        long numberOfHighPriests = Long.parseLong(Strings.firstRow(input));
        int numberOfAcolytes = 10;

        long availableBlocks = 202400000L;

        long firstLayerThickness = 1L;
        List<Long> columnHeights = new ArrayList<>();
        columnHeights.add(firstLayerThickness);

        long previousLayerThickness = firstLayerThickness;

        int numberOfLayers = 1;

        while (true) {

            numberOfLayers++;

            long thickness = previousLayerThickness * numberOfHighPriests % numberOfAcolytes + numberOfAcolytes;

            previousLayerThickness = thickness;

            for (int previousColumnIndex = 0; previousColumnIndex < numberOfLayers - 1; previousColumnIndex++) {
                columnHeights.set(previousColumnIndex, columnHeights.get(previousColumnIndex) + thickness);
            }

            columnHeights.add(thickness);

            // Compute the number of blocks used in the tower
            int shrineWidth = numberOfLayers * 2 - 1;

            List<Long> actualBlocksUsed = new ArrayList<>();
            for (int columnIndex = 0; columnIndex < numberOfLayers - 1; columnIndex++) {
                long columnHeight = columnHeights.get(columnIndex);
                long blocksRemoved = numberOfHighPriests * shrineWidth * columnHeight % numberOfAcolytes;
                actualBlocksUsed.add(columnHeight - blocksRemoved);
            }
            actualBlocksUsed.add(columnHeights.get(numberOfLayers - 1));

            long totalBlocksUsed = actualBlocksUsed.stream().mapToLong(l -> l).sum() * 2 - actualBlocksUsed.getFirst();

            if (totalBlocksUsed > availableBlocks) {
                return Long.toString(totalBlocksUsed - availableBlocks);
            }

        }

    }
}
