package stories.s03;

import com.google.common.collect.Sets;
import common.AbstractQuest;
import common.geo.CardinalDirection2D;
import common.geo.ImmutableCell2D;
import common.support.interfaces.Quest02;
import common.support.interfaces.Story03;
import common.support.params.ExecutionParameters;

import java.util.*;

import static common.geo.CardinalDirection2D.*;
import static common.geo.ImmutableCell2D.nTimes;

public class Story03Quest02 extends AbstractQuest implements Story03, Quest02 {

    private static final List<CardinalDirection2D> BASE_SEQUENCE = List.of(NORTH, EAST, SOUTH, WEST);
    private static final int MOVE_SEQUENCE_LENGTH = BASE_SEQUENCE.size();

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        ImmutableCell2D startingPosition = null;
        Set<ImmutableCell2D> targetPositions = new HashSet<>();

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] row =  inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                char c = row[columnIndex];

                if (c != '.') {

                    ImmutableCell2D currentPosition = ImmutableCell2D.of(rowIndex, columnIndex);

                    if (c == '@') {
                        startingPosition = currentPosition;
                    } else if (c == '#') {
                        targetPositions.add(currentPosition);
                    }

                }

            }
        }

        if (startingPosition == null ||  targetPositions.isEmpty()) {
            throw new IllegalArgumentException("Invalid input");
        }

        int stepsTaken = 0;

        Set<ImmutableCell2D> visited =  new HashSet<>();
        visited.add(startingPosition);

        ImmutableCell2D currentPosition = startingPosition;
        int currentMoveIndex = 0;

        while (!targetPositions.contains(currentPosition)) {

            CardinalDirection2D move = BASE_SEQUENCE.get(currentMoveIndex);

            ImmutableCell2D nextPosition = currentPosition.add(move);

            if (visited.add(nextPosition)) {
                // 'add' returns when the element was added to the set, meaning it's a new position
                currentPosition = nextPosition;
                stepsTaken++;

            }

            currentMoveIndex++;
            currentMoveIndex %= MOVE_SEQUENCE_LENGTH;

        }

        return Integer.toString(stepsTaken);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(inputLines, 1);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(inputLines, 3);
    }

    private String solve(List<String> inputLines, int copiesOfEachMoveSequenceElement) {

        List<CardinalDirection2D> moveSequence = new ArrayList<>();

        for (CardinalDirection2D cardinalDirection2D : BASE_SEQUENCE) {
            moveSequence.addAll(Collections.nCopies(copiesOfEachMoveSequenceElement, cardinalDirection2D));
        }

        int moveSequenceLength = moveSequence.size();

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        ImmutableCell2D startingPosition = null;
        Set<ImmutableCell2D> unshiftedTargets = new HashSet<>();

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] row =  inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                char c = row[columnIndex];

                if (c != '.') {

                    ImmutableCell2D currentPosition = ImmutableCell2D.of(rowIndex, columnIndex);

                    if (c == '@') {
                        startingPosition = currentPosition;
                    } else if (c == '#') {
                        unshiftedTargets.add(currentPosition);
                    }

                }

            }
        }

        if (startingPosition == null) {
            throw new IllegalArgumentException("Invalid input");
        }

        Set<ImmutableCell2D> targets = new HashSet<>();
        for (ImmutableCell2D unshiftedTarget : unshiftedTargets) {
            targets.add(ImmutableCell2D.of(unshiftedTarget.getRow() - startingPosition.getRow(),
                    unshiftedTarget.getColumn() - startingPosition.getColumn()));
        }

        startingPosition = ImmutableCell2D.of(0, 0);

        int maxDistance = Integer.MIN_VALUE;

        for (ImmutableCell2D target : targets) {
            int rowDiff = Math.abs(target.getRow());
            int columnDiff = Math.abs(target.getColumn());
            maxDistance = Integer.max(maxDistance, Integer.max(rowDiff, columnDiff));
        }

        int insideBorderDistance = maxDistance + 1;
        ImmutableCell2D positionDefinitelyOutside = nTimes(NORTH, insideBorderDistance + 1)
                .add(nTimes(WEST, insideBorderDistance + 1));

        int outsideBorderDistance = insideBorderDistance + 2;

        Set<ImmutableCell2D> insideBorder = generateBorder(insideBorderDistance);
        Set<ImmutableCell2D> outsideBorder = generateBorder(outsideBorderDistance);

        Set<ImmutableCell2D> unvisited = new HashSet<>();

        for (int row = -insideBorderDistance + 1; row < insideBorderDistance; row++) {
            for (int column = -insideBorderDistance + 1; column < insideBorderDistance; column++) {
                unvisited.add(ImmutableCell2D.of(row, column));
            }
        }

        int stepsTaken = 0;

        Set<ImmutableCell2D> visited =  new HashSet<>();
        visited.add(startingPosition);
        unvisited.remove(startingPosition);

        ImmutableCell2D currentPosition = startingPosition;
        int currentMoveIndex = 0;

        boolean isTargetSurrounded = false;

        do {

            CardinalDirection2D move = moveSequence.get(currentMoveIndex);

            ImmutableCell2D nextPosition = currentPosition.add(move);

            if (!targets.contains(nextPosition) && visited.add(nextPosition)) {

                // There is a movement. The 'visited' set now contains the nextPosition

                stepsTaken++;

                // Check if the borders need to be expanded
                int maxDistanceReached = Integer.max(
                        Math.abs(nextPosition.getRow()),
                        Math.abs(nextPosition.getColumn())
                );

                if (maxDistanceReached == insideBorderDistance) {

                    // Expand everything
                    insideBorderDistance = maxDistanceReached + 1;

                    positionDefinitelyOutside = nTimes(NORTH, insideBorderDistance + 1)
                            .add(nTimes(WEST, insideBorderDistance + 1));

                    outsideBorderDistance = insideBorderDistance + 2;

                    // Add the old inside border to the unvisited cells
                    unvisited.addAll(insideBorder);

                    // Regenerate the borders

                    insideBorder = generateBorder(insideBorderDistance);
                    outsideBorder = generateBorder(outsideBorderDistance);

                }

                unvisited.remove(nextPosition);

                // Perform a flood fill from the cell definitely outside and bounded by the outside border
                Set<ImmutableCell2D> floodedCells = floodFill(positionDefinitelyOutside, targets, outsideBorder, visited);

                Collection<ImmutableCell2D> targetsStillAdjacentToOutside = targets.stream().filter(target -> Arrays.stream(CardinalDirection2D.values()).anyMatch(direction -> floodedCells.contains(target.add(direction)))).toList();

                boolean isAnyTargetAdjacentToAnOutsidePosition = !targetsStillAdjacentToOutside.isEmpty();

                Set<ImmutableCell2D> positionsOutside = new HashSet<>(floodedCells);

                if (isAnyTargetAdjacentToAnOutsidePosition) {

                    // Target has not been encircled yet. Some cells may have been encircled with this move
                    positionsOutside.addAll(targetsStillAdjacentToOutside);

                }

                // If anything is unvisited but not outside, set it as a visited position
                Set<ImmutableCell2D> encircledPositions = Set.copyOf(Sets.difference(unvisited, positionsOutside));

                visited.addAll(encircledPositions);
                unvisited.removeAll(encircledPositions);

                // Update the current position

                currentPosition = nextPosition;

                // Check if the target is surrounded
                isTargetSurrounded = !isAnyTargetAdjacentToAnOutsidePosition;

            }

            currentMoveIndex++;
            currentMoveIndex %= moveSequenceLength;

        } while (!isTargetSurrounded);

        return Integer.toString(stepsTaken);
    }

    private Set<ImmutableCell2D> generateBorder(int distance) {

        Set<ImmutableCell2D> border = new HashSet<>();

        for (int delta = -distance; delta <= distance; delta++) {

            border.add(ImmutableCell2D.of(-distance, delta));
            border.add(ImmutableCell2D.of(distance, delta));

            border.add(ImmutableCell2D.of(delta, -distance));
            border.add(ImmutableCell2D.of(delta, distance));

        }

        return border;

    }

    private Set<ImmutableCell2D> floodFill(ImmutableCell2D source, Set<ImmutableCell2D> targets, Set<ImmutableCell2D> border, Set<ImmutableCell2D> visited) {

        Set<ImmutableCell2D> boundary = new HashSet<>();
        boundary.add(source);

        Set<ImmutableCell2D> filledByFlood = new HashSet<>();
        filledByFlood.add(source);

        while (!boundary.isEmpty()) {

            Set<ImmutableCell2D> nextBoundary = new HashSet<>();

            for (ImmutableCell2D cell : boundary) {

                for (CardinalDirection2D direction: CardinalDirection2D.values()) {

                    ImmutableCell2D nextPosition = cell.add(direction);

                    if (!border.contains(nextPosition) && !targets.contains(nextPosition)
                        && !visited.contains(nextPosition) && !filledByFlood.contains(nextPosition)) {
                        nextBoundary.add(nextPosition);
                    }

                }

            }

            // Add the next boundary to the visited cells
            filledByFlood.addAll(nextBoundary);

            // Update the boundary
            boundary = nextBoundary;

        }

        return filledByFlood;

    }

}
