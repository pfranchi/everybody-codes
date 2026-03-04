package stories.s03;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import common.AbstractQuest;
import common.support.interfaces.Quest01;
import common.support.interfaces.Story03;
import common.support.params.ExecutionParameters;

import javax.annotation.Nonnull;
import java.util.*;

public class Story03Quest01 extends AbstractQuest implements Story03, Quest01 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int total = 0;

        for (String line: inputLines) {

            String[] parts = line.split(":");
            int identifier = Integer.parseInt(parts[0]);

            String[] colorComponents = parts[1].split(" ");

            Map<Character, Integer> valuesByColor =  new HashMap<>();

            for (String colorComponent:  colorComponents) {

                char color = Character.toLowerCase(colorComponent.charAt(0));
                int value = decode(colorComponent);

                valuesByColor.put(color, value);

            }

            int greenValue = valuesByColor.get('g');

            if (greenValue > valuesByColor.get('r') && greenValue > valuesByColor.get('b')) {
                total += identifier;
            }

        }

        return Integer.toString(total);
    }

    private int decode(String colorComponent) {
        char[] chars = colorComponent.toCharArray();
        int maxExponent = chars.length - 1;
        int currentPowerOf2 = 1 << maxExponent;

        int total = 0;

        for (char c: chars) {

            if (Character.isUpperCase(c)) {
                total += currentPowerOf2;
            }

            currentPowerOf2 /= 2;

        }

        return total;

    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Comparator<ColorScale> cmp = Comparator
                .comparingInt(ColorScale::shine).reversed()
                .thenComparing(ColorScale::getDarkness);

        NavigableMap<ColorScale, Integer> colorScalesToIdentifiers = new TreeMap<>(cmp);

        for (String line:  inputLines) {

            String[] parts = line.split(":");
            int identifier = Integer.parseInt(parts[0]);

            String[] colorComponents = parts[1].split(" ");

            Map<Character, Integer> valuesByColor =  new HashMap<>();

            for (String colorComponent:  colorComponents) {

                char color = Character.toLowerCase(colorComponent.charAt(0));
                int value = decode(colorComponent);

                valuesByColor.put(color, value);

            }

            ColorScale colorScale = new ColorScale(valuesByColor.get('r'), valuesByColor.get('g'), valuesByColor.get('b'), valuesByColor.get('s'));

            colorScalesToIdentifiers.put(colorScale, identifier);

        }

        return String.valueOf(colorScalesToIdentifiers.firstEntry().getValue());
    }

    private record ColorScale(int red, int green, int blue, int shine) {

        public int getDarkness() {
            return red + green + blue;
        }

        public Optional<Group> getGroup() {

            if (shine == 31 || shine == 32) {
                return Optional.empty();
            }

            int maxValue = Integer.max(red, Integer.max(green, blue));
            int countOfValuesEqualToMax = 0;
            if (red == maxValue) {
                countOfValuesEqualToMax++;
            }
            if (green == maxValue) {
                countOfValuesEqualToMax++;
            }
            if (blue == maxValue) {
                countOfValuesEqualToMax++;
            }

            if (countOfValuesEqualToMax > 1) {
                return Optional.empty();
            }

            char dominantColor = 'g';

            if (blue > red && blue > green) {
                dominantColor = 'b';
            }

            if (red > blue && red > green) {
                dominantColor = 'r';
            }

            boolean isShiny = shine >= 33;

            return Optional.of(new Group(dominantColor, isShiny));
        }

    }

    private record Group(char dominantColor, boolean isShiny) {}

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        //inputLines.forEach(this::log);

        Multimap<Group, Long> identifiersByGroup = ArrayListMultimap.create();

        for (String line:  inputLines) {

            String[] parts = line.split(":");
            long identifier = Long.parseLong(parts[0]);

            String[] colorComponents = parts[1].split(" ");

            Map<Character, Integer> valuesByColor =  new HashMap<>();

            for (String colorComponent:  colorComponents) {

                char color = Character.toLowerCase(colorComponent.charAt(0));
                int value = decode(colorComponent);

                valuesByColor.put(color, value);

            }

            ColorScale colorScale = new ColorScale(valuesByColor.get('r'), valuesByColor.get('g'), valuesByColor.get('b'), valuesByColor.get('s'));
            Optional<Group> group = colorScale.getGroup();


            /*
            log("Line {}. Color scale = {}. Group = {}", line,  colorScale,
                    group.map(Objects::toString).orElse("Unassigned")
            );



             */

            //colorScalesToIdentifiers.put(colorScale, identifier);

            group.ifPresent(value -> identifiersByGroup.put(value, identifier));

        }

        /*
        emptyLine();
        log("Found the following groups:");
        identifiersByGroup.asMap().entrySet().forEach(this::log);

         */

        int maxOccurrences = Integer.MIN_VALUE;
        long sumOfIdentifiersOfCurrentBest = 0;

        for (Map.Entry<Group, Collection<Long>> entry: identifiersByGroup.asMap().entrySet()) {

            //log("Group {} with {} occurrences", entry.getKey(), entry.getValue().size());

            int occurrences = entry.getValue().size();

            if (occurrences > maxOccurrences) {

                //log("Found new max for group {} with {} occurrences", entry.getKey(),  occurrences);

                // Found new best
                maxOccurrences = occurrences;
                sumOfIdentifiersOfCurrentBest = entry.getValue().stream().mapToLong(i -> i).sum();
            }

        }

        //log("Sum of identifiers = {}", sumOfIdentifiersOfCurrentBest);

        return Long.toString(sumOfIdentifiersOfCurrentBest);
    }

}
