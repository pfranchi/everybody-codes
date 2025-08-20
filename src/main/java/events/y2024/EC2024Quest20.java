package events.y2024;

import common.AbstractQuest;
import common.Grids;
import common.geo.CardinalDirection2D;
import common.geo.ImmutableCell2D;
import common.support.interfaces.MainEvent2024;
import common.support.interfaces.Quest20;
import common.support.params.ExecutionParameters;

import java.util.*;

import static com.google.common.primitives.Booleans.trueFirst;

public class EC2024Quest20 extends AbstractQuest implements MainEvent2024, Quest20 {

    private enum SegmentType {
        NO_STREAM(true, -1, "."),
        WARM_STREAM(true, 1, "+"),
        COLD_STREAM(true, -2, "-"),
        ROCK(false, 0, "#");

        private final boolean flyable;
        private final int altitudeDelta;
        private final String symbol;

        SegmentType(boolean flyable, int altitudeDelta, String symbol) {
            this.flyable = flyable;
            this.altitudeDelta = altitudeDelta;
            this.symbol = symbol;
        }

        public static SegmentType fromChar(char c) {
            return switch (c) {
                case '.' -> NO_STREAM;
                case '+' -> WARM_STREAM;
                case '-' -> COLD_STREAM;
                case '#' -> ROCK;
                default -> throw new IllegalArgumentException("Invalid char " + c);
            };
        }

        public boolean isFlyable() {
            return flyable;
        }

        public int getAltitudeDelta() {
            return altitudeDelta;
        }


        @Override
        public String toString() {
            return symbol;
        }
    }

    private record Part1State(int flightSeconds, ImmutableCell2D position, CardinalDirection2D direction)
            implements Comparable<Part1State> {

        static Comparator<Part1State> flightCmp = Comparator.comparingInt(Part1State::flightSeconds); // less flight is better

        @Override
        public int compareTo(Part1State o) {
            return flightCmp.thenComparing(Part1State::position).thenComparing(Part1State::direction).compare(this, o);
        }
    }

