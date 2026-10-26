package stories.s04;

import common.AbstractQuest;
import common.support.interfaces.Quest01;
import common.support.interfaces.Story04;
import common.support.params.ExecutionParameters;

import java.util.*;

public class Story04Quest01 extends AbstractQuest implements Story04, Quest01 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        int total = inputLines.stream().mapToInt(this::endingOfSequence).sum();
        return Integer.toString(total);
    }

    private int endingOfSequence(String sequence) {
        String[] sequenceParts = sequence.split(",");

        NavigableSet<Integer> visited =  new TreeSet<>();
        visited.add(0);
        int current = 0;

        for (String part: sequenceParts) {
            int delta = Integer.parseInt(part);

            int backward = current - delta;
            if (backward > 0 && visited.add(backward)) {
                // Good! new value
                current = backward;
            } else {
                int forward = current + delta;
                visited.add(forward);
                current = forward;
            }

        }

        return current;

    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        int total = inputLines.stream().mapToInt(this::endingOfSequencePart2).sum();
        return Integer.toString(total);
    }

    private int endingOfSequencePart2(String sequence) {
        String[] sequenceParts = sequence.split(",");

        NavigableSet<Integer> visited =  new TreeSet<>();
        visited.add(0);
        int current = 0;

        for (String part: sequenceParts) {
            int delta = Integer.parseInt(part);

            int backward = current - delta;
            if (backward > 0 && visited.add(backward)) {
                // Good! new value
                current = backward;
            } else {
                int forward = current + delta;
                while (!visited.add(forward)) {
                    forward++;
                }
                current = forward;
            }

        }

        return current;

    }

    private record Arc(int min, int max) {

        // Every instance has the invariant that min < max.


        private Arc {

            if (min == max) {
                throw new IllegalArgumentException("min must be != max: min=max=" + min);
            }

            if (min > max) {
                throw new IllegalArgumentException("min must be <= max: min=" + min + ", max=" + max);
            }

        }

        public boolean crosses(Arc other) {
            // This method assumes that min, max, other.min and other.max are all different

            if (this.min == other.min || this.min == other.max || this.max == other.min || this.max == other.max) {
                throw new IllegalArgumentException("Cannot compare arcs: this=" + this + ", other=" + other);
            }

            return

                    this.min < other.min
                    && other.min < this.max
                    && this.max < other.max
                    ||
                    other.min < this.min
                    && this.min < other.max
                    && other.max < this.max;

        }

        public boolean contains(int value) {
            // Again we assume that value != min and value != max

            if (this.min == value || this.max == value) {
                throw new IllegalArgumentException("Cannot check if arc contains value: arc=" + this + ", value=" + value);
            }

            return this.min <  value && value < this.max;
        }

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        inputLines.forEach(this::log);

        int total = 0;

        for (String line: inputLines) {

            int ending = endingOfSequencePart3(line);
            log("Line {} ends at {}", line, ending);

            total += ending;

        }

        return Integer.toString(total);
    }

    private int  endingOfSequencePart3(String sequence) {
        String[] sequenceParts = sequence.split(",");

        boolean drawingBelow = true;

        List<Arc> arcsBelow = new ArrayList<>();
        List<Arc> arcsAbove = new ArrayList<>();

        NavigableSet<Integer> visited = new TreeSet<>();
        visited.add(0);

        int current = 0;

        for (String part: sequenceParts) {

            List<Arc> relevantArcs = drawingBelow ? arcsBelow : arcsAbove;

            int value = Integer.parseInt(part);

            int backward = current - value;

            boolean isBackwardGood = backward > 0 && !visited.contains(backward);

            // Check if we cross an arc by going backward

            if (isBackwardGood) {
                Arc backwardArc = new Arc(backward, current);

                if (relevantArcs.stream().noneMatch(arc -> arc.crosses(backwardArc))) {

                    log("{}: Drawing a backward arc {} the line from {} back to {}",
                            value, drawingBelow ? "below" : "above", current, backward);

                    // We can go backward. Consolidate the backward movement and continue the iteration
                    relevantArcs.add(backwardArc);
                    drawingBelow = !drawingBelow;
                    visited.add(backward);
                    current = backward;

                    log("\tLast arc added below: {}. Last arc added above: {}",
                            arcsBelow.isEmpty() ? "[]" : arcsBelow.getLast(),
                            arcsAbove.isEmpty() ? "[]" : arcsAbove.getLast()
                    );

                    continue;

                }

            }

            // Try moving forward

            int forward = current + value;
            while (visited.contains(forward)) {
                forward++;
            }

            // This is a forward value never visited before. Check if there are crossings

            Arc forwardArc = new Arc(current, forward);

            if (relevantArcs.stream().noneMatch(arc -> arc.crosses(forwardArc))) {

                log("{}: Drawing a forward arc {} the line from {} to {}",
                        value, drawingBelow ? "below" : "above", current, forward);

                // We can go forward. Consolidate the forward movement and continue the iteration
                relevantArcs.add(forwardArc);
                drawingBelow = !drawingBelow;
                visited.add(forward);
                current = forward;

            } else {

                // Try to avoid the crossings. It may not be possible.

                int minOfThePotentialArc = current;
                Optional<Arc> cagingArc = relevantArcs.stream().filter(arc -> arc.contains(minOfThePotentialArc)).findAny();
                if (cagingArc.isPresent()) {
                    // No solution because the min (which is fixed) is caged by another arc and can never escape.
                    // Skip this jump entirely

                    Arc cagedBy = cagingArc.get();
                    log("{}: Skipping the jump of length {} because the value we are " +
                        "currently at ({}) is caged by arc {}", value, value, current, cagedBy);

                    //drawingBelow = !drawingBelow;

                } else {

                    int finalCurrent = current;
                    int currentCandidate = forward - 1;

                    boolean isBadCandidate;

                    do {

                        currentCandidate++;
                        int finalCurrentCandidate = currentCandidate;

                        isBadCandidate = visited.contains(currentCandidate) ||
                                         relevantArcs.stream().anyMatch(arc -> arc.crosses(new Arc(finalCurrent, finalCurrentCandidate)));

                    } while (isBadCandidate);

                    // Found the good candidate

                    log("{}: After extending the original length of {} to a length of {}, " +
                        "we jump forward {} the line from {} to {}", value, value, (currentCandidate - current),
                            drawingBelow ? "below" : "above", current, currentCandidate);

                    relevantArcs.add(new Arc(current, currentCandidate));
                    drawingBelow = !drawingBelow;
                    visited.add(currentCandidate);
                    current = currentCandidate;

                }

            }

            log("\tLast arc added below: {}. Last arc added above: {}",
                    arcsBelow.isEmpty() ? "[]" : arcsBelow.getLast(),
                    arcsAbove.isEmpty() ? "[]" : arcsAbove.getLast()
            );
            //log("\tArcs above: {}", arcsAbove);

        }

        return current;

    }

}
