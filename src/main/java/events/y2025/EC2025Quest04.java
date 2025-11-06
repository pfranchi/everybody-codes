package events.y2025;

import common.AbstractQuest;
import common.support.interfaces.MainEvent2025;
import common.support.interfaces.Quest04;
import common.support.params.ExecutionParameters;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

public class EC2025Quest04 extends AbstractQuest implements MainEvent2025, Quest04 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        double first = Double.parseDouble(inputLines.getFirst());
        double last = Double.parseDouble(inputLines.getLast());

        return Integer.toString((int) (2025 * (first / last)));
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        BigDecimal first = BigDecimal.valueOf(Long.parseLong(inputLines.getFirst()));
        BigDecimal last = BigDecimal.valueOf(Long.parseLong(inputLines.getLast()));

        BigDecimal ratio = last.divide(first, MathContext.DECIMAL128);
        BigDecimal result = ratio.multiply(BigDecimal.valueOf(10000000000000L));
        BigDecimal numberOfTurns = result.setScale(0, RoundingMode.CEILING);

        return Long.toString(numberOfTurns.longValue());

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfInputLines = inputLines.size();

        BigDecimal ratio = BigDecimal.ONE;

        for (int inputLineIndex = 0; inputLineIndex < numberOfInputLines; inputLineIndex++) {

            String inputLine = inputLines.get(inputLineIndex);
            String[] parts = inputLine.split("\\|");

            BigDecimal[] bigDecimalValues = Arrays.stream(parts)
                    .mapToInt(Integer::parseInt)
                    .mapToObj(BigDecimal::valueOf)
                    .toArray(BigDecimal[]::new);

            if (inputLineIndex == 0) {
                ratio = ratio.multiply(bigDecimalValues[0], MathContext.DECIMAL128);
            } else {

                ratio = ratio.divide(bigDecimalValues[0], MathContext.DECIMAL128);

                if (inputLineIndex != numberOfInputLines - 1) {
                    ratio = ratio.multiply(bigDecimalValues[1], MathContext.DECIMAL128);
                }

            }

        }

        BigDecimal result = ratio.multiply(BigDecimal.valueOf(100), MathContext.DECIMAL128);

        return Long.toString(result.longValue());
    }

}
