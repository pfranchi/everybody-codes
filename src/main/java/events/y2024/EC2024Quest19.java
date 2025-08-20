package events.y2024;

import common.AbstractQuest;
import common.Sections;
import common.geo.ImmutableCell2D;
import common.geo.PrincipalDirection2D;
import common.support.interfaces.MainEvent2024;
import common.support.interfaces.Quest19;
import common.support.params.ExecutionParameters;

import java.util.*;

import static common.geo.PrincipalDirection2D.*;

public class EC2024Quest19 extends AbstractQuest implements MainEvent2024, Quest19 {

    private static final List<PrincipalDirection2D> DIRECTIONS_CLOCKWISE = List.of(NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST, NORTH);
    private static final List<PrincipalDirection2D> DIRECTIONS_COUNTERCLOCKWISE = DIRECTIONS_CLOCKWISE.reversed();

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        char[] instructions = inputLines.getFirst().toCharArray();
        int numberOfInstructions = instructions.length;

        log("Instructions are: ");

        List<List<String>> sections = Sections.splitAtBlankLines(input);
        List<String> gridInputLines = sections.getLast();

        int numberOfRows = gridInputLines.size();
        int numberOfColumns = gridInputLines.getFirst().length();

        char[][] grid = new char[numberOfRows][numberOfColumns];

