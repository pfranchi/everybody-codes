package events.y2026;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import common.AbstractQuest;
import common.geo.CardinalDirection2D;
import common.geo.Cell2D;
import common.geo.ImmutableCell2D;
import common.geo.MutableCell2D;
import common.support.interfaces.MainEvent2026;
import common.support.interfaces.Quest10;
import common.support.params.ExecutionIntParameter;
import common.support.params.ExecutionParameters;

import java.util.*;
import java.util.stream.IntStream;

public class EC2026Quest10 extends AbstractQuest implements MainEvent2026, Quest10 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        ParsedInput parsedInput = ParsedInput.from(inputLines);

        boolean[][] grid = parsedInput.grid();
        List<Nullghost> nullghosts = parsedInput.nullghosts();

        int totalNumberOfCollisions = IntStream
                .rangeClosed(1, 10)
                .mapToObj(_ -> simulateOneRound(grid, Long.MAX_VALUE, nullghosts))
                .mapToInt(RoundSimulationResult::numberOfCollisions)
                .sum();

        return Integer.toString(totalNumberOfCollisions);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        long collisionLimit = 10000;

        if (executionParameters instanceof ExecutionIntParameter(int value)) {
            collisionLimit = value;
        }

        return solveWithRoundSkip(inputLines, collisionLimit);

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        long collisionLimit = 1000000000000L;
        return solveWithRoundSkip(inputLines, collisionLimit);

    }

    private String solveWithRoundSkip(List<String> inputLines, long collisionLimit) {

        ParsedInput parsedInput = ParsedInput.from(inputLines);

        boolean[][] grid = parsedInput.grid();
        List<Nullghost> nullghosts = parsedInput.nullghosts();

        Map<BoardPosition, FullGameDetails> boardPositions = new HashMap<>();

        long round = 0;

        boolean checkForSkips = true;

        while (true) {

            round++;
            RoundSimulationResult roundSimulationResult = simulateOneRound(grid, collisionLimit, nullghosts);

            if (roundSimulationResult.anyNullghostDisappeared()) {

                if (nullghosts.isEmpty()) {
                    // The game has ended! Return the current round number
                    return Long.toString(round);
                }

                // We continue the simulation, but we reset the map of board positions because they are no longer relevant since the number of Nullghosts changed this round
                boardPositions.clear();
                checkForSkips = true;

            } else if (checkForSkips) {

                BoardPosition boardPosition = new BoardPosition(nullghosts.stream().map(ImmutableNullghost::copyOf).toList());
                FullGameDetails fullGameDetails = new FullGameDetails(round, nullghosts.stream().map(Nullghost::getCollisions).toList());

                if (boardPositions.containsKey(boardPosition)) {

                    checkForSkips = false;

                    FullGameDetails originalFullGameDetails = boardPositions.get(boardPosition);

                    long roundDelta = round - originalFullGameDetails.round();

                    List<Long> collisionDeltas = IntStream
                            .range(0, nullghosts.size())
                            .mapToObj(nullghostIndex -> fullGameDetails.collisions().get(nullghostIndex) - originalFullGameDetails.collisions().get(nullghostIndex))
                            .toList();

                    long minNumberOfBlockRepeats = Long.MAX_VALUE;

                    for (int nullghostIndex = 0; nullghostIndex < nullghosts.size(); nullghostIndex++) {

                        long currentCollisionValue = fullGameDetails.collisions().get(nullghostIndex);
                        long collisionDelta = collisionDeltas.get(nullghostIndex);
                        long numberOfBlockRepeats = (collisionLimit - currentCollisionValue) / collisionDelta; // rounds down

                        minNumberOfBlockRepeats = Math.min(minNumberOfBlockRepeats, numberOfBlockRepeats);

                    }

                    minNumberOfBlockRepeats--; // This -1 might be optimizable. Without the -1 it works for all Nullghosts except for the very last one

                    long numberOfRoundSimulated = minNumberOfBlockRepeats * roundDelta;
                    round += numberOfRoundSimulated;

                    // We increment the collisions in bulk for all Nullghosts

                    for (int nullghostIndex = 0; nullghostIndex < nullghosts.size(); nullghostIndex++) {

                        long collisionDelta = collisionDeltas.get(nullghostIndex);
                        long totalCollisionDelta = minNumberOfBlockRepeats * collisionDelta;
                        nullghosts.get(nullghostIndex).incrementCollisions(totalCollisionDelta);

                    }

                    // Nullghosts that have reached the limit disappear. The game might stop now
                    nullghosts.removeIf(nullghost -> nullghost.getCollisions() >= collisionLimit);

                    if (nullghosts.isEmpty()) {
                        return Long.toString(round);
                    }

                } else {
                    boardPositions.put(boardPosition, fullGameDetails);
                }

            }

        }

    }

    private record ParsedInput(boolean[][] grid, List<Nullghost> nullghosts) {

        public static ParsedInput from(List<String> inputLines) {
            int numberOfRows = inputLines.size();
            int numberOfColumns = inputLines.getFirst().length();

            boolean[][] grid = new boolean[numberOfRows][numberOfColumns];

            List<Nullghost> nullghosts = new ArrayList<>();

            for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {

                char[] row =  inputLines.get(rowIndex).toCharArray();

                for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                    char c =  row[columnIndex];

                    switch (c) {
                        case '#' -> grid[rowIndex][columnIndex] = true;
                        case '.' -> grid[rowIndex][columnIndex] = false;
                        case '>', '<', '^', 'v' -> {
                            MutableCell2D location = MutableCell2D.of(rowIndex, columnIndex);
                            CardinalDirection2D facing = CardinalDirection2D.fromChar(c);

                            nullghosts.add(new Nullghost(location, facing));
                        }
                        default -> throw new IllegalStateException("Unexpected value: " + c);
                    }

                }

            }

            return new ParsedInput(grid, nullghosts);
        }

    }

    public static final class Nullghost {
        private final MutableCell2D position;
        private CardinalDirection2D facing;
        private long collisions;

        public Nullghost(MutableCell2D position, CardinalDirection2D facing) {
            this.position = position;
            this.facing = facing;
            this.collisions = 0;
        }

        public MutableCell2D getPosition() {
            return position;
        }

        public CardinalDirection2D getFacing() {
            return facing;
        }

        public void setFacing(CardinalDirection2D facing) {
            this.facing = facing;
        }

        public void incrementCollision() {
            collisions++;
        }

        public void incrementCollisions(long collisionDelta) {
            collisions += collisionDelta;
        }

        public long getCollisions() {
            return collisions;
        }

    }

    private record ImmutableNullghost(ImmutableCell2D location, CardinalDirection2D facing) {
        public static ImmutableNullghost copyOf(Nullghost nullghost) {
            return new ImmutableNullghost(ImmutableCell2D.copyOf(nullghost.getPosition()), nullghost.getFacing());
        }
    }
    private record BoardPosition(List<ImmutableNullghost> nullghosts) {}

    private record FullGameDetails(long round, List<Long> collisions) {}

    private record RoundSimulationResult(int numberOfCollisions, boolean anyNullghostDisappeared) {}

    private RoundSimulationResult simulateOneRound(boolean[][] grid, long collisionLimit, List<Nullghost> nullghosts) {

        int numberOfCollisions = 0;
        boolean anyNullghostDisappeared = false;

        // Wall collisions

        for (Nullghost nullghost : nullghosts) {

            Cell2D targetPosition = nullghost.getPosition().add(nullghost.getFacing());

            if (grid[targetPosition.getRow()][targetPosition.getColumn()]) {
                // This is a wall. The Nullghost does not move and instead turns 90° clockwise. Count one collision
                numberOfCollisions++;
                nullghost.incrementCollision();
                nullghost.setFacing(nullghost.getFacing().rotateRight());
            } else {
                // Move in the direction it's currently facing
                nullghost.getPosition().move(nullghost.getFacing());
            }

        }

        // First round of disappearances
        Iterator<Nullghost> nullghostIterator = nullghosts.iterator();
        while (nullghostIterator.hasNext()) {
            Nullghost nullghost = nullghostIterator.next();
            if (nullghost.getCollisions() >= collisionLimit) {
                nullghostIterator.remove();
                anyNullghostDisappeared = true;
            }
        }

        // We group the surviving Nullghosts by their location
        Multimap<ImmutableCell2D, Nullghost> nullghostByTheirCurrentLocation = HashMultimap.create();
        for (Nullghost nullghost : nullghosts) {
            nullghostByTheirCurrentLocation.put(ImmutableCell2D.copyOf(nullghost.getPosition()), nullghost);
        }

        // Check collisions between Nullghosts
        for (Map.Entry<ImmutableCell2D, Collection<Nullghost>> entry: nullghostByTheirCurrentLocation.asMap().entrySet()) {
            Collection<Nullghost> nullghostsCollidingHere = entry.getValue();

            if (nullghostsCollidingHere.size() > 1) {
                // There is a collision here. Increment the collision counter by the number of Nullghosts here. Rotate each of them 180°
                numberOfCollisions += nullghostsCollidingHere.size();

                for (Nullghost collidingNullghost: nullghostsCollidingHere) {
                    collidingNullghost.incrementCollision();
                    collidingNullghost.setFacing(collidingNullghost.getFacing().inverse());
                }

            }
        }

        // Second round of disappearances
        nullghostIterator = nullghosts.iterator();
        while (nullghostIterator.hasNext()) {
            Nullghost nullghost = nullghostIterator.next();
            if (nullghost.getCollisions() >= collisionLimit) {
                nullghostIterator.remove();
                anyNullghostDisappeared = true;
            }
        }

        return new RoundSimulationResult(numberOfCollisions, anyNullghostDisappeared);

    }

}
