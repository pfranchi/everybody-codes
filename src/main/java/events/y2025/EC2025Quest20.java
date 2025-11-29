package events.y2025;

import common.AbstractQuest;
import common.geo.CardinalDirection2D;
import common.geo.ImmutableCell2D;
import common.grids.Grids;
import common.pathfinding.algorithms.PathFindingAlgorithms;
import common.support.interfaces.Quest20;
import common.support.interfaces.MainEvent2025;
import common.support.params.ExecutionParameters;

import javax.smartcardio.Card;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class EC2025Quest20 extends AbstractQuest implements MainEvent2025, Quest20 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int sideLength = inputLines.getFirst().length();

        int total = 0;

        for (String line: inputLines) {
            int length = line.length();
            char[] chars = line.toCharArray();
            for (int charIndex = 0; charIndex < length - 1; charIndex++) {
                char c1 = chars[charIndex];
                char c2 = chars[charIndex + 1];
                if (c1 == 'T' && c2 == 'T') {
                    total++;
                }
            }
        }

        for (int rowIndex = 0; rowIndex < inputLines.size() - 1; rowIndex++) {
            char[] row1 = inputLines.get(rowIndex).toCharArray();
            char[] row2 = inputLines.get(rowIndex + 1).toCharArray();

            for (int index = 0; index < sideLength - 2 * (rowIndex + 1); index += 2) {
                if (row2[rowIndex + index + 1] == 'T' && row1[rowIndex + index + 1] == 'T') {
                    total++;
                }
            }
        }

        return Integer.toString(total);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        char[][] grid = new char[numberOfRows][numberOfColumns];

        ImmutableCell2D start = null;
        ImmutableCell2D end = null;

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {

            char[] row = inputLines.get(rowIndex).toCharArray();

            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                char c = row[columnIndex];

                if (c == 'S') {
                    start = ImmutableCell2D.of(rowIndex, columnIndex);
                    c = 'T';
                } else if (c == 'E') {
                    end = ImmutableCell2D.of(rowIndex, columnIndex);
                    c = 'T';
                }

                grid[rowIndex][columnIndex] = c;
            }

        }

        if (start == null || end == null) {
            throw new IllegalArgumentException();
        }

        Function<ImmutableCell2D, Set<ImmutableCell2D>> neighborExtractor = currentPosition -> {

            int rowIndex = currentPosition.getRow();
            int columnIndex = currentPosition.getColumn();

            Set<ImmutableCell2D> neighbors = new HashSet<>();

            ImmutableCell2D leftNeighbor = currentPosition.add(CardinalDirection2D.WEST);

            if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, leftNeighbor) && get(grid, leftNeighbor) == 'T') {
                neighbors.add(leftNeighbor);
            }

            ImmutableCell2D rightNeighbor = currentPosition.add(CardinalDirection2D.EAST);

            if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, rightNeighbor) && get(grid, rightNeighbor) == 'T') {
                neighbors.add(rightNeighbor);
            }

            int trueColumnIndex = columnIndex - rowIndex; // because rowIndex is the offset for the row (the number of . at the start of the row in the grid)

            if (trueColumnIndex % 2 == 0) {
                // It's a cell with a top edge: \/
                ImmutableCell2D topNeighbor = currentPosition.add(CardinalDirection2D.NORTH);
                if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, topNeighbor) && get(grid, topNeighbor) == 'T') {
                    neighbors.add(topNeighbor);
                }
            } else {
                // It's a cell with a bottom edge: /\
                ImmutableCell2D bottomNeighbor = currentPosition.add(CardinalDirection2D.SOUTH);
                if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, bottomNeighbor) && get(grid, bottomNeighbor) == 'T') {
                    neighbors.add(bottomNeighbor);
                }
            }

            return neighbors;
        };

        int minDistance = PathFindingAlgorithms.minDistanceSimpleCost(neighborExtractor, start, end);

        return Integer.toString(minDistance);
    }

    private char get(char[][] grid, ImmutableCell2D position) {
        return grid[position.getRow()][position.getColumn()];
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        //inputLines = inputLines.stream().map(String::trim).toList(); // Trimming because the input contains a whitespace at the end of the rows

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        char[][] grid = new char[numberOfRows][numberOfColumns];

        ImmutableCell2D start = null;
        ImmutableCell2D end = null;

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {

            char[] row = inputLines.get(rowIndex).toCharArray();

            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                char c = row[columnIndex];

                if (c == 'S') {
                    start = ImmutableCell2D.of(rowIndex, columnIndex);
                    c = 'T';
                } else if (c == 'E') {
                    end = ImmutableCell2D.of(rowIndex, columnIndex);
                    c = 'T';
                }

                grid[rowIndex][columnIndex] = c;
            }

        }

        if (start == null || end == null) {
            throw new IllegalArgumentException();
        }

        ImmutableCell2D finalEndLocation = end;

        char[][] oneRotationGrid = rotate(numberOfRows, numberOfColumns, grid);
        char[][] twoRotationsGrid = rotate(numberOfRows, numberOfColumns, oneRotationGrid);

        State initialState = new State(start, 0);
        Predicate<State> endingCondition = state -> finalEndLocation.equals(state.currentPosition());

        Function<State, Set<State>> neighborExtractor = currentState -> {

            ImmutableCell2D currentPosition = currentState.currentPosition();
            int indexOfCurrentRotation = currentState.indexOfCurrentRotation();

            int indexOfNextRotation = (indexOfCurrentRotation + 1) % 3;
            char[][] gridAfterJump = switch (indexOfNextRotation) {
                case 0 -> grid;
                case 1 -> oneRotationGrid;
                case 2 -> twoRotationsGrid;
                default -> throw new IllegalStateException();
            };

            int rowIndex = currentPosition.getRow();
            int columnIndex = currentPosition.getColumn();

            Set<State> nextStates = new HashSet<>();

            // Check jumping in place

            if (get(gridAfterJump, currentPosition) == 'T') {
                nextStates.add(new State(currentPosition, indexOfNextRotation));
            }

            ImmutableCell2D leftNeighbor = currentPosition.add(CardinalDirection2D.WEST);

            if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, leftNeighbor) && get(gridAfterJump, leftNeighbor) == 'T') {
                nextStates.add(new State(leftNeighbor, indexOfNextRotation));
            }

            ImmutableCell2D rightNeighbor = currentPosition.add(CardinalDirection2D.EAST);

            if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, rightNeighbor) && get(gridAfterJump, rightNeighbor) == 'T') {
                nextStates.add(new State(rightNeighbor, indexOfNextRotation));
            }

            int trueColumnIndex = columnIndex - rowIndex; // because rowIndex is the offset for the row (the number of . at the start of the row in the grid)

            if (trueColumnIndex % 2 == 0) {
                // It's a cell with a top edge: \/
                ImmutableCell2D topNeighbor = currentPosition.add(CardinalDirection2D.NORTH);
                if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, topNeighbor) && get(gridAfterJump, topNeighbor) == 'T') {
                    nextStates.add(new State(topNeighbor, indexOfNextRotation));
                }
            } else {
                // It's a cell with a bottom edge: /\
                ImmutableCell2D bottomNeighbor = currentPosition.add(CardinalDirection2D.SOUTH);
                if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, bottomNeighbor) && get(gridAfterJump, bottomNeighbor) == 'T') {
                    nextStates.add(new State(bottomNeighbor, indexOfNextRotation));
                }
            }

            return nextStates;
        };

        int minDistance = PathFindingAlgorithms.minDistanceSimpleCost(neighborExtractor, initialState, endingCondition);

        return Integer.toString(minDistance);

    }

    private record State(ImmutableCell2D currentPosition, int indexOfCurrentRotation) {}

    private char[][] rotate(int numberOfRows, int numberOfColumns, char[][] grid) {
        char[][] rotatedGrid = new char[numberOfRows][numberOfColumns];

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {

            // Fill the rotatedGrid with . where required
            for (int columnIndex = 0; columnIndex < rowIndex; columnIndex++) {
                rotatedGrid[rowIndex][columnIndex] = '.';
            }

            for (int columnIndex = numberOfColumns - rowIndex; columnIndex < numberOfColumns; columnIndex++) {
                rotatedGrid[rowIndex][columnIndex] = '.';
            }

            // rowIndex = 0 -> startRow = numberOfRows - 1, startColumn = numberOfRows - 1
            // rowIndex = 1 -> startRow = numberOfRows - 2, startColumn = numberOfRows

            ImmutableCell2D currentPosition = ImmutableCell2D.of(numberOfRows - rowIndex - 1, numberOfRows + rowIndex - 1);

            boolean moveUp = true;

            for (int columnIndex = rowIndex; columnIndex < numberOfColumns - rowIndex; columnIndex++) {
                rotatedGrid[rowIndex][columnIndex] = get(grid, currentPosition);
                currentPosition = currentPosition.add( moveUp ? CardinalDirection2D.NORTH : CardinalDirection2D.WEST );
                moveUp = !moveUp;
            }

        }

        return rotatedGrid;

    }

}