        ImmutableCell2D startArrowPosition = null;
        ImmutableCell2D endArrowPosition = null;

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            grid[rowIndex] = gridInputLines.get(rowIndex).toCharArray();

            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {

                char c = grid[rowIndex][columnIndex];

                if (c == '>') {
                    startArrowPosition = ImmutableCell2D.of(rowIndex, columnIndex);
                } else if (c == '<') {
                    endArrowPosition = ImmutableCell2D.of(rowIndex, columnIndex);
                }

            }

        }

        if (startArrowPosition == null || endArrowPosition == null) {
            throw new IllegalArgumentException("Start arrow > or end arrow < are missing");
        }

        log("Grid is");
        log(Arrays.deepToString(grid));

        List<ImmutableCell2D> rotationPoints = new ArrayList<>();
        for (int rowIndex = 1; rowIndex < numberOfRows - 1; rowIndex++) {
            for (int columnIndex = 1; columnIndex < numberOfColumns - 1; columnIndex++) {
                rotationPoints.add(ImmutableCell2D.of(rowIndex, columnIndex));
            }
        }

        int numberOfRotationsPoints = rotationPoints.size();

        int step = 0;

        while (true) {

            ImmutableCell2D rotationPoint = rotationPoints.get(step % numberOfRotationsPoints);
            char instruction = instructions[step % numberOfInstructions];

            // Perform rotation
            emptyLine();
            log("Rotation {} around {}", instruction, rotationPoint);

            List<PrincipalDirection2D> directionsOfRotation = instruction == 'R' ? DIRECTIONS_CLOCKWISE : DIRECTIONS_COUNTERCLOCKWISE;

            ImmutableCell2D startPosition = rotationPoint.add(directionsOfRotation.getFirst());

            ImmutableCell2D previousPosition = startPosition;
            char previousValue = get(grid, startPosition);

            ImmutableCell2D nextStartArrowPosition = null;
            ImmutableCell2D nextEndArrowPosition = null;

            for (int index = 1; index < 8; index++) {

                ImmutableCell2D currentPosition = rotationPoint.add(directionsOfRotation.get(index));
                char currentValue = get(grid, currentPosition);

                grid[currentPosition.getRow()][currentPosition.getColumn()] = previousValue;

                previousValue = currentValue;

                if (previousPosition.equals(startArrowPosition)) {
                    nextStartArrowPosition = currentPosition;
                }
                if (previousPosition.equals(endArrowPosition)) {
                    nextEndArrowPosition = currentPosition;
                }

                previousPosition = currentPosition;

            }

            grid[startPosition.getRow()][startPosition.getColumn()] = previousValue;

            log("Grid is");
            log(Arrays.deepToString(grid));

            if (nextStartArrowPosition != null) {
                log("Moved start arrow from {} to {}", startArrowPosition, nextStartArrowPosition);
                startArrowPosition = nextStartArrowPosition;
            }

            if (nextEndArrowPosition != null) {
                log("Moved end arrow from {} to {}", endArrowPosition, nextEndArrowPosition);
                endArrowPosition = nextEndArrowPosition;
            }

            // Check exit condition
            if (startArrowPosition.getRow() == endArrowPosition.getRow() && startArrowPosition.getColumn() < endArrowPosition.getColumn() ) {
                log("Found solution!!!");
                break;
            }

            step++;

        }

        char[] rowWithMessage = grid[startArrowPosition.getRow()];

        String message = new String(rowWithMessage);

        return message.substring(startArrowPosition.getColumn() + 1, endArrowPosition.getColumn());
    }

    private char get(char[][] grid, ImmutableCell2D position) {
        return grid[position.getRow()][position.getColumn()];
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        char[] instructions = inputLines.getFirst().toCharArray();
        int numberOfInstructions = instructions.length;

        log("Instructions are: ");

        List<List<String>> sections = Sections.splitAtBlankLines(input);
        List<String> gridInputLines = sections.getLast();

        int numberOfRows = gridInputLines.size();
        int numberOfColumns = gridInputLines.getFirst().length();

        char[][] grid = new char[numberOfRows][numberOfColumns];

        log("Grid is");
        log(Arrays.deepToString(grid));

        List<ImmutableCell2D> rotationPoints = new ArrayList<>();
        for (int rowIndex = 1; rowIndex < numberOfRows - 1; rowIndex++) {
            for (int columnIndex = 1; columnIndex < numberOfColumns - 1; columnIndex++) {
                rotationPoints.add(ImmutableCell2D.of(rowIndex, columnIndex));
            }
        }

        int numberOfRotationsPoints = rotationPoints.size();

        for (int round = 1; round <= 100; round++) {

            for (int step = 0; step < numberOfRotationsPoints; step++) {

                ImmutableCell2D rotationPoint = rotationPoints.get(step % numberOfRotationsPoints);
                char instruction = instructions[step % numberOfInstructions];

                // Perform rotation

                List<PrincipalDirection2D> directionsOfRotation = instruction == 'R' ? DIRECTIONS_CLOCKWISE : DIRECTIONS_COUNTERCLOCKWISE;

                ImmutableCell2D startPosition = rotationPoint.add(directionsOfRotation.getFirst());

                char previousValue = get(grid, startPosition);

                for (int index = 1; index < 8; index++) {

                    ImmutableCell2D currentPosition = rotationPoint.add(directionsOfRotation.get(index));
                    char currentValue = get(grid, currentPosition);

                    grid[currentPosition.getRow()][currentPosition.getColumn()] = previousValue;

                    previousValue = currentValue;

                }

                grid[startPosition.getRow()][startPosition.getColumn()] = previousValue;

            }

        }

        ImmutableCell2D startArrowPosition = null;
        ImmutableCell2D endArrowPosition = null;
        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {

            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {

                char c = grid[rowIndex][columnIndex];

                if (c == '>') {
                    startArrowPosition = ImmutableCell2D.of(rowIndex, columnIndex);
                } else if (c == '<') {
                    endArrowPosition = ImmutableCell2D.of(rowIndex, columnIndex);
                }

            }

        }

        if (startArrowPosition == null || endArrowPosition == null) {
            throw new IllegalArgumentException("Start arrow > or end arrow < are missing");
        }

        char[] rowWithMessage = grid[startArrowPosition.getRow()];

        String message = new String(rowWithMessage);

        return message.substring(startArrowPosition.getColumn() + 1, endArrowPosition.getColumn());

    }

    private void printGrid(char[][] grid) {
        for (char[] row: grid) {
            log(new String(row));
        }
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        char[] instructions = inputLines.getFirst().toCharArray();
        int numberOfInstructions = instructions.length;

        log("Instructions are: ");

        List<List<String>> sections = Sections.splitAtBlankLines(input);
        List<String> gridInputLines = sections.getLast();

        int numberOfRows = gridInputLines.size();
        int numberOfColumns = gridInputLines.getFirst().length();

        List<ImmutableCell2D> rotationPoints = new ArrayList<>();
        for (int rowIndex = 1; rowIndex < numberOfRows - 1; rowIndex++) {
            for (int columnIndex = 1; columnIndex < numberOfColumns - 1; columnIndex++) {
                rotationPoints.add(ImmutableCell2D.of(rowIndex, columnIndex));
            }
        }

        int numberOfRotationsPoints = rotationPoints.size();

        List<Map<ImmutableCell2D, ImmutableCell2D>> singleStepTransformationMaps = new ArrayList<>();

        // Determine the transformation applied to the grid by one round

        for (int step = 0; step < numberOfRotationsPoints; step++) {

            ImmutableCell2D rotationPoint = rotationPoints.get(step % numberOfRotationsPoints);
            char instruction = instructions[step % numberOfInstructions];

            // Perform rotation

            List<PrincipalDirection2D> directionsOfRotation = instruction == 'R' ? DIRECTIONS_CLOCKWISE : DIRECTIONS_COUNTERCLOCKWISE;

            Map<ImmutableCell2D, ImmutableCell2D> singleStepTransformationMap = new HashMap<>();

            for (int index = 1; index < 9; index++) {
                ImmutableCell2D previousPosition = rotationPoint.add(directionsOfRotation.get(index - 1));
                ImmutableCell2D currentPosition = rotationPoint.add(directionsOfRotation.get(index));

                singleStepTransformationMap.put(previousPosition, currentPosition);

            }

            singleStepTransformationMaps.add(singleStepTransformationMap);

        }

        log("Single step maps computed");

        char[][] grid1 = new char[numberOfRows][numberOfColumns];

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            grid1[rowIndex] = gridInputLines.get(rowIndex).toCharArray();
        }

        log("Grid1 is");
        log(Arrays.deepToString(grid1));

        List<ImmutableCell2D> cellsWithDigits = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {

                char c = grid1[rowIndex][columnIndex];

                if (Character.isDigit(c)) {
                    cellsWithDigits.add(ImmutableCell2D.of(rowIndex, columnIndex));
                }

            }
        }

        log("There are {} digits, at {}", cellsWithDigits.size(), cellsWithDigits);

        // Compose the single steps for all cells
        Map<ImmutableCell2D, ImmutableCell2D> composedMap = new HashMap<>();
        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                ImmutableCell2D from = ImmutableCell2D.of(rowIndex, columnIndex);

                ImmutableCell2D to = from;
                for (Map<ImmutableCell2D, ImmutableCell2D> singleStep: singleStepTransformationMaps) {
                    to = singleStep.getOrDefault(to, to);
                }

                composedMap.put(from, to);

            }
        }

        log("Transformation map computed");

        // Repeat the operation 1048576000 times but perform it only for the digit cells

        Map<ImmutableCell2D, ImmutableCell2D> fullTransformation = new HashMap<>();

        for (int i = 0, cellsWithDigitsSize = cellsWithDigits.size(); i < cellsWithDigitsSize; i++) {
            ImmutableCell2D cellWithDigit = cellsWithDigits.get(i);

            ImmutableCell2D to = cellWithDigit;

            for (long step = 1; step <= 1048576000L; step++) {
                to = composedMap.getOrDefault(to, to);
            }

            fullTransformation.put(cellWithDigit, to);

            log("Computed digit {} / {}", i, cellsWithDigitsSize);

        }

        log("Grid is");
        printTransformationMap(numberOfRows, numberOfColumns, grid1, fullTransformation);

        return NOT_IMPLEMENTED;

    }

    private void printTransformationMap(int numberOfRows, int numberOfColumns, char[][] originalGrid,
                                        Map<ImmutableCell2D, ImmutableCell2D> transformationMap) {
        char[][] grid = new char[numberOfRows][numberOfColumns];
        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                grid[rowIndex][columnIndex] = '.';
            }
        }

        for (Map.Entry<ImmutableCell2D, ImmutableCell2D> entry: transformationMap.entrySet()) {
            ImmutableCell2D from = entry.getKey();
            ImmutableCell2D to = entry.getValue();
            char c = get(originalGrid, from);

            grid[to.getRow()][to.getColumn()] = c;

        }

        printGrid(grid);

    }

}
