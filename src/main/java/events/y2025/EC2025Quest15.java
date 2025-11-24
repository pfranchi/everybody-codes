package events.y2025;

import common.AbstractQuest;
import common.Strings;
import common.geo.CardinalDirection2D;
import common.geo.Cell2D;
import common.geo.ImmutableCell2D;
import common.geo.MutableCell2D;
import common.pathfinding.PathFinder;
import common.pathfinding.algorithms.PathFindingAlgorithms;
import common.stats.Cell2DMinMaxStatistics;
import common.stats.IntMinMaxStatistics;
import common.support.interfaces.MainEvent2025;
import common.support.interfaces.Quest15;
import common.support.params.ExecutionParameters;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static common.geo.CardinalDirection2D.*;

public class EC2025Quest15 extends AbstractQuest implements MainEvent2025, Quest15 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return unoptimizedImplementation(input);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return unoptimizedImplementation(input);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return optimizedVersion(input);
    }

    private String unoptimizedImplementation(String input) {
        Set<ImmutableCell2D> wallLocations = new HashSet<>();

        MutableCell2D currentLocation = MutableCell2D.of(0, 0);
        CardinalDirection2D currentFacing = NORTH;

        wallLocations.add(ImmutableCell2D.copyOf(currentLocation));

        Cell2DMinMaxStatistics locationStats = new Cell2DMinMaxStatistics();

        for (String segmentInstruction: Strings.firstRow(input).split(",")) {

            char c = segmentInstruction.charAt(0);
            int steps = Integer.parseInt(segmentInstruction.substring(1));

            currentFacing = switch (c) {
                case 'L' -> currentFacing.rotateLeft();
                case 'R' -> currentFacing.rotateRight();
                default -> throw new IllegalArgumentException("Invalid char " + c);
            };

            for (int step = 1; step <= steps; step++) {
                currentLocation.move(currentFacing);

                wallLocations.add(ImmutableCell2D.copyOf(currentLocation));
                locationStats.accept(currentLocation);

            }

        }

        ImmutableCell2D start = ImmutableCell2D.of(0, 0);
        ImmutableCell2D end = ImmutableCell2D.copyOf(currentLocation);

        wallLocations.removeAll(List.of(start, end));

        IntMinMaxStatistics rowStats = locationStats.getRowStatistics();
        int minRow = rowStats.getMin();
        int maxRow = rowStats.getMax();
        IntMinMaxStatistics columnStats = locationStats.getColumnStatistics();
        int minColumn = columnStats.getMin();
        int maxColumn = columnStats.getMax();

        int numberOfRows = maxRow - minRow + 1;
        int numberOfColumns = maxColumn - minColumn + 1;

        boolean[][] grid = new boolean[numberOfRows][numberOfColumns];

        for (ImmutableCell2D unadjustedWallLocation: wallLocations) {

            int adjustedRow = unadjustedWallLocation.getRow() - minRow;
            int adjustedColumn = unadjustedWallLocation.getColumn() - minColumn;

            grid[adjustedRow][adjustedColumn] = true;

        }

        ImmutableCell2D adjustedStart = ImmutableCell2D.of(start.getRow() - minRow, start.getColumn() - minColumn);
        ImmutableCell2D adjustedEnd = ImmutableCell2D.of(end.getRow() - minRow, end.getColumn() - minColumn);

        PathFinder<Cell2D> pathFinder = PathFinder.forSimpleMaze(grid);

        int distance = pathFinder.shortestDistance(adjustedStart, adjustedEnd);

        return Integer.toString(distance);
    }

    private String optimizedVersion(String input) {
        MutableCell2D currentLocation = MutableCell2D.of(0, 0);
        CardinalDirection2D currentFacing = NORTH;

        Cell2DMinMaxStatistics locationStats = new Cell2DMinMaxStatistics();

        List<Wall> walls = new ArrayList<>();

        NavigableSet<Integer> relevantRows = new TreeSet<>();
        relevantRows.add(0);
        NavigableSet<Integer> relevantColumns = new TreeSet<>();
        relevantColumns.add(0);

        for (String segmentInstruction: Strings.firstRow(input).split(",")) {

            ImmutableCell2D wallStart = ImmutableCell2D.copyOf(currentLocation);

            char c = segmentInstruction.charAt(0);
            int steps = Integer.parseInt(segmentInstruction.substring(1));

            currentFacing = switch (c) {
                case 'L' -> currentFacing.rotateLeft();
                case 'R' -> currentFacing.rotateRight();
                default -> throw new IllegalArgumentException("Invalid char " + c);
            };

            currentLocation.move(ImmutableCell2D.nTimes(currentFacing, steps));

            ImmutableCell2D wallEnd = ImmutableCell2D.copyOf(currentLocation);

            relevantRows.add(wallEnd.row());
            relevantColumns.add(wallEnd.column());

            locationStats.accept(currentLocation);

            Wall wall;
            if (currentFacing.isHorizontal()) {

                int columnStart = Integer.min(wallStart.getColumn(), wallEnd.getColumn());
                int columnEnd = Integer.max(wallStart.getColumn(), wallEnd.getColumn());

                wall = new HorizontalWall(wallStart.getRow(), columnStart, columnEnd);
            } else {

                int rowStart = Integer.min(wallStart.getRow(), wallEnd.getRow());
                int rowEnd = Integer.max(wallStart.getRow(), wallEnd.getRow());

                wall = new VerticalWall(wallStart.getColumn(), rowStart, rowEnd);
            }

            walls.add(wall);

        }

        ImmutableCell2D start = ImmutableCell2D.of(0, 0);
        ImmutableCell2D end = ImmutableCell2D.copyOf(currentLocation);

        IntMinMaxStatistics rowStats = locationStats.getRowStatistics();
        int minRow = rowStats.getMin();
        int maxRow = rowStats.getMax();
        IntMinMaxStatistics columnStats = locationStats.getColumnStatistics();
        int minColumn = columnStats.getMin();
        int maxColumn = columnStats.getMax();

        List<Integer> listOfRelevantRows = new ArrayList<>(relevantRows);
        List<Integer> listOfRelevantColumns = new ArrayList<>(relevantColumns);

        int numberOfBlockRows = listOfRelevantRows.size() + 1;
        int numberOfBlockColumns = listOfRelevantColumns.size() + 1;

        Block[][] blocks = new Block[numberOfBlockRows][numberOfBlockColumns];

        // First row of blocks (blockRow = 0):

        for (int blockColumn = 0; blockColumn < numberOfBlockColumns; blockColumn++) {

            if (blockColumn == 0) {

                Block topLeftBlock = new Block(0, blockColumn, minRow - 2, minRow,
                        minColumn - 2, minColumn, Set.of(NORTH, WEST), Set.of(SOUTH, EAST));
                blocks[0][blockColumn] = topLeftBlock;


                Block bottomLeftBlock = new Block(numberOfBlockRows - 1, blockColumn, maxRow, maxRow + 2,
                        minColumn - 2, minColumn, Set.of(SOUTH, WEST), Set.of(NORTH, EAST));
                blocks[numberOfBlockRows - 1][blockColumn] = bottomLeftBlock;

                //
                for (int blockRow = 1; blockRow < numberOfBlockRows - 1; blockRow++) {

                    // minRowInclusive, maxRowInclusive, and the EAST wall depend on the input

                    int minRowInclusive = listOfRelevantRows.get(blockRow - 1);
                    int maxRowInclusive = listOfRelevantRows.get(blockRow);

                    boolean containsEastWall = isInAnyWall(walls, ImmutableCell2D.of(minRowInclusive + 1, minColumn));

                    Set<CardinalDirection2D> surroundingWallDirections = new HashSet<>(Set.of(WEST));
                    Set<CardinalDirection2D> allowedMovementDirections = new HashSet<>(Set.of(NORTH, SOUTH));

                    if (containsEastWall) {
                        surroundingWallDirections.add(EAST);
                    } else {
                        allowedMovementDirections.add(EAST);
                    }

                    blocks[blockRow][blockColumn] = new Block(blockRow, blockColumn, minRowInclusive, maxRowInclusive,
                            minColumn - 2, minColumn, surroundingWallDirections, allowedMovementDirections);

                }


            } else if (blockColumn == numberOfBlockColumns - 1) {

                Block topRightBlock = new Block(0, blockColumn, minRow - 2, minRow,
                        maxColumn, maxColumn + 2, Set.of(NORTH, EAST), Set.of(SOUTH, WEST));
                blocks[0][blockColumn] = topRightBlock;

                Block bottomRightBlock = new Block(numberOfBlockRows - 1, blockColumn, maxRow, maxRow + 2,
                        maxColumn, maxColumn + 2, Set.of(SOUTH, EAST), Set.of(NORTH, WEST));
                blocks[numberOfBlockRows - 1][blockColumn] = bottomRightBlock;


                for (int blockRow = 1; blockRow < numberOfBlockRows - 1; blockRow++) {

                    // minRowInclusive, maxRowInclusive, and the WEST wall depend on the input

                    int minRowInclusive = listOfRelevantRows.get(blockRow - 1);
                    int maxRowInclusive = listOfRelevantRows.get(blockRow);

                    boolean containsWestWall = isInAnyWall(walls, ImmutableCell2D.of(minRowInclusive + 1, maxColumn));

                    Set<CardinalDirection2D> surroundingWallDirections = new HashSet<>(Set.of(EAST));
                    Set<CardinalDirection2D> allowedMovementDirections = new HashSet<>(Set.of(NORTH, SOUTH));

                    if (containsWestWall) {
                        surroundingWallDirections.add(WEST);
                    } else {
                        allowedMovementDirections.add(WEST);
                    }

                    Block block = new Block(blockRow, blockColumn, minRowInclusive, maxRowInclusive, maxColumn,
                            maxColumn + 2, surroundingWallDirections, allowedMovementDirections);
                    blocks[blockRow][blockColumn] = block;

                }


            } else {

                int minColumnInclusive = listOfRelevantColumns.get(blockColumn - 1);
                int maxColumnInclusive = listOfRelevantColumns.get(blockColumn);

                // Block in the top row
                boolean containsSouthWall = isInAnyWall(walls, ImmutableCell2D.of(minRow, minColumnInclusive + 1));
                Set<CardinalDirection2D> topBlockSurroundingWallDirections = new HashSet<>(Set.of(NORTH));
                Set<CardinalDirection2D> topBlockAllowedMovementDirections = new HashSet<>(Set.of(EAST, WEST));
                if (containsSouthWall) {
                    topBlockSurroundingWallDirections.add(SOUTH);
                } else {
                    topBlockAllowedMovementDirections.add(SOUTH);
                }
                Block topRowBlock = new Block(0, blockColumn, minRow - 2, minRow, minColumnInclusive,
                        maxColumnInclusive, topBlockSurroundingWallDirections, topBlockAllowedMovementDirections);
                blocks[0][blockColumn] = topRowBlock;

                // Block in the bottom row
                boolean containsNorthWall = isInAnyWall(walls, ImmutableCell2D.of(maxRow, minColumnInclusive + 1));
                Set<CardinalDirection2D> bottomBlockSurroundingWallDirections = new HashSet<>(Set.of(SOUTH));
                Set<CardinalDirection2D> bottomBlockAllowedMovementDirections = new HashSet<>(Set.of(EAST, WEST));
                if (containsNorthWall) {
                    bottomBlockSurroundingWallDirections.add(NORTH);
                } else {
                    bottomBlockAllowedMovementDirections.add(NORTH);
                }
                Block bottomBlock = new Block(numberOfBlockRows - 1, blockColumn, maxRow, maxRow + 2,
                        minColumnInclusive, maxColumnInclusive, bottomBlockSurroundingWallDirections, bottomBlockAllowedMovementDirections);
                blocks[numberOfBlockRows - 1][blockColumn] = bottomBlock;


                // Internal blocks
                for (int blockRow = 1; blockRow < numberOfBlockRows - 1; blockRow++) {

                    // min, max row and columns and all the walls depend on the input
                    int minRowInclusive = listOfRelevantRows.get(blockRow - 1);
                    int maxRowInclusive = listOfRelevantRows.get(blockRow);

                    containsNorthWall = isInAnyWall(walls, ImmutableCell2D.of(minRowInclusive, minColumnInclusive + 1));
                    containsSouthWall = isInAnyWall(walls, ImmutableCell2D.of(maxRowInclusive, minColumnInclusive + 1));
                    boolean containsEastWall = isInAnyWall(walls, ImmutableCell2D.of(minRowInclusive + 1, maxColumnInclusive));
                    boolean containsWestWall = isInAnyWall(walls, ImmutableCell2D.of(minRowInclusive + 1, minColumnInclusive));

                    Set<CardinalDirection2D> surroundingWallDirections = new HashSet<>();
                    Set<CardinalDirection2D> allowedMovementDirections = new HashSet<>();

                    if (containsNorthWall) {
                        surroundingWallDirections.add(NORTH);
                    } else {
                        allowedMovementDirections.add(NORTH);
                    }

                    if (containsSouthWall) {
                        surroundingWallDirections.add(SOUTH);
                    } else {
                        allowedMovementDirections.add(SOUTH);
                    }

                    if (containsEastWall) {
                        surroundingWallDirections.add(EAST);
                    } else {
                        allowedMovementDirections.add(EAST);
                    }

                    if (containsWestWall) {
                        surroundingWallDirections.add(WEST);
                    } else {
                        allowedMovementDirections.add(WEST);
                    }

                    Block internalBlock = new Block(blockRow, blockColumn, minRowInclusive, maxRowInclusive, minColumnInclusive,
                            maxColumnInclusive, surroundingWallDirections, allowedMovementDirections);
                    blocks[blockRow][blockColumn] = internalBlock;

                }

            }

        }

        int startBlockRow = listOfRelevantRows.indexOf(0);
        int startBlockColumn = listOfRelevantColumns.indexOf(0);

        List<ImmutableCell2D> possibleStartBlockLocations = new ArrayList<>();
        possibleStartBlockLocations.add(ImmutableCell2D.of(startBlockRow, startBlockColumn));
        possibleStartBlockLocations.add(ImmutableCell2D.of(startBlockRow + 1, startBlockColumn));
        possibleStartBlockLocations.add(ImmutableCell2D.of(startBlockRow, startBlockColumn + 1));
        possibleStartBlockLocations.add(ImmutableCell2D.of(startBlockRow + 1, startBlockColumn + 1));

        int endBlockRow = listOfRelevantRows.indexOf(end.getRow());
        int endBlockColumn = listOfRelevantColumns.indexOf(end.getColumn());

        List<ImmutableCell2D> possibleEndBlockLocations = new ArrayList<>();
        possibleEndBlockLocations.add(ImmutableCell2D.of(endBlockRow, endBlockColumn));
        possibleEndBlockLocations.add(ImmutableCell2D.of(endBlockRow + 1, endBlockColumn));
        possibleEndBlockLocations.add(ImmutableCell2D.of(endBlockRow, endBlockColumn + 1));
        possibleEndBlockLocations.add(ImmutableCell2D.of(endBlockRow + 1, endBlockColumn + 1));

        Function<ImmutableCell2D, Set<ImmutableCell2D>> blockNeighborExtractor = currentBlockLocation -> {

            Block currentBlock = blocks[currentBlockLocation.getRow()][currentBlockLocation.getColumn()];

            return Arrays.stream(values())
                    .filter(direction -> currentBlock.allowedMovementDirections().contains(direction))
                    .map(currentBlockLocation::add)
                    .collect(Collectors.toSet());
        };

        Set<List<Block>> distinctBlockPaths = createAllMinimalBlockPaths(blocks, blockNeighborExtractor,
                possibleStartBlockLocations, possibleEndBlockLocations, start, end);

        long minStepCount = Long.MAX_VALUE;

        for (List<Block> blockPath: distinctBlockPaths) {

            int pathLength = blockPath.size();

            CornerDescriptor startCornerDescriptor = blockPath.getFirst().containsPointAsCorner(start).orElseThrow();
            CornerDescriptor endCornerDescriptor = blockPath.getLast().containsPointAsCorner(end).orElseThrow();

            CardinalDirection2D currentCornerVerticalLocation = startCornerDescriptor.verticalLocation();
            CardinalDirection2D currentCornerHorizontalLocation = startCornerDescriptor.horizontalLocation();

            long stepCount = 2;

            for (int pathIndex = 0; pathIndex < pathLength - 1; pathIndex++) {

                Block sourceBlock = blockPath.get(pathIndex);
                Block targetBlock = blockPath.get(pathIndex + 1);

                CardinalDirection2D movementFromSourceToTarget;
                if (sourceBlock.blockRow() == targetBlock.blockRow()) {
                    // Same row. Horizontal movement
                    movementFromSourceToTarget = sourceBlock.blockColumn() > targetBlock.blockColumn() ? WEST : EAST;
                } else if (sourceBlock.blockColumn() == targetBlock.blockColumn()) {
                    // Same column. Vertical movement
                    movementFromSourceToTarget = sourceBlock.blockRow() > targetBlock.blockRow() ? NORTH : SOUTH;
                } else {
                    throw new IllegalStateException();
                }

                if (movementFromSourceToTarget.isHorizontal()) {

                    if (movementFromSourceToTarget.equals(currentCornerHorizontalLocation)) {
                        currentCornerHorizontalLocation = currentCornerHorizontalLocation.inverse();
                        int stepIncrease = 2;
                        stepCount += stepIncrease;
                    } else {
                        int stepIncrease = sourceBlock.maxColumnInclusive() - sourceBlock.minColumnInclusive();
                        stepCount += stepIncrease;
                    }

                } else if (movementFromSourceToTarget.isVertical()) {

                    if (movementFromSourceToTarget.equals(currentCornerVerticalLocation)) {
                        currentCornerVerticalLocation = currentCornerVerticalLocation.inverse();
                        int stepIncrease = 2;
                        stepCount += stepIncrease;
                    } else {
                        int stepIncrease = sourceBlock.maxRowInclusive() - sourceBlock.minRowInclusive();
                        stepCount += stepIncrease;
                    }

                } else {
                    throw new IllegalStateException();
                }

            }

            // Crossing the last block

            if (!currentCornerHorizontalLocation.equals(endCornerDescriptor.horizontalLocation())) {
                Block lastBlock = blockPath.getLast();
                long stepIncrease = lastBlock.maxColumnInclusive() - lastBlock.minColumnInclusive() - 2;
                stepCount += stepIncrease;
            }

            if (!currentCornerVerticalLocation.equals(endCornerDescriptor.verticalLocation())) {
                Block lastBlock = blockPath.getLast();
                long stepIncrease = lastBlock.maxRowInclusive() - lastBlock.minRowInclusive() - 2;
                stepCount += stepIncrease;
            }

            stepCount += 2; // From the corner of the last block to the actual end location

            minStepCount = Long.min(minStepCount, stepCount);

        }

        return Long.toString(minStepCount);
    }

    private interface Wall {
        boolean contains(Cell2D location);
    }

    private record HorizontalWall(int row, int columnStartInclusive, int columnEndInclusive) implements Wall {

        @Override
        public boolean contains(Cell2D location) {
            return row == location.getRow()
                   && columnStartInclusive <= location.getColumn()
                   && location.getColumn() <= columnEndInclusive;
        }
    }

    private record VerticalWall(int column, int rowStartInclusive, int rowEndInclusive) implements Wall {

        @Override
        public boolean contains(Cell2D location) {
            return column == location.getColumn()
                   && rowStartInclusive <= location.getRow()
                   && location.getRow() <= rowEndInclusive;
        }
    }

    private record CornerDescriptor(CardinalDirection2D verticalLocation, CardinalDirection2D horizontalLocation) {

        static final CornerDescriptor TOP_LEFT = new CornerDescriptor(NORTH, WEST);
        static final CornerDescriptor TOP_RIGHT = new CornerDescriptor(NORTH, EAST);
        static final CornerDescriptor BOTTOM_LEFT = new CornerDescriptor(SOUTH, WEST);
        static final CornerDescriptor BOTTOM_RIGHT = new CornerDescriptor(SOUTH, EAST);

    }

    private record Block(int blockRow, int blockColumn,
                         int minRowInclusive, int maxRowInclusive, int minColumnInclusive, int maxColumnInclusive,
                         Set<CardinalDirection2D> surroundingWallDirections, Set<CardinalDirection2D> allowedMovementDirections) {

        Optional<CornerDescriptor> containsPointAsCorner(Cell2D cell) {

            int row = cell.getRow();
            int column = cell.getColumn();

            if (minRowInclusive == row && minColumnInclusive == column) {
                return Optional.of(CornerDescriptor.TOP_LEFT);
            }

            if (minRowInclusive == row && maxColumnInclusive == column) {
                return Optional.of(CornerDescriptor.TOP_RIGHT);
            }

            if (maxRowInclusive == row && minColumnInclusive == column) {
                return Optional.of(CornerDescriptor.BOTTOM_LEFT);
            }

            if (maxRowInclusive == row && maxColumnInclusive == column) {
                return Optional.of(CornerDescriptor.BOTTOM_RIGHT);
            }

            return Optional.empty();

        }

    }

    private int maxIndexThatContainsLocationAsCorner(Block[][] blocks, int blockPathLength, List<ImmutableCell2D> blockPath, Cell2D location) {

        int maxIndex = -1;

        for (int blockIndex = 0; blockIndex < blockPathLength; blockIndex++) {
            ImmutableCell2D blockLocation = blockPath.get(blockIndex);
            Block block = blocks[blockLocation.getRow()][blockLocation.getColumn()];
            Optional<CornerDescriptor> cornerDescriptor = block.containsPointAsCorner(location);
            if (cornerDescriptor.isPresent()) {
                maxIndex = Integer.max(maxIndex, blockIndex);
            }
        }

        return maxIndex;

    }

    private int minIndexThatContainsLocationAsCorner(Block[][] blocks, int blockPathLength, List<ImmutableCell2D> blockPath, Cell2D location) {

        int minIndex = blockPathLength;

        for (int blockIndex = blockPathLength - 1; blockIndex >= 0; blockIndex--) {
            ImmutableCell2D blockLocation = blockPath.get(blockIndex);
            Block block = blocks[blockLocation.getRow()][blockLocation.getColumn()];
            Optional<CornerDescriptor> cornerDescriptor = block.containsPointAsCorner(location);
            if (cornerDescriptor.isPresent()) {
                minIndex = Integer.min(minIndex, blockIndex);
            }
        }

        return minIndex;

    }

    private boolean isInAnyWall(List<Wall> walls, Cell2D location) {
        for (Wall wall : walls) {
            if (wall.contains(location)) {
                return true;
            }
        }
        return false;
    }

    private Set<List<Block>> createAllMinimalBlockPaths(Block[][] blocks, Function<ImmutableCell2D, Set<ImmutableCell2D>> blockNeighborExtractor,
                                                        List<ImmutableCell2D> possibleStartBlockLocations, List<ImmutableCell2D> possibleEndBlockLocations,
                                                        Cell2D start, Cell2D end) {

        Set<List<Block>> distinctBlockPaths = new HashSet<>();

        for (ImmutableCell2D startBlockLocation: possibleStartBlockLocations) {

            for (ImmutableCell2D endBlockLocation: possibleEndBlockLocations) {

                List<ImmutableCell2D> blockPathLocations = PathFindingAlgorithms.minPathSimpleCost(blockNeighborExtractor, startBlockLocation, endBlockLocation);

                int maxIndexOfStartBlockInThePath = maxIndexThatContainsLocationAsCorner(blocks, blockPathLocations.size(), blockPathLocations, start);

                int minIndexOfEndBlockInThePath = minIndexThatContainsLocationAsCorner(blocks, blockPathLocations.size(), blockPathLocations, end);

                List<Block> blockPath = blockPathLocations.subList(maxIndexOfStartBlockInThePath, minIndexOfEndBlockInThePath + 1)
                        .stream()
                        .map(blockPathLocation -> blocks[blockPathLocation.getRow()][blockPathLocation.getColumn()])
                        .toList();

                distinctBlockPaths.add(blockPath);

            }

        }

        return distinctBlockPaths;

    }

}
