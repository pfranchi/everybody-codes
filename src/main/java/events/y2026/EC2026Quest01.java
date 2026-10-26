package events.y2026;

import common.AbstractQuest;
import common.Strings;
import common.support.interfaces.MainEvent2026;
import common.support.interfaces.Quest01;
import common.support.params.ExecutionParameters;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntUnaryOperator;

public class EC2026Quest01 extends AbstractQuest implements MainEvent2026, Quest01 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        IntUnaryOperator functionToComputeRequired = n -> {
            int ceil = (int) Math.ceil(Math.sqrt(n));
            return ceil * ceil - n;
        };

        return solve(input, functionToComputeRequired);

    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        IntUnaryOperator functionToComputeRequired = n -> {
            int ceil = (int) Math.ceil(n / 4.0);
            ceil = Integer.max(ceil, 2);
            return ceil * 4 - n;
        };

        return solve(input, functionToComputeRequired);

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        IntUnaryOperator functionToComputeRequired = n -> {
            int ceil = (int) Math.ceil(Math.sqrt(n));
            ceil = Integer.max(ceil, 2);
            if (ceil % 2 == 0) {
                ceil++;
            }
            int required = ceil * ceil - 1 - n;

            if (required < 0) {
                required = (ceil + 2) * (ceil + 2) - 1 - n;
            }
            return required;
        };

        return solve(input, functionToComputeRequired);

    }

    private String solve(String input, IntUnaryOperator functionToComputeRequired) {
        int total = Arrays.stream(Strings.firstRow(input).split(","))
                .mapToInt(Integer::parseInt)
                .map(functionToComputeRequired)
                .sum();
        return Integer.toString(total);
    }

}
