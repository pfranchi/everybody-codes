package common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class Sections {

    private Sections() {}

    public static List<List<String>> splitIntoSections(String input, Predicate<? super String> splitPredicate) {

        List<List<String>> sections = new ArrayList<>();

        List<String> currentSection = new ArrayList<>();
        for (String line: Strings.splitByRow(input)) {

            if (splitPredicate.test(line)) {
                sections.add(currentSection);
                currentSection = new ArrayList<>();
            } else {
                currentSection.add(line);
            }

        }
        sections.add(currentSection);

        return sections;

    }

    public static List<List<String>> splitAtBlankLines(String input) {
        return splitIntoSections(input, String::isBlank);
    }

}
