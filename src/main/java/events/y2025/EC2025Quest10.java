package events.y2025;

import com.google.common.collect.Sets;
import common.AbstractQuest;
import common.Grids;
import common.geo.ImmutableCell2D;
import common.support.interfaces.MainEvent2025;
import common.support.interfaces.Quest10;
import common.support.params.ExecutionIntParameter;
import common.support.params.ExecutionParameters;

import java.util.*;

import static common.geo.CardinalDirection2D.*;
import static common.geo.ImmutableCell2D.nTimes;

public class EC2025Quest10 extends AbstractQuest implements MainEvent2025, Quest10 {

    private static final List<ImmutableCell2D> KNIGHT_MOVES = List.of(
            nTimes(NORTH, 2).add(EAST),
            nTimes(NORTH, 2).add(WEST),
            nTimes(EAST, 2).add(NORTH),
            nTimes(EAST, 2).add(SOUTH),
            nTimes(SOUTH, 2).add(EAST),
            nTimes(SOUTH, 2).add(WEST),
            nTimes(WEST, 2).add(NORTH),
            nTimes(WEST, 2).add(SOUTH)
    );

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int distance = 4;
        if (executionParameters instanceof ExecutionIntParameter(int value)) {
            distance = value;
        }

        Set<ImmutableCell2D> sheepPositions = new HashSet<>();
        ImmutableCell2D dragonPosition = null;

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] row = inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                char c = row[columnIndex];

                if (c == 'S') {
                    sheepPositions.add(ImmutableCell2D.of(rowIndex, columnIndex));
                } else if (c == 'D') {
                    dragonPosition = ImmutableCell2D.of(rowIndex, columnIndex);
                }
            }
        }

        if (dragonPosition == null) {
            throw new IllegalArgumentException("No D found");
        }

        Set<ImmutableCell2D> reachablePositions = new HashSet<>();
        reachablePositions.add(dragonPosition);
        Set<ImmutableCell2D> currentBoundary = new HashSet<>();
        currentBoundary.add(dragonPosition);

        for (int move = 1; move <= distance; move++) {

            Set<ImmutableCell2D> newBoundary = new HashSet<>();

            for (ImmutableCell2D position: currentBoundary) {
                for (ImmutableCell2D knightMove: KNIGHT_MOVES) {
                    ImmutableCell2D reachableCell = position.add(knightMove);

                    if (reachablePositions.add(reachableCell)) {
                        // It's a new reachable position
                        newBoundary.add(reachableCell);
                    }

                }
            }

            currentBoundary = newBoundary;

        }

        int intersectionSize = Sets.intersection(reachablePositions, sheepPositions).size();

        return Integer.toString(intersectionSize);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfRounds = 20;
        if (executionParameters instanceof ExecutionIntParameter(int value)) {
            numberOfRounds = value;
        }

        Set<ImmutableCell2D> sheepPositions = new HashSet<>();
        Set<ImmutableCell2D> hideoutPositions = new HashSet<>();
        ImmutableCell2D dragonPosition = null;

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] row = inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                char c = row[columnIndex];

                if (c == 'S') {
                    sheepPositions.add(ImmutableCell2D.of(rowIndex, columnIndex));
                } else if (c == 'D') {
                    dragonPosition = ImmutableCell2D.of(rowIndex, columnIndex);
                } else if (c == '#') {
                    hideoutPositions.add(ImmutableCell2D.of(rowIndex, columnIndex));
                }
            }
        }

        if (dragonPosition == null) {
            throw new IllegalArgumentException("No D found");
        }

        Set<ImmutableCell2D> currentDragonPositions = new HashSet<>();
        currentDragonPositions.add(dragonPosition);

        Set<ImmutableCell2D> currentSheepPositions = sheepPositions;

        int countOfSheepEaten = 0;

        for (int round = 1; round <= numberOfRounds; round++) {

            int numberOfSheepEatenThisRound = 0;

            Set<ImmutableCell2D> newDragonPositions = new HashSet<>();
            for (ImmutableCell2D oldDragonPosition: currentDragonPositions) {
                for (ImmutableCell2D knightMove: KNIGHT_MOVES) {

                    ImmutableCell2D newDragonPosition = oldDragonPosition.add(knightMove);

                    if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, newDragonPosition)) {
                        newDragonPositions.add(newDragonPosition);
                    }

                }
            }

            currentDragonPositions = newDragonPositions;

            // Check how many sheep are eaten now, before they move

            for (Iterator<ImmutableCell2D> iterator = currentSheepPositions.iterator(); iterator.hasNext(); ) {
                ImmutableCell2D sheepPosition = iterator.next();

                if (currentDragonPositions.contains(sheepPosition) && !hideoutPositions.contains(sheepPosition)) {
                    // This sheep is eaten now
                    numberOfSheepEatenThisRound++;
                    iterator.remove();
                }

            }

            // Sheep move

            Set<ImmutableCell2D> newSheepPositions = new HashSet<>();
            for (ImmutableCell2D oldSheepPosition: currentSheepPositions) {

                ImmutableCell2D newSheepPosition = oldSheepPosition.add(SOUTH);

                if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, newSheepPosition)) {
                    newSheepPositions.add(newSheepPosition); // not adding sheep that reach the south end of the grid, because they are safe
                }

            }

            currentSheepPositions = newSheepPositions;

            // Check how many are eaten

            for (Iterator<ImmutableCell2D> iterator = currentSheepPositions.iterator(); iterator.hasNext(); ) {
                ImmutableCell2D sheepPosition = iterator.next();

                if (currentDragonPositions.contains(sheepPosition) && !hideoutPositions.contains(sheepPosition)) {
                    // This sheep is eaten now
                    numberOfSheepEatenThisRound++;
                    iterator.remove();
                }

            }

            // Finalize round
            countOfSheepEaten += numberOfSheepEatenThisRound;

        }

        return Integer.toString(countOfSheepEaten);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        inputLines.forEach(this::log);

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        NavigableSet<ImmutableCell2D> sheepPositions = new TreeSet<>();
        Set<ImmutableCell2D> hideoutPositions = new HashSet<>();
        ImmutableCell2D dragonPosition = null;

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] row = inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                char c = row[columnIndex];

                if (c == 'S') {
                    sheepPositions.add(ImmutableCell2D.of(rowIndex, columnIndex));
                } else if (c == 'D') {
                    dragonPosition = ImmutableCell2D.of(rowIndex, columnIndex);
                } else if (c == '#') {
                    hideoutPositions.add(ImmutableCell2D.of(rowIndex, columnIndex));
                }
            }
        }

        if (dragonPosition == null) {
            throw new IllegalArgumentException("No D found");
        }

        Set<ImmutableCell2D> sheepSafePositions = new HashSet<>();
        for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {

            ImmutableCell2D sheepSafePositionInColumn = null;
            ImmutableCell2D sheepSafePositionCurrentCandidate = ImmutableCell2D.of(numberOfRows - 1, columnIndex);

            while (Grids.isPositionInGrid(numberOfRows, numberOfColumns, sheepSafePositionCurrentCandidate)
                   && hideoutPositions.contains(sheepSafePositionCurrentCandidate)) {

                sheepSafePositionInColumn = sheepSafePositionCurrentCandidate;
                sheepSafePositionCurrentCandidate = sheepSafePositionCurrentCandidate.add(NORTH);

            }

            if (sheepSafePositionInColumn != null) {
                sheepSafePositions.add(sheepSafePositionInColumn);
            }

        }

        BoardState initialBoardState = new BoardState(false, dragonPosition, sheepPositions);

        Map<BoardState, Long> cache = new HashMap<>();
        long result = computeAllPossibleDragonWinSequences(numberOfRows, numberOfColumns, hideoutPositions, sheepSafePositions, cache, initialBoardState);

        return Long.toString(result);
    }

    private record BoardState(boolean dragonToMove, ImmutableCell2D dragonPosition, NavigableSet<ImmutableCell2D> sheepPositions) {}

    private long computeAllPossibleDragonWinSequences(int numberOfRows, int numberOfColumns, Set<ImmutableCell2D> hideouts,
                                                      Set<ImmutableCell2D> sheepSafePositions,
                                                      Map<BoardState, Long> cache, BoardState currentState ) {

        if (cache.containsKey(currentState)) {
            return cache.get(currentState);
        }

        if (currentState.sheepPositions().isEmpty()) {
            cache.put(currentState, 1L);
            return 1L;
        }

        if (currentState.sheepPositions().stream().anyMatch(sheepSafePositions::contains)) {
            // One of the sheep made it to a safe location
            cache.put(currentState, 0L);
            return 0L;
        }

        List<BoardState> nextBoardStates = generateNextBoardStates(numberOfRows, numberOfColumns, hideouts, currentState);

        long total = 0L;

        for (BoardState nextBoardState: nextBoardStates) {
            total += computeAllPossibleDragonWinSequences(numberOfRows, numberOfColumns, hideouts, sheepSafePositions, cache, nextBoardState);
        }

        cache.put(currentState, total);

        return total;

    }

    private List<BoardState> generateNextBoardStates(int numberOfRows, int numberOfColumns, Set<ImmutableCell2D> hideouts, BoardState currentBoardState) {

        List<BoardState> nextBoardStates = new ArrayList<>();

        if (currentBoardState.dragonToMove()) {

            // Dragon's turn

            for (ImmutableCell2D knightMove: KNIGHT_MOVES) {

                ImmutableCell2D newDragonPosition = currentBoardState.dragonPosition().add(knightMove);

                if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, newDragonPosition)) {

                    // Dragon moves to its new position. Check if a sheep is eaten
                    if (currentBoardState.sheepPositions().contains(newDragonPosition) && !hideouts.contains(newDragonPosition)) {
                        // The sheep here is eaten
                        NavigableSet<ImmutableCell2D> newSheepPositions = new TreeSet<>(currentBoardState.sheepPositions());
                        newSheepPositions.remove(newDragonPosition);

                        BoardState newBoardState = new BoardState(false, newDragonPosition, newSheepPositions);
                        nextBoardStates.add(newBoardState);
                    } else {
                        // No sheep is eaten after the dragon's move
                        nextBoardStates.add(new BoardState(false, newDragonPosition, currentBoardState.sheepPositions()));
                    }

                }

            }

        } else {

            // Sheep's turn

            boolean noSheepCanMove = true;

            for (ImmutableCell2D oldSheepPosition: currentBoardState.sheepPositions()) {
                ImmutableCell2D newSheepPosition = oldSheepPosition.add(SOUTH);

                // If it is outside the grid, the sheep win and there is no need to generate another board state
                // So, check only the case when the sheep remains inside the grid
                if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, newSheepPosition)) {

                    // If it coincides with the dragon position and it is not a hideout, this move is not valid
                    if (!newSheepPosition.equals(currentBoardState.dragonPosition()) || hideouts.contains(newSheepPosition)) {
                        // valid move
                        noSheepCanMove = false;

                        NavigableSet<ImmutableCell2D> newSheepPositions = new TreeSet<>(currentBoardState.sheepPositions());
                        newSheepPositions.remove(oldSheepPosition);
                        newSheepPositions.add(newSheepPosition);

                        nextBoardStates.add(new BoardState(true, currentBoardState.dragonPosition(), newSheepPositions));

                    }

                } else {
                    noSheepCanMove = false;
                }

            }

            if (noSheepCanMove) {
                // Generate one single board state that follows the current state, in which all sheep and the dragon have remained in the same spot, and it's now the dragon's turn
                nextBoardStates.add(new BoardState(true, currentBoardState.dragonPosition(), currentBoardState.sheepPositions()));
            }


        }

        return nextBoardStates;

    }

}
