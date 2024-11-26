package challenges.ec2024;

import challenges.Quest;
import challenges.interfaces.ECEvent2024;
import challenges.interfaces.Quest10;
import challenges.params.ExecutionParameters;
import com.google.common.collect.*;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class EC2024Quest10 extends Quest implements ECEvent2024, Quest10 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return extractRunicWord(inputLines, 0, 0);
    }

    private String extractRunicWord(List<String> inputLines, int rowIndexOfTopLeftCorner, int columnIndexOfTopLeftCorner) {

        SetMultimap<Integer, Character> rowMap = MultimapBuilder.treeKeys().hashSetValues().build();
        SetMultimap<Integer, Character> columnMap = MultimapBuilder.treeKeys().hashSetValues().build();

        // Rows
        for (int rowIndex = 0; rowIndex < 4; rowIndex++) {
            String line = inputLines.get(rowIndexOfTopLeftCorner + rowIndex + 2);
            rowMap.put(rowIndex, line.charAt(columnIndexOfTopLeftCorner));
            rowMap.put(rowIndex, line.charAt(columnIndexOfTopLeftCorner + 1));
            rowMap.put(rowIndex, line.charAt(columnIndexOfTopLeftCorner + 6));
            rowMap.put(rowIndex, line.charAt(columnIndexOfTopLeftCorner + 7));
        }

        // Columns
        for (int columnIndex = 0; columnIndex < 4; columnIndex++) {
            columnMap.put(columnIndex, inputLines.get(rowIndexOfTopLeftCorner).charAt(columnIndexOfTopLeftCorner + columnIndex + 2));
            columnMap.put(columnIndex, inputLines.get(rowIndexOfTopLeftCorner + 1).charAt(columnIndexOfTopLeftCorner + columnIndex + 2));
            columnMap.put(columnIndex, inputLines.get(rowIndexOfTopLeftCorner + 6).charAt(columnIndexOfTopLeftCorner + columnIndex + 2));
            columnMap.put(columnIndex, inputLines.get(rowIndexOfTopLeftCorner + 7).charAt(columnIndexOfTopLeftCorner + columnIndex + 2));
        }

        StringBuilder sb = new StringBuilder();
        for (int rowIndex = 0; rowIndex < 4; rowIndex++) {
            Set<Character> charactersInRow = rowMap.get(rowIndex);
            for (int columnIndex = 0; columnIndex < 4; columnIndex++) {
                Set<Character> charactersInColumn = columnMap.get(columnIndex);

                Set<Character> intersection = Sets.intersection(charactersInRow, charactersInColumn);
                if (intersection.size() != 1) {
                    throw new IllegalStateException("Row " + rowIndex + ", column " + columnIndex);
                }
                Character commonChar = intersection.iterator().next();

                sb.append(commonChar);

            }
        }

        return sb.toString();

    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfInputRows = inputLines.size();
        int numberOfInputColumns = inputLines.getFirst().length();

        log("Input has {} rows and {} columns", numberOfInputRows, numberOfInputColumns);

        int totalPower = 0;

        for (int rowIndexOfTopLeftCorner = 0; rowIndexOfTopLeftCorner < numberOfInputRows; rowIndexOfTopLeftCorner += 9) {
            for (int columnIndexOfTopLeftCorner = 0; columnIndexOfTopLeftCorner < numberOfInputColumns; columnIndexOfTopLeftCorner += 9) {

                String runicWord = extractRunicWord(inputLines, rowIndexOfTopLeftCorner, columnIndexOfTopLeftCorner);

                int power = IntStream.range(0, 16).map(letterIndex -> (letterIndex + 1) * (runicWord.charAt(letterIndex) - 64)).sum();

                totalPower += power;

            }
        }

        return Integer.toString(totalPower);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return NOT_IMPLEMENTED;
    }
}
