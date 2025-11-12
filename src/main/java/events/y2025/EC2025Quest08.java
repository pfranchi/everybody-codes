package events.y2025;

import common.AbstractQuest;
import common.Strings;
import common.support.interfaces.MainEvent2025;
import common.support.interfaces.Quest08;
import common.support.params.ExecutionIntParameter;
import common.support.params.ExecutionParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class EC2025Quest08 extends AbstractQuest implements MainEvent2025, Quest08 {

    private record Segment(int low, int high) {

        public boolean intersects(Segment other) {
            if (this.equals(other)) {
                return true;
            }

            if (this.low == other.low || this.low == other.high
                || this.high == other.low || this.high == other.high) {
                return false;
            }

            int count = 0;

            if (this.low < other.low && other.low < this.high ) {
                count++;
            }

            if (this.low < other.high && other.high < this.high ) {
                count++;
            }

            return count == 1;

        }

    }

    private List<Segment> createSegments(String input) {

        List<Integer> numbers = Arrays.stream(Strings.firstRow(input).split(",")).map(Integer::parseInt).toList();

        List<Segment> segments = new ArrayList<>();

        for (int i = 0; i < numbers.size() - 1; i++) {

            int n1 = numbers.get(i);
            int n2 = numbers.get(i + 1);

            segments.add(new Segment(Integer.min(n1, n2), Integer.max(n1, n2)));

        }

        return segments;

    }

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfNails = 32;

        if (executionParameters instanceof ExecutionIntParameter(int value)) {
            numberOfNails = value;
        }

        List<Segment> segments = createSegments(input);

        int count = 0;

        for (Segment segment: segments) {
            if (segment.high() - segment.low() == numberOfNails / 2) {
                count++;
            }
        }

        return Integer.toString(count);

    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        /*

        // The number of nails is not actually used in part 2. If you need it for a
        // different implementation, uncomment this code

        int numberOfNails = 256;

        if (executionParameters instanceof ExecutionIntParameter(int value)) {
            numberOfNails = value;
        }

         */

        List<Segment> segments = createSegments(input);

        long count = 0L;

        for (int s1Index = 0; s1Index < segments.size(); s1Index++ ) {

            Segment s1 = segments.get(s1Index);

            count += IntStream.range(0, s1Index)
                    .mapToObj(segments::get)
                    .filter(s1::intersects)
                    .count();

        }

        return Long.toString(count);

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfNails = 256;

        if (executionParameters instanceof ExecutionIntParameter(int value)) {
            numberOfNails = value;
        }

        List<Segment> segments = createSegments(input);

        long max = Long.MIN_VALUE;

        for (int lower = 1; lower <= numberOfNails; lower++) {
            for (int upper = lower + 1; upper <= numberOfNails; upper++) {

                Segment testSegment = new Segment(lower, upper);

                long count = segments.stream()
                        .filter(testSegment::intersects)
                        .count();

                max = Long.max(max, count);

            }
        }

        return Long.toString(max);
    }

}