    static final ImmutableCell2D END_POSITION = ImmutableCell2D.of(-1, -1);
    static final Part1State PART_1_END_STATE = new Part1State(100, END_POSITION, CardinalDirection2D.NORTH);

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        SegmentType[][] grid = new SegmentType[numberOfRows][numberOfColumns];
        ImmutableCell2D startingPosition = null;

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] chars = inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                char c = chars[columnIndex];

                SegmentType segmentType;

                if (c == 'S') {
                    startingPosition = ImmutableCell2D.of(rowIndex, columnIndex);
                    segmentType = SegmentType.ROCK;
                } else {
                    segmentType = SegmentType.fromChar(c);
                }

                grid[rowIndex][columnIndex] = segmentType;
            }
        }

        if (startingPosition == null) {
            throw new IllegalStateException("No starting position found");
        }

        log("{} rows and {} columns. The grid is", numberOfRows, numberOfColumns);
        log(Arrays.deepToString(grid));

        log("The starting position is {}", startingPosition);

        int maxAltitude = maxAltitudePart1(numberOfRows, numberOfColumns, grid, startingPosition, 100);

        return Integer.toString(maxAltitude);

    }

    private record Part1StateWithAltitude(Part1State state, int altitude) implements Comparable<Part1StateWithAltitude> {

        static Comparator<Part1StateWithAltitude> costCmp = Comparator.comparingInt(Part1StateWithAltitude::effectiveCost).reversed(); // higher cost is better

        @Override
        public int compareTo(Part1StateWithAltitude o) {
            return costCmp.thenComparing(Part1StateWithAltitude::state).compare(this, o);
        }

        private int effectiveCost() {
            return altitude() - state.flightSeconds();
        }

    }

    private int maxAltitudePart1(int numberOfRows, int numberOfColumns,
                                 SegmentType[][] grid, ImmutableCell2D startingPosition, int maxFlightTime) {

        PriorityQueue<Part1StateWithAltitude> priorityQueue = new PriorityQueue<>();

        Map<Part1State, Integer> confirmedMaximalAltitudes = new TreeMap<>();

        for (CardinalDirection2D direction: CardinalDirection2D.values()) {

            Part1State initialState = new Part1State(0, startingPosition, direction);

            Part1StateWithAltitude initialStateWithAltitude = new Part1StateWithAltitude(initialState, 1000);
            priorityQueue.add(initialStateWithAltitude);

            confirmedMaximalAltitudes.put(initialState, 1000);

        }

        while (!priorityQueue.isEmpty()) {

            Part1StateWithAltitude currentStateWithAltitude = priorityQueue.remove();

            Part1State currentState = currentStateWithAltitude.state();
            int currentAltitude = currentStateWithAltitude.altitude();

            if (currentState == PART_1_END_STATE) {
                // Exit condition
                return currentAltitude;
            }

            if (confirmedMaximalAltitudes.containsKey(currentState) && confirmedMaximalAltitudes.get(currentState) > currentAltitude) {
                continue; // already found a path that reaches this state at a higher altitude
            }

            // Compute the next states

            int flightSeconds = currentState.flightSeconds();
            ImmutableCell2D position = currentState.position();
            CardinalDirection2D previousDirection = currentState.direction();

            Set<Part1StateWithAltitude> nextStates = new HashSet<>();
            if (flightSeconds == maxFlightTime) {
                nextStates.add( new Part1StateWithAltitude(PART_1_END_STATE, currentAltitude) ); // no altitude change from this state to the single END_STATE
            } else {

                for (CardinalDirection2D direction: CardinalDirection2D.values()) {
                    if (!direction.equals(previousDirection.inverse())) {

                        ImmutableCell2D nextPosition = position.add(direction);

                        int nextPositionRow = nextPosition.row();
                        int nextPositionColumn = nextPosition.column();

                        if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, nextPositionRow, nextPositionColumn)) {

                            SegmentType nextSegmentType = grid[nextPositionRow][nextPositionColumn];

                            if (nextSegmentType.isFlyable()) {

                                int nextAltitude = currentAltitude + nextSegmentType.getAltitudeDelta();
                                nextStates.add(new Part1StateWithAltitude(new Part1State(flightSeconds + 1, nextPosition, direction), nextAltitude));

                            }

                        }

                    }
                }

            }

            // Examine next states

            for (Part1StateWithAltitude nextStateWithAltitude: nextStates) {

                Part1State state = nextStateWithAltitude.state();
                int altitude = nextStateWithAltitude.altitude();

                if (confirmedMaximalAltitudes.containsKey(state)) {

                    // This next state has already been reached in another path. Compare altitudes
                    int altitudeThroughOtherPath = confirmedMaximalAltitudes.get(state);

                    if (altitude > altitudeThroughOtherPath) {

                        // This is a better path. Update map of minimal costs
                        confirmedMaximalAltitudes.put(state, altitude);
                        priorityQueue.add( nextStateWithAltitude ); // readded to the queue with a higher altitude (=higher priority)

                    }

                } else {

                    // This state was never seen before
                    priorityQueue.add(nextStateWithAltitude);
                    confirmedMaximalAltitudes.put(state, altitude);

                }

            }

        }

        return -1;

    }



    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.size();

        SegmentType[][] grid = new SegmentType[numberOfRows][numberOfColumns];

        ImmutableCell2D startingPosition = null;

        ImmutableCell2D checkpointAPosition = null;
        ImmutableCell2D checkpointBPosition = null;
        ImmutableCell2D checkpointCPosition = null;

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] chars = inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                char c = chars[columnIndex];

                SegmentType segmentType;

                if (c == 'S') {
                    startingPosition = ImmutableCell2D.of(rowIndex, columnIndex);
                    segmentType = SegmentType.ROCK;
                } else if (c == 'A' || c == 'B' || c == 'C') {

                    ImmutableCell2D position = ImmutableCell2D.of(rowIndex, columnIndex);

                    if (c == 'A') {
                        checkpointAPosition = position;
                    } else if (c == 'B') {
                        checkpointBPosition = position;
                    } else {
                        checkpointCPosition = position;
                    }

                    segmentType = SegmentType.NO_STREAM;

                } else {
                    segmentType = SegmentType.fromChar(c);
                }

                grid[rowIndex][columnIndex] = segmentType;
            }
        }

        if (startingPosition == null || checkpointAPosition == null || checkpointBPosition == null || checkpointCPosition == null) {
            throw new IllegalStateException("Starting position or checkpoints not found");
        }

        log("{} rows and {} columns. The grid is", numberOfRows, numberOfColumns);
        log(Arrays.deepToString(grid));

        log("Start at {}, checkpoint A at {}, checkpoint B at {}, checkpoint C at {}", startingPosition, checkpointAPosition, checkpointBPosition, checkpointCPosition);

        int minFlightTime = minFlightTimePart2(numberOfRows, numberOfColumns, grid, startingPosition, checkpointAPosition, checkpointBPosition, checkpointCPosition);

        return Integer.toString(minFlightTime);
    }

    private record Part2State(ImmutableCell2D position, int altitude, CardinalDirection2D direction, boolean checkpointAVisited, boolean checkpointBVisited, boolean checkpointCVisited)
            implements Comparable<Part2State> {

        @Override
        public int compareTo(Part2State o) {
            return Comparator.comparing(Part2State::checkpointAVisited, trueFirst())
                    .thenComparing(Part2State::checkpointBVisited, trueFirst())
                    .thenComparing(Part2State::checkpointCVisited, trueFirst())
                    .thenComparing(Part2State::position)
                    .thenComparing(Part2State::altitude)
                    .thenComparing(Part2State::direction)
                    .compare(this, o);
        }
    }

    private record Part2StateWithFlightTime(Part2State state, int flightTime) {}

    private static final Part2State PART_2_END_STATE = new Part2State(END_POSITION, 0, CardinalDirection2D.NORTH, false, false, false);
    // the values of the fields are not important

    private int minFlightTimePart2(int numberOfRows, int numberOfColumns, SegmentType[][] grid,
                                   ImmutableCell2D startingPosition, ImmutableCell2D checkpointAPosition,
                                   ImmutableCell2D checkpointBPosition, ImmutableCell2D checkpointCPosition) {

        Comparator<Part2StateWithFlightTime> cmp = Comparator.comparingInt(value -> value.flightTime()
                + distanceToNextCheckpoint(startingPosition,
                value.state().position(),
                checkpointAPosition,
                checkpointBPosition,
                checkpointCPosition,
                value.state().checkpointAVisited(),
                value.state().checkpointBVisited(),
                value.state().checkpointCVisited())

                + (value.state().checkpointAVisited() ? 0 : 1000)
                + (value.state().checkpointBVisited() ? 0 : 1000)
                + (value.state().checkpointCVisited() ? 0 : 1000)

                + Math.abs(10000 - value.state().altitude())

        );

        PriorityQueue<Part2StateWithFlightTime> priorityQueue = new PriorityQueue<>(cmp);
        Map<Part2State, Integer> confirmedMinimalFlightTimes = new TreeMap<>();

        for (CardinalDirection2D direction: CardinalDirection2D.values()) {

            Part2State initialState = new Part2State(startingPosition, 10000, direction, false, false, false);
            Part2StateWithFlightTime initialStateWithFlightTime = new Part2StateWithFlightTime(initialState, 0);

            priorityQueue.add(initialStateWithFlightTime);

            confirmedMinimalFlightTimes.put(initialState, 0);

        }

        while (!priorityQueue.isEmpty()) {

            Part2StateWithFlightTime stateWithFlightTime = priorityQueue.remove();

            Part2State currentState = stateWithFlightTime.state();
            int currentFlightTime = stateWithFlightTime.flightTime();

            if (currentState == PART_2_END_STATE) {
                return currentFlightTime;
            }

            if (confirmedMinimalFlightTimes.containsKey(currentState) && confirmedMinimalFlightTimes.get(currentState) < currentFlightTime) {
                continue;
            }

            // Compute the next states

            ImmutableCell2D currentPosition = currentState.position();
            int currentAltitude = currentState.altitude();
            CardinalDirection2D currentDirection = currentState.direction();
            boolean checkpointAVisited = currentState.checkpointAVisited();
            boolean checkpointBVisited = currentState.checkpointBVisited();
            boolean checkpointCVisited = currentState.checkpointCVisited();

            boolean isAnEndState = currentPosition.equals(startingPosition) && currentAltitude == 10000 && checkpointCVisited;

            Set<Part2StateWithFlightTime> nextStates = new HashSet<>();

            if (isAnEndState) {
                nextStates.add( new Part2StateWithFlightTime(PART_2_END_STATE, currentFlightTime) );
            } else {

                for (CardinalDirection2D nextDirection: CardinalDirection2D.values()) {
                    if (!nextDirection.equals(currentDirection.inverse())) {

                        ImmutableCell2D nextPosition = currentPosition.add(nextDirection);

                        int nextPositionRow = nextPosition.row();
                        int nextPositionColumn = nextPosition.column();

                        if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, nextPositionRow, nextPositionColumn)) {

                            SegmentType nextSegmentType = grid[nextPositionRow][nextPositionColumn];
                            boolean isFlyable = nextSegmentType.isFlyable();

                            // If the next position is checkpoint A and it was already visited, it is not a reachable position
                            if (nextPosition.equals(checkpointAPosition) && checkpointAVisited) {
                                isFlyable = false;
                            }

                            // If the next position is checkpoint B and (A was not visited or B was already visited), it is not a reachable position
                            if (nextPosition.equals(checkpointBPosition) && (!checkpointAVisited || checkpointBVisited)) {
                                isFlyable = false;
                            }

                            // If the next position is checkpoint C and (B was not visited or C was already visited), it is not a reachable position
                            if (nextPosition.equals(checkpointCPosition) && (!checkpointBVisited || checkpointCVisited)) {
                                isFlyable = false;
                            }

                            // If the next position is the starting position and C was not visited, it is not a reachable position
                            if (nextPosition.equals(startingPosition) && (!checkpointCVisited)) {
                                isFlyable = false;
                            }

                            if (isFlyable) {

                                int nextAltitude = currentAltitude + nextSegmentType.getAltitudeDelta();

                                boolean nextCheckpointAVisited = checkpointAVisited || nextPosition.equals(checkpointAPosition);
                                boolean nextCheckpointBVisited = checkpointBVisited || nextPosition.equals(checkpointBPosition);
                                boolean nextCheckpointCVisited = checkpointCVisited || nextPosition.equals(checkpointCPosition);

                                Part2State nextState = new Part2State(nextPosition, nextAltitude, nextDirection, nextCheckpointAVisited, nextCheckpointBVisited, nextCheckpointCVisited);

                                nextStates.add(new Part2StateWithFlightTime(nextState, currentFlightTime + 1));

                            }

                        }

                    }
                }

            }

            // Next states computed

            for (Part2StateWithFlightTime nextStateWithFlightTime: nextStates) {

                Part2State nextState = nextStateWithFlightTime.state();
                int nextFlightTime = nextStateWithFlightTime.flightTime();

                if (confirmedMinimalFlightTimes.containsKey(nextState)) {

                    // State already reached in another path. Compare times
                    int flightTimeThroughOtherPath = confirmedMinimalFlightTimes.get(nextState);
                    if (nextFlightTime > flightTimeThroughOtherPath) {

                        // This is a better path. Update map and readd to queue with lower time (= higher priority)
                        confirmedMinimalFlightTimes.put(nextState, nextFlightTime);
                        priorityQueue.add(nextStateWithFlightTime);

                    }

                } else {

                    // This state was never seen before
                    priorityQueue.add(nextStateWithFlightTime);
                    confirmedMinimalFlightTimes.put(nextState, nextFlightTime);

                }

            }

        }

        return -1;

    }

    private int distanceToNextCheckpoint(ImmutableCell2D startPosition, ImmutableCell2D currentPosition,
                                         ImmutableCell2D checkpointAPosition, ImmutableCell2D checkpointBPosition,
                                         ImmutableCell2D checkpointCPosition, boolean checkpointAVisited,
                                         boolean checkpointBVisited, boolean checkpointCVisited) {

        if (!checkpointAVisited) {
            return distanceToCheckpoint(currentPosition, checkpointAPosition);
        }

        if (!checkpointBVisited) {
            return distanceToCheckpoint(currentPosition, checkpointBPosition);
        }

        if (!checkpointCVisited) {
            return distanceToCheckpoint(currentPosition, checkpointCPosition);
        }

        return distanceToCheckpoint(currentPosition, startPosition);

    }

    private int distanceToCheckpoint(ImmutableCell2D currentPosition, ImmutableCell2D targetPosition) {
        return currentPosition.taxicabDistance(targetPosition);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return NOT_IMPLEMENTED;
    }
}
