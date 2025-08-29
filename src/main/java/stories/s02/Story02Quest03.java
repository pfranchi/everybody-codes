package stories.s02;

import common.AbstractQuest;
import common.Grids;
import common.Sections;
import common.VisualSolutions;
import common.geo.CardinalDirection2D;
import common.geo.ImmutableCell2D;
import common.support.interfaces.Quest03;
import common.support.interfaces.Story02;
import common.support.params.ExecutionParameters;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Story02Quest03 extends AbstractQuest implements Story02, Quest03 {

    private static final Pattern PATTERN = Pattern.compile("^(?<id>\\d+): faces=\\[(?<vals>.*)] seed=(?<seed>\\d+)$");

    private static final class Die {
        private final int id;
        private final int seed;
        private final List<Integer> faces;
        private final int numberOfFaces;
        private long nextRollNumber = 1;
        private long pulse;
        private int currentFaceIndex = 0; // this is the index

        public Die(int id, int seed, List<Integer> faces) {
            this.id = id;
            this.seed = seed;
            this.faces = faces;
            this.numberOfFaces = faces.size();
            this.pulse = seed;
        }

        public int roll() {

            long spin = nextRollNumber * pulse;

            currentFaceIndex += (int) (spin % numberOfFaces);
            currentFaceIndex %= numberOfFaces;

            // update values
            pulse += spin;
            pulse %= seed;
            pulse = pulse + 1 + nextRollNumber + seed;
            nextRollNumber++;

            return faces.get(currentFaceIndex);

        }

    }

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        List<Die> dice = createDice(inputLines);

        int total = 0;

        int rollCount = 0;

        while (total < 10000) {

            rollCount++;

            for (Die die: dice) {
                total += die.roll();
            }

        }

        return Integer.toString(rollCount);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        List<List<String>> sections = Sections.splitAtBlankLines(input);

        List<String> section1 = sections.getFirst();
        String trackString = sections.get(1).getFirst();

        List<Integer> track = new ArrayList<>();
        for (char c: trackString.toCharArray()) {
            track.add(Integer.parseInt(Character.toString(c)));
        }

        List<Die> dice = createDice(section1);

        List<Result> results = new ArrayList<>();

        for (Die die: dice) {

            int rollCount = numberOfRollsToReachTheEnd(track, die);
            results.add(new Result(die.id, rollCount));

        }

        results.sort(Comparator.comparingInt(Result::rollCount));

        return results.stream().map(result -> Integer.toString(result.dieId)).collect(Collectors.joining(","));
    }

    private record Result(int dieId, int rollCount) {}

    private int numberOfRollsToReachTheEnd(List<Integer> track, Die die) {
        int rollCount = 0;
        for (int targetValue : track) {
            do {
                rollCount++;
            } while (die.roll() != targetValue);
        }
        return rollCount;
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        List<List<String>> sections = Sections.splitAtBlankLines(input);

        List<String> section1 = sections.getFirst();
        List<String> section2 = sections.get(1);

        List<Die> dice = createDice(section1);

        int numberOfRows = section2.size();
        int numberOfColumns = section2.getFirst().length();

        int[][] grid = new int[numberOfRows][numberOfColumns];

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            String row = section2.get(rowIndex);
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                grid[rowIndex][columnIndex] = Integer.parseInt(Character.toString(row.charAt(columnIndex)));
            }
        }

        Set<ImmutableCell2D> visited = new HashSet<>();

        for (Die die: dice) {

            int firstRoll = die.roll();

            Set<ImmutableCell2D> playerPositions = new HashSet<>();

            for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
                for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                    if (grid[rowIndex][columnIndex] == firstRoll) {
                        playerPositions.add(ImmutableCell2D.of(rowIndex, columnIndex));
                    }
                }
            }

            visited.addAll(playerPositions);

            int currentValue = firstRoll; // all players are currently on cells that have this value

            while (!playerPositions.isEmpty()) {

                int roll = die.roll();

                Set<ImmutableCell2D> newPlayerPositions = new HashSet<>();

                for (ImmutableCell2D oldPlayerPosition: playerPositions) {
                    for (CardinalDirection2D direction: CardinalDirection2D.values()) {
                        ImmutableCell2D newPlayerPosition = oldPlayerPosition.add(direction);

                        if (isInGrid(numberOfRows, numberOfColumns, newPlayerPosition) &&
                            grid[newPlayerPosition.getRow()][newPlayerPosition.getColumn()] == roll) {
                            // Can move to this new position
                            newPlayerPositions.add(newPlayerPosition);
                        }

                    }
                }

                visited.addAll(newPlayerPositions);

                if (roll == currentValue) {

                    // All players can stay
                    newPlayerPositions.addAll(playerPositions);

                }

                currentValue = roll;
                playerPositions = newPlayerPositions;

            }

        }

        return Integer.toString(visited.size());
    }

    private boolean isInGrid(int numberOfRows, int numberOfColumns, ImmutableCell2D cell) {
        return 0 <= cell.getRow() && cell.getRow() < numberOfRows && 0 <= cell.getColumn() && cell.getColumn() < numberOfColumns;
    }

    private List<Die> createDice(List<String> lines) {
        List<Die> dice = new ArrayList<>();

        for (String line: lines) {
            Matcher matcher = PATTERN.matcher(line);
            if (matcher.matches()) {
                int id = Integer.parseInt(matcher.group("id"));
                List<Integer> faces = Arrays.stream(matcher.group("vals").split(",")).map(Integer::parseInt).toList();
                int seed = Integer.parseInt(matcher.group("seed"));

                Die die = new Die(id, seed, faces);

                dice.add(die);

            }
        }
        return dice;
    }

}
