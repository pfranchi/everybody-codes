package events.y2026;

import common.AbstractQuest;
import common.geo.Cell2D;
import common.geo.ImmutableCell2D;
import common.geo.PrincipalDirection2D;
import common.grids.Grids;
import common.support.interfaces.MainEvent2026;
import common.support.interfaces.Quest08;
import common.support.params.ExecutionParameters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EC2026Quest08 extends AbstractQuest implements MainEvent2026, Quest08 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        ParsedInput parsedInput = ParsedInput.from(inputLines);

        int numberOfRows = parsedInput.numberOfRows();
        int numberOfColumns = parsedInput.numberOfColumns();
        Integer[][] grid = parsedInput.grid();
        Set<ImmutableCell2D> edgeLocations = parsedInput.edgeLocations();

        int totalEnergy = edgeLocations
                .stream()
                .mapToInt(startingLocation -> totalEnergy(numberOfRows, numberOfColumns, grid, startingLocation, Round.R1))
                .sum();

        return Integer.toString(totalEnergy);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        ParsedInput parsedInput = ParsedInput.from(inputLines);

        int numberOfRows = parsedInput.numberOfRows();
        int numberOfColumns = parsedInput.numberOfColumns();
        Integer[][] grid = parsedInput.grid();
        Set<ImmutableCell2D> edgeLocations = parsedInput.edgeLocations();

        int totalEnergy = Arrays.stream(Round.values())
                .mapToInt(round -> edgeLocations
                        .stream()
                        .mapToInt(startingLocation -> totalEnergy(numberOfRows, numberOfColumns, grid, startingLocation, round))
                        .sum())
                .sum();

        return Integer.toString(totalEnergy);

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        ParsedInput parsedInput = ParsedInput.from(inputLines);

        int numberOfRows = parsedInput.numberOfRows();
        int numberOfColumns = parsedInput.numberOfColumns();
        Integer[][] grid = parsedInput.grid();
        Set<ImmutableCell2D> edgeLocations = parsedInput.edgeLocations();

        int roundIndex = 0;

        EnergyAndNewWarriors total = new EnergyAndNewWarriors(0, 0);

        while (total.getNumberOfNewWarriors() < 100000) {

            Round round = Round.values()[roundIndex];

            edgeLocations.stream()
                    .map(startingLocation -> totalEnergyAndNewWarriors(numberOfRows, numberOfColumns, grid, startingLocation, round))
                    .forEach(total::add);

            roundIndex++;
            roundIndex %= Round.values().length;

        }

        return Integer.toString(total.getTotalEnergy());
    }

    private record ParsedInput(int numberOfRows, int numberOfColumns, Integer[][] grid, Set<ImmutableCell2D> edgeLocations) {

        public static ParsedInput from(List<String> inputLines) {

            int originalNumberOfRows = inputLines.size();
            int originalNumberOfColumns = inputLines.getFirst().length();

            int numberOfRows = originalNumberOfRows + 2;
            int numberOfColumns = originalNumberOfColumns + 2;

            Integer[][] grid =  new Integer[numberOfRows][numberOfColumns];
            for (int rowIndex = 0; rowIndex < originalNumberOfRows; rowIndex++) {
                char[] row =  inputLines.get(rowIndex).toCharArray();
                for (int columnIndex = 0; columnIndex < originalNumberOfColumns; columnIndex++) {
                    char c =  row[columnIndex];
                    grid[rowIndex + 1][columnIndex + 1] = c == '.' ? null : Integer.parseInt(String.valueOf(c));
                }
            }

            Set<ImmutableCell2D> edgeLocations = new HashSet<>();
            for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
                edgeLocations.add(ImmutableCell2D.of(rowIndex, 0));
                edgeLocations.add(ImmutableCell2D.of(rowIndex, numberOfColumns - 1));
            }
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                edgeLocations.add(ImmutableCell2D.of(0, columnIndex));
                edgeLocations.add(ImmutableCell2D.of(numberOfRows - 1, columnIndex));
            }

            return new ParsedInput(numberOfRows, numberOfColumns, grid, edgeLocations);

        }

    }

    private enum Round {
        R1(PrincipalDirection2D.SOUTH_EAST),
        R2(PrincipalDirection2D.SOUTH_WEST),
        R3(PrincipalDirection2D.WEST, 2),
        R4(PrincipalDirection2D.NORTH_WEST),
        R5(PrincipalDirection2D.NORTH_EAST),
        R6(PrincipalDirection2D.EAST, 2);

        private final PrincipalDirection2D movingDirection;
        private int stepLength = 1;

        Round(PrincipalDirection2D movingDirection) {
            this.movingDirection = movingDirection;
        }

        Round(PrincipalDirection2D movingDirection, int stepLength) {
            this.movingDirection = movingDirection;
            this.stepLength = stepLength;
        }

        public PrincipalDirection2D getMovingDirection() {
            return movingDirection;
        }

        public int getStepLength() {
            return stepLength;
        }
    }

    private int totalEnergy(int numberOfRows, int numberOfColumns, Integer[][] grid, ImmutableCell2D startingLocation, Round round) {

        ImmutableCell2D currentCell = startingLocation;

        while (Grids.isPositionInGrid(numberOfRows, numberOfColumns, currentCell) && grid[currentCell.getRow()][currentCell.getColumn()] == null) {
            currentCell = currentCell.add(round.getMovingDirection());
        }

        Cell2D step = ImmutableCell2D.nTimes(round.getMovingDirection(), round.getStepLength());

        int totalEnergy = 0;
        int currentShadowHeight = 0;

        while (Grids.isPositionInGrid(numberOfRows, numberOfColumns, currentCell)) {

            if (currentShadowHeight > 0) {
                currentShadowHeight--;
            }

            // Check the current cell

            Integer currentValue = grid[currentCell.getRow()][currentCell.getColumn()];

            if (currentValue != null) {

                // There is an Ent here
                if (currentValue > currentShadowHeight) {
                    totalEnergy++;
                    currentShadowHeight = currentValue;
                }

            }

            // Move to the next cell
            currentCell = currentCell.add(step);

        }

        return totalEnergy;

    }

    private static final class EnergyAndNewWarriors {

        private int totalEnergy;
        private int numberOfNewWarriors;

        public EnergyAndNewWarriors(int totalEnergy, int numberOfNewWarriors) {
            this.totalEnergy = totalEnergy;
            this.numberOfNewWarriors = numberOfNewWarriors;
        }

        public void add(EnergyAndNewWarriors other) {
            this.totalEnergy += other.totalEnergy;
            this.numberOfNewWarriors += other.numberOfNewWarriors;
        }

        public int getTotalEnergy() {
            return totalEnergy;
        }

        public int getNumberOfNewWarriors() {
            return numberOfNewWarriors;
        }
    }

    private EnergyAndNewWarriors totalEnergyAndNewWarriors(int numberOfRows, int numberOfColumns, Integer[][] grid, ImmutableCell2D startingLocation, Round round) {
        return computeTotalEnergyStartingFromPart3(numberOfRows, numberOfColumns, grid, startingLocation.getRow(), startingLocation.getColumn(), round.getMovingDirection(), round.getStepLength());
    }

    private EnergyAndNewWarriors computeTotalEnergyStartingFromPart3(int numberOfRows, int numberOfColumns, Integer[][] grid,
                                                                     int startingRow, int startingColumn,
                                                                     PrincipalDirection2D movingDirection, int stepLength) {

        int currentShadowHeight = 0;

        ImmutableCell2D currentCell = ImmutableCell2D.of(startingRow, startingColumn);

        while (Grids.isPositionInGrid(numberOfRows, numberOfColumns, currentCell) && grid[currentCell.getRow()][currentCell.getColumn()] == null) {
            currentCell = currentCell.add(movingDirection);
        }

        Cell2D step = ImmutableCell2D.nTimes(movingDirection, stepLength);

        Set<ImmutableCell2D> locationsThatReceiveEnergy = new HashSet<>();

        while (Grids.isPositionInGrid(numberOfRows, numberOfColumns, currentCell)) {

            if (currentShadowHeight > 0) {
                currentShadowHeight--;
            }

            // Check the current cell

            Integer currentValue = grid[currentCell.getRow()][currentCell.getColumn()];

            if (currentValue != null) {

                // There is an Ent here
                if (currentValue > currentShadowHeight) {
                    locationsThatReceiveEnergy.add(currentCell);
                    currentShadowHeight = currentValue;
                }

                if (currentValue <= 0) {
                    // A seed is growing here. Increase it now
                    grid[currentCell.getRow()][currentCell.getColumn()]++;
                }

            }

            // Move to the next cell
            currentCell = currentCell.add(step);

        }

        int totalEnergy = locationsThatReceiveEnergy.size();

        int numberOfNewWarriors = 0;
        for (Cell2D locationThatReceivesEnergy : locationsThatReceiveEnergy) {

            int originalValue = grid[locationThatReceivesEnergy.getRow()][locationThatReceivesEnergy.getColumn()];

            if (originalValue == 9) {
                numberOfNewWarriors++;
                grid[locationThatReceivesEnergy.getRow()][locationThatReceivesEnergy.getColumn()] = -2;
            } else {
                grid[locationThatReceivesEnergy.getRow()][locationThatReceivesEnergy.getColumn()]++;
            }

        }

        return new EnergyAndNewWarriors(totalEnergy, numberOfNewWarriors);
    }

}
