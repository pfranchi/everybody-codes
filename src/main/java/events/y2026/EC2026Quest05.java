package events.y2026;

import common.AbstractQuest;
import common.geo.ImmutableCell2D;
import common.geo.PrincipalDirection2D;
import common.grids.Grids;
import common.support.interfaces.MainEvent2026;
import common.support.interfaces.Quest05;
import common.support.params.ExecutionParameters;

import java.util.*;

import static common.geo.CardinalDirection2D.*;

public class EC2026Quest05 extends AbstractQuest implements MainEvent2026, Quest05 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Part1Result part1Result = computeLandLocations(inputLines);

        return Integer.toString(part1Result.landLocations().size());
    }

    private boolean isLandOr1(boolean[][] originalGrid, Set<ImmutableCell2D> landLocations, ImmutableCell2D location) {
        return landLocations.contains(location) || originalGrid[location.getRow()][location.column()];
    }

    private record Part1Result(int numberOfRows, int numberOfColumns, Set<ImmutableCell2D> landLocations) {
    }

    private Part1Result computeLandLocations(List<String> inputLines) {
        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        boolean[][] grid = new boolean[numberOfRows][numberOfColumns];

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] row = inputLines.get(rowIndex).toCharArray();
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                grid[rowIndex][columnIndex] = row[columnIndex] == '1';
            }
        }

        int rowIndexOfCenter = numberOfRows / 2; // rounds down to the correct integer
        int columnIndexOfCenter = numberOfColumns / 2;

        ImmutableCell2D center = ImmutableCell2D.of(rowIndexOfCenter, columnIndexOfCenter);

        Set<ImmutableCell2D> landLocations = new HashSet<>();
        landLocations.add(center);

        boolean anyChange;
        do {

            // Compute the current boundary, which is the set of cells adjacent (in all 8 directions) to a land but that are not currently lands
            Set<ImmutableCell2D> boundary = new HashSet<>();
            for (ImmutableCell2D landLocation : landLocations) {
                for (PrincipalDirection2D direction : PrincipalDirection2D.values()) {
                    ImmutableCell2D neighbor = landLocation.add(direction);
                    if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, neighbor)) {
                        boundary.add(landLocation.add(direction));
                    }
                }
            }
            boundary.removeAll(landLocations);

            // Check which boundary cells turn to land

            Set<ImmutableCell2D> locationsThatTurnToLand = new HashSet<>();

            for (ImmutableCell2D cell : boundary) {
                int count = 0;
                for (PrincipalDirection2D direction : PrincipalDirection2D.values()) {
                    ImmutableCell2D neighbor = cell.add(direction);
                    if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, neighbor) && isLandOr1(grid, landLocations, neighbor)) {
                        count++;
                    }
                }

                if (count >= 5) {
                    locationsThatTurnToLand.add(cell);
                }

            }

            anyChange = !locationsThatTurnToLand.isEmpty();

            landLocations.addAll(locationsThatTurnToLand);

        } while (anyChange);

        return new Part1Result(numberOfRows, numberOfColumns, landLocations);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        Part2Result part2Result = computePart2Result(inputLines);
        return Integer.toString(part2Result.sandLocations().size());
    }

    private boolean isAnyNeighborNotALand(Set<ImmutableCell2D> landLocations, ImmutableCell2D location) {
        return Arrays.stream(PrincipalDirection2D.values()).anyMatch(direction -> !landLocations.contains(location.add(direction)));
    }

    private record Part2Result(Set<ImmutableCell2D> landLocations, Set<ImmutableCell2D> sandLocations) {}

    private Part2Result computePart2Result(List<String> inputLines) {

        Part1Result part1Result = computeLandLocations(inputLines);
        int numberOfRows = part1Result.numberOfRows();
        int numberOfColumns = part1Result.numberOfColumns();
        Set<ImmutableCell2D> landLocations = part1Result.landLocations();

        // First pass: any land that touches a non-land (= water) becomes sand
        Set<ImmutableCell2D> sandLocations = new HashSet<>();

        for (ImmutableCell2D cell : landLocations) {
            if (isAnyNeighborNotALand(landLocations, cell)) {
                sandLocations.add(cell);
            }
        }

        landLocations.removeAll(sandLocations);

        // Repeat the next pass until there is no change

        boolean anyChange;
        do {

            Set<ImmutableCell2D> newSandLocations = new HashSet<>();

            for (ImmutableCell2D cell : landLocations) {

                int count = 0;
                for (PrincipalDirection2D direction : PrincipalDirection2D.values()) {
                    ImmutableCell2D neighbor = cell.add(direction);
                    if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, neighbor) && sandLocations.contains(neighbor)) {
                        count++;
                    }
                }

                if (count >= 5) {
                    newSandLocations.add(cell);
                }

            }

            anyChange = !newSandLocations.isEmpty();

            sandLocations.addAll(newSandLocations);
            landLocations.removeAll(newSandLocations);

        } while (anyChange);

        return new Part2Result(landLocations, sandLocations);

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Part2Result part2Result = computePart2Result(inputLines);
        Set<ImmutableCell2D> landLocations = part2Result.landLocations();
        Set<ImmutableCell2D> sandLocations = part2Result.sandLocations();

        Set<ImmutableCell2D> treeLocations = new HashSet<>();

        boolean anyChange;
        do {

            Set<ImmutableCell2D> newTreeLocations = new HashSet<>();

            for (ImmutableCell2D x : landLocations) {
                Set<ImmutableCell2D> potatoStampLocations = potatoStampLocations(x);
                if (potatoStampLocations.stream().allMatch(location -> isLandOrTree(landLocations, treeLocations, location))) {
                    newTreeLocations.add(x);
                }
            }

            anyChange = !newTreeLocations.isEmpty();

            landLocations.removeAll(newTreeLocations);
            treeLocations.addAll(newTreeLocations);

        } while (anyChange);

        // Forest-growth steps

        for (int step = 1; step <= 2; step++) {

            Set<ImmutableCell2D> newTreeLocations = new HashSet<>();

            for (ImmutableCell2D cell : landLocations) {
                int treeCount = 0;
                int sandCount = 0;
                for (PrincipalDirection2D direction : PrincipalDirection2D.values()) {
                    ImmutableCell2D neighbor = cell.add(direction);
                    if (treeLocations.contains(neighbor)) {
                        treeCount++;
                    } else if (sandLocations.contains(neighbor)) {
                        sandCount++;
                    }
                }

                if (treeCount >= 4 && sandCount == 0) {
                    newTreeLocations.add(cell);
                }

            }

            landLocations.removeAll(newTreeLocations);
            treeLocations.addAll(newTreeLocations);

        }

        return Integer.toString(treeLocations.size());
    }

    private Set<ImmutableCell2D> potatoStampLocations(ImmutableCell2D x) {
        Set<ImmutableCell2D> potatoStampLocations = new HashSet<>();

        ImmutableCell2D oneStepNorth = x.add(NORTH);
        addTwoOnBothSides(potatoStampLocations, oneStepNorth);
        potatoStampLocations.add(oneStepNorth);

        ImmutableCell2D twoStepsNorth = oneStepNorth.add(NORTH);
        potatoStampLocations.add(twoStepsNorth);
        potatoStampLocations.add(twoStepsNorth.add(WEST));
        potatoStampLocations.add(twoStepsNorth.add(EAST));

        addTwoOnBothSides(potatoStampLocations, x);

        ImmutableCell2D oneStepSouth = x.add(SOUTH);
        addTwoOnBothSides(potatoStampLocations, oneStepSouth);
        potatoStampLocations.add(oneStepSouth);

        ImmutableCell2D twoStepsSouth = oneStepSouth.add(SOUTH);
        potatoStampLocations.add(twoStepsSouth);
        potatoStampLocations.add(twoStepsSouth.add(WEST));
        potatoStampLocations.add(twoStepsSouth.add(EAST));

        return potatoStampLocations;

    }

    private void addTwoOnBothSides(Set<ImmutableCell2D> locations, ImmutableCell2D cell) {
        locations.add(cell.add(WEST).add(WEST));
        locations.add(cell.add(WEST));
        locations.add(cell.add(EAST));
        locations.add(cell.add(EAST).add(EAST));
    }

    private boolean isLandOrTree(Set<ImmutableCell2D> landLocations, Set<ImmutableCell2D> treeLocations, ImmutableCell2D location) {
        return landLocations.contains(location) || treeLocations.contains(location);
    }

}
