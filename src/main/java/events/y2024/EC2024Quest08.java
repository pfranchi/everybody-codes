package events.y2024;

import common.stats.AbstractQuest;
import common.support.interfaces.MainEvent2024;
import common.support.interfaces.Quest08;
import common.support.params.ExecutionParameters;
import common.support.params.GenericExecutionParameter;

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
        if (executionParameters instanceof GenericExecutionParameter<?> genericExecutionParameter) {
            Object value = genericExecutionParameter.value();
            if (value instanceof Quest8Input quest8Input) {
                questInput = quest8Input;
            }
        }

        int numberOfPriestAcolytes = questInput.numberOfPriestAcolytes();
        int numberOfAvailableBlocks = questInput.numberOfAvailableBlocks();

        int inputNumber = Integer.parseInt(input);

        log("Input number is {}, acolytes {}, available blocks {}", inputNumber, numberOfPriestAcolytes, numberOfAvailableBlocks);

        int totalNumberOfUsedBlocks = 0;
        int currentLayerNumber = 0;
        int currentLayerThickness = 1;

        while (totalNumberOfUsedBlocks < numberOfAvailableBlocks) {

            currentLayerNumber++;

            int currentLayerWidth = currentLayerNumber * 2 - 1;
            int numberOfBlocksInThisLayer = currentLayerWidth * currentLayerThickness;

            totalNumberOfUsedBlocks += numberOfBlocksInThisLayer;

            log("Layer {} has thickness {} and adds {} blocks, bringing the total to {}", currentLayerNumber,
                    currentLayerThickness, numberOfBlocksInThisLayer, totalNumberOfUsedBlocks);

            currentLayerThickness = (currentLayerThickness * inputNumber) % numberOfPriestAcolytes;

        }

        int numberOfMissingBlocks = totalNumberOfUsedBlocks - numberOfAvailableBlocks;
        long currentLayerWidth = currentLayerNumber * 2 - 1;

        return Long.toString(numberOfMissingBlocks * currentLayerWidth);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return NOT_IMPLEMENTED;
    }
}
