package challenges.ec2024;

import challenges.AbstractQuest;
import challenges.interfaces.ECEvent2024;
import challenges.interfaces.Quest02;
import challenges.params.ExecutionParameters;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EC2024Quest02 extends AbstractQuest implements ECEvent2024, Quest02 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        String[] words = inputLines.getFirst().substring(6).split(","); // removed "WORDS:"
        String text = inputLines.get(2);

        long total = 0;

        for (String word: words) {
            Matcher matcher = Pattern.compile(word).matcher(text);
            total += matcher.results().count();
        }

        return Long.toString(total);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfInputLines = inputLines.size();
        int numberOfTextLines = numberOfInputLines - 2;

        String[] words = inputLines.getFirst().substring(6).split(","); // removed "WORDS:"

        List<String> textLines = inputLines.subList(2, numberOfInputLines);

        boolean[][] flags = new boolean[numberOfTextLines][];

        for (int textLineIndex = 0; textLineIndex < numberOfTextLines; textLineIndex++) {
            flags[textLineIndex] = new boolean[textLines.get(textLineIndex).length()];
        }

        for (String word: words) {

            Pattern forwardPattern = Pattern.compile(word);
            Pattern backwardPattern = Pattern.compile(StringUtils.reverse(word));

            for (int textLineIndex = 0; textLineIndex < numberOfTextLines; textLineIndex++) {

                String textLine = textLines.get(textLineIndex);
                setFlags(textLine.length(), textLineIndex, forwardPattern, textLine, flags, false);
                setFlags(textLine.length(), textLineIndex, backwardPattern, textLine, flags, false);

            }

        }

        int total = 0;
        for (int rowIndex = 0; rowIndex < numberOfTextLines; rowIndex++) {
            boolean[] row = flags[rowIndex];
            for (boolean cell: row) {
                if (cell) {
                    total++;
                }
            }
        }

        for (int textLineIndex = 0; textLineIndex < numberOfTextLines; textLineIndex++) {

            String line = textLines.get(textLineIndex);
            log(line);

            StringBuilder sb = new StringBuilder();
            for (int idx = 0; idx < line.length(); idx++) {

                if (flags[textLineIndex][idx]) {
                    sb.append('*');
                } else {
                    sb.append(line.charAt(idx));
                }

            }

            log(sb);

        }

        return Integer.toString(total);
    }

    private static void setFlags(int numberOfColumnsInThisRow, int textLineIndex,
                                 Pattern pattern, String input, boolean[][] flags, boolean transpose) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find() && matcher.start() < numberOfColumnsInThisRow) {

            int start = matcher.start();
            int end = matcher.end();

            for (int idx = start; idx < end; idx++) {
                if (transpose) {
                    flags[idx][textLineIndex] = true;
                } else {
                    flags[textLineIndex][idx % numberOfColumnsInThisRow] = true;
                }
            }

            while (matcher.find(matcher.start() + 1) && matcher.start() < numberOfColumnsInThisRow) {
                start = matcher.start();
                end = matcher.end();

                for (int idx = start; idx < end; idx++) {
                    if (transpose) {
                        flags[idx][textLineIndex] = true;
                    } else {
                        flags[textLineIndex][idx % numberOfColumnsInThisRow] = true;
                    }
                }

            }

        }
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        //log(input);

        int numberOfInputLines = inputLines.size();
        int numberOfTextLines = numberOfInputLines - 2;

        String[] words = inputLines.getFirst().substring(6).split(","); // removed "WORDS:"

        List<String> originalTextLines = inputLines.subList(2, numberOfInputLines);
        int numberOfTextColumns = originalTextLines.getFirst().length();

        List<String> horizontalTextLines = originalTextLines.stream().map(s -> s + s).toList();

        List<String> verticalTextLines = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < numberOfTextColumns; columnIndex++) {
            StringBuilder sb = new StringBuilder();
            for (int rowIndex = 0; rowIndex < numberOfTextLines; rowIndex++) {
                sb.append(originalTextLines.get(rowIndex).charAt(columnIndex));
            }
            verticalTextLines.add(sb.toString());
        }

        /*
        log("Horizontal");
        log(horizontalTextLines);
        log("Vertical");
        log(verticalTextLines);

         */

        boolean[][] flags = new boolean[numberOfTextLines][numberOfTextColumns];

        for (String word: words) {
            Pattern forwardPattern = Pattern.compile(word);
            Pattern backwardPattern = Pattern.compile(StringUtils.reverse(word));

            for (int textLineIndex = 0; textLineIndex < numberOfTextLines; textLineIndex++) {

                String textLine = horizontalTextLines.get(textLineIndex);
                setFlags(numberOfTextColumns, textLineIndex, forwardPattern, textLine, flags, false);
                setFlags(numberOfTextColumns, textLineIndex, backwardPattern, textLine, flags, false);

            }


            for (int columnIndex = 0; columnIndex < numberOfTextColumns; columnIndex++) {

                String textLine = verticalTextLines.get(columnIndex);

                setFlags(numberOfTextLines, columnIndex, forwardPattern, textLine, flags, true);
                setFlags(numberOfTextLines, columnIndex, backwardPattern, textLine, flags, true);

            }

        }



        int total = 0;
        for (int rowIndex = 0; rowIndex < numberOfTextLines; rowIndex++) {
            boolean[] row = flags[rowIndex];
            for (boolean cell: row) {
                if (cell) {
                    total++;
                }
            }
        }

        for (int textLineIndex = 0; textLineIndex < numberOfTextLines; textLineIndex++) {

            String line = originalTextLines.get(textLineIndex);
            //log(line);

            StringBuilder sb = new StringBuilder();
            for (int idx = 0; idx < line.length(); idx++) {

                if (flags[textLineIndex][idx]) {
                    sb.append('*');
                } else {
                    sb.append(line.charAt(idx));
                }

            }

            //log(sb);

        }


        return Integer.toString(total);
    }
}
