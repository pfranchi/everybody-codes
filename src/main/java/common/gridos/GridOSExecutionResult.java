package common.gridos;

import java.util.List;

public record GridOSExecutionResult(int numberOfHeads, int numberOfStates, int numberOfUsedRules,
                                    long numberOfSteps, List<String> grid) {
}
