package events.y2024;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import common.stats.AbstractQuest;
import common.support.interfaces.MainEvent2024;
import common.support.interfaces.Quest05;
import common.support.params.ExecutionParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EC2024Quest05 extends AbstractQuest implements MainEvent2024, Quest05 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        List<List<Integer>> columns = createColumns(inputLines);
        printColumns(columns);

        for (int round = 0; round < 10; round++) {

            emptyLine();
            log("Start round {}", round + 1);
            int sourceColumnIndex = round % 4;
            int targetColumnIndex = (sourceColumnIndex + 1) % 4;

            log("Source column index is {}, target column index is {}", sourceColumnIndex, targetColumnIndex);

            List<Integer> sourceColumn = columns.get(sourceColumnIndex);
            List<Integer> targetColumn = columns.get(targetColumnIndex);

            int clapper = sourceColumn.removeFirst();

            log("Clapper is {}", clapper);
            printColumns(columns);

            int indexOfInsertion = getIndexOfInsertion(targetColumn.size(), clapper);

            log("Adding clapper to target column");
            targetColumn.add(indexOfInsertion, clapper);
            printColumns(columns);

        }

        return columns.stream().map(column -> Integer.toString(column.getFirst())).collect(Collectors.joining());
    }

    private static List<List<Integer>> createColumns(List<String> inputLines) {
        List<List<Integer>> gridOfNumbers = new ArrayList<>();
        for (String line: inputLines) {
            List<Integer> lineOfNumbers = Arrays.stream(line.split(" ")).mapToInt(Integer::parseInt).boxed().toList();
            gridOfNumbers.add(lineOfNumbers);
        }

        List<List<Integer>> columns = new ArrayList<>(); // list of size 4
        for (int columnIndex = 0; columnIndex < 4; columnIndex++) {
            columns.add(new ArrayList<>());
        }

        for (List<Integer> lineOfNumbers: gridOfNumbers) {
            for (int columnIndex = 0; columnIndex < 4; columnIndex++) {
                columns.get(columnIndex).add(lineOfNumbers.get(columnIndex));
            }
        }
        return columns;
    }

    private void printColumns(List<List<Integer>> columns) {
        log("Columns are:");
        columns.forEach(this::log);
    }

    private int getIndexOfInsertion(int targetColumnLength, int clapper) {
        if (clapper > 5) {
            throw new UnsupportedOperationException("Not implemented for clapper " + clapper);
        }
        return switch (targetColumnLength) {
            case 3 -> (clapper == 5) ? 2 : clapper - 1;
            case 4, 5 -> clapper - 1;
            default ->
                    throw new UnsupportedOperationException("Not implemented for targetColumnLength " + targetColumnLength + " and clapper " + clapper);
        };
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        List<List<Integer>> columns = createColumns(inputLines);
        printColumns(columns);

        Multiset<Integer> shoutedNumbers = HashMultiset.create();
        long round = 0;

        while (true) {

            int sourceColumnIndex = (int) (round % 4);
            int targetColumnIndex = (sourceColumnIndex + 1) % 4;
            List<Integer> sourceColumn = columns.get(sourceColumnIndex);
            List<Integer> targetColumn = columns.get(targetColumnIndex);
            int clapper = sourceColumn.removeFirst();
            int targetColumnSize = targetColumn.size();

            int indexOfInsertion = getIndexOfInsertionPart2(targetColumnSize, clapper);
            targetColumn.add(indexOfInsertion, clapper);
            round++;

            Integer shouted = Integer.parseInt(columns.stream().map(column -> Integer.toString(column.getFirst())).collect(Collectors.joining()));
            shoutedNumbers.add(shouted);
            if (shoutedNumbers.count(shouted) == 2024) {
                break;
            }

        }

        int shouted = Integer.parseInt(columns.stream().map(column -> Integer.toString(column.getFirst())).collect(Collectors.joining()));
        log("After round {}, number {} is shouted for the 2024th time", round, shouted);

        long result = round * shouted;

        return Long.toString(result);
    }

    private int getIndexOfInsertionPart2(int targetColumnLength, int clapper) {

        clapper = ((clapper - 1) % (2 * targetColumnLength)) + 1; // now it is in range 1..(2 * targetColumnLength) inclusive

        if (clapper <= targetColumnLength) {
            return clapper - 1;
        }

        return 2 * targetColumnLength - clapper + 1;

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        List<List<Integer>> columns = createColumns(inputLines);
        printColumns(columns);

        long round = 0;
        long max = Long.MIN_VALUE;

        while (round < 100000) {

            int sourceColumnIndex = (int) (round % 4);
            int targetColumnIndex = (sourceColumnIndex + 1) % 4;
            List<Integer> sourceColumn = columns.get(sourceColumnIndex);
            List<Integer> targetColumn = columns.get(targetColumnIndex);
            int clapper = sourceColumn.removeFirst();
            int targetColumnSize = targetColumn.size();

            int indexOfInsertion = getIndexOfInsertionPart2(targetColumnSize, clapper);
            targetColumn.add(indexOfInsertion, clapper);
            round++;

            long shouted = Long.parseLong(columns.stream().map(column -> Integer.toString(column.getFirst())).collect(Collectors.joining()));
            max = Long.max(max, shouted);

            if (round % 1000 == 0) {
                log("Round {}, max seen is {}", round, max);
            }

        }

        return Long.toString(max);

    }
}
