package stories.s02;

import common.AbstractQuest;
import common.support.interfaces.Quest01;
import common.support.interfaces.Story02;
import common.support.params.ExecutionParameters;

import java.util.List;

public class Story02Quest01 extends AbstractQuest implements Story02, Quest01 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        inputLines.forEach(this::log);

        return NOT_IMPLEMENTED;
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return NOT_IMPLEMENTED;
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return NOT_IMPLEMENTED;
    }

}
