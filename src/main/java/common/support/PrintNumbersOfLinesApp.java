package common.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PrintNumbersOfLinesApp {

    private static final Pattern PATTERN = Pattern.compile("y(?<year>\\d{4})");

    public static void main(String[] args) {

        try (Stream<Path> folders = Files.list(Path.of("src/main/java/challenges"))) {

            List<Path> folderPathsList = folders.toList();

            for (Path folderPath: folderPathsList) {

                Matcher matcher = PATTERN.matcher(folderPath.getFileName().toString());
                if (matcher.matches()) {
                    int year = Integer.parseInt(matcher.group("ecEvent"));

                    System.out.println("YEAR " + year);
                    printYear(folderPath, year);
                    System.out.println();

                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void printYear(Path folderPath, int year) {

        int total = 0;

        try (Stream<Path> fileStream = Files.list(folderPath)) {
            List<Path> files = fileStream.sorted().toList();

            for (int day = 1; day <= 25; day++) {

                Path filePath = files.get(day - 1);

                try (Stream<String> lines = Files.lines(filePath)) {
                    int length = lines.toList().size();
                    System.out.println(year + "," + (day < 10 ? " " : "") + day + "," + length);

                    total += length;
                }

            }

            System.out.println("Total for ecEvent " + year + ": " + total);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
