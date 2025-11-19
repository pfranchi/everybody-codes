package events.y2025;

import common.AbstractQuest;
import common.support.interfaces.Quest13;
import common.support.interfaces.MainEvent2025;
import common.support.params.ExecutionParameters;

import java.util.ArrayList;
import java.util.List;

public class EC2025Quest13 extends AbstractQuest implements MainEvent2025, Quest13 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int wheelSize = inputLines.size() + 1;
        int res = 2025 % wheelSize;

        if (res == 0) {
            return "1";
        }

        if (res <= wheelSize / 2) {
            // Right half of the wheel, therefore a number with an even index in the list (indexes 0, 2, 4, ...)
            // res = 1 -> index = 0
            // res = 2 -> index = 2
            // res = 3 -> index = 4
            return inputLines.get( 2 * (res - 1) );
        }

        // Left half of the wheel, therefore a number with an odd index in the list (indexes 1, 3, 5, ...), also reversed
        // res = wheelSize - 1 -> index = 1
        // res = wheelSize - 2 -> index = 3
        // res = wheelSize - 3 -> index = 5

        int x = wheelSize - res;
        return inputLines.get(2 * x - 1);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(inputLines, 20252025L);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(inputLines, 202520252025L);
    }

    private record ArcRange(long earlierBound, long laterBound, long length, boolean ascendingValues) {}

    private String solve(List<String> inputLines, long numberOfTurns) {

        List<ArcRange> arcRanges = new ArrayList<>(); // arranged clockwise

        int numberOfLines = inputLines.size();

        long wheelSize = 1L;

        for (int evenLineIndex = 0; evenLineIndex < numberOfLines; evenLineIndex += 2) {
            String[] bounds = inputLines.get(evenLineIndex).split("-");
            long earlierBound = Long.parseLong(bounds[0]);
            long laterBound = Long.parseLong(bounds[1]);

            boolean ascendingValues = laterBound >= earlierBound;

            long length = Math.abs(laterBound - earlierBound) + 1;

            wheelSize += length;

            arcRanges.add(new ArcRange(earlierBound, laterBound, length, ascendingValues));

        }

        int lastOddLineIndex = numberOfLines % 2 == 0 ? numberOfLines - 1 : numberOfLines - 2;

        for (int oddLineIndex = lastOddLineIndex; oddLineIndex > 0; oddLineIndex -= 2) {

            String[] bounds = inputLines.get(oddLineIndex).split("-");
            long earlierBound = Long.parseLong(bounds[1]);
            long laterBound = Long.parseLong(bounds[0]);

            boolean ascendingValues = laterBound >= earlierBound;

            long length = Math.abs(laterBound - earlierBound) + 1;

            wheelSize += length;

            arcRanges.add(new ArcRange(earlierBound, laterBound, length, ascendingValues));

        }

        long res = numberOfTurns % wheelSize;

        if (res == 0L) {
            return "1";
        }

        long globalIndex = 1L;
        for (ArcRange arcRange: arcRanges) {

            long globalIndexStart = globalIndex;
            long globalIndexEndExclusive = globalIndex + arcRange.length();

            if (globalIndexEndExclusive > res) {
                // This is the arc which the dial lands on

                long offset = res - globalIndexStart;

                if (!arcRange.ascendingValues()) {
                    offset *= -1;
                }

                return Long.toString(arcRange.earlierBound() + offset);

            }

            globalIndex = globalIndexEndExclusive;

        }

        throw new IllegalStateException("Should not reach here");

    }

}
