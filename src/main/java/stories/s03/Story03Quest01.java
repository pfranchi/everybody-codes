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

        Map<ColorScale, Integer> colorScales = parseInput(inputLines);

        int total = colorScales
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().isGreenDominant())
                .mapToInt(Map.Entry::getValue)
                .sum();

        return Integer.toString(total);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        NavigableMap<ColorScale, Integer> colorScalesToIdentifiers = parseInput(inputLines);
        return String.valueOf(colorScalesToIdentifiers.firstEntry().getValue());
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Map<ColorScale, Integer> colorScales =  parseInput(inputLines);

        Multimap<Group, Integer> identifiersByGroup = ArrayListMultimap.create();

        colorScales.forEach((colorScale, identifier) -> {
            Optional<Group> group = colorScale.getGroup();
            group.ifPresent(value -> identifiersByGroup.put(value, identifier));
        });

        int maxOccurrences = Integer.MIN_VALUE;
        int sumOfIdentifiersOfCurrentBest = 0;

        for (Map.Entry<Group, Collection<Integer>> entry: identifiersByGroup.asMap().entrySet()) {

            int occurrences = entry.getValue().size();

            if (occurrences > maxOccurrences) {
                // Found new best
                maxOccurrences = occurrences;
                sumOfIdentifiersOfCurrentBest = entry.getValue().stream().mapToInt(i -> i).sum();
            }

        }

        return Integer.toString(sumOfIdentifiersOfCurrentBest);
    }

    private NavigableMap<ColorScale, Integer> parseInput(List<String> inputLines) {

        NavigableMap<ColorScale, Integer> colorScalesToIdentifiers = new TreeMap<>();

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

            ColorScale colorScale = new ColorScale(valuesByColor.get('r'), valuesByColor.get('g'),
                    valuesByColor.get('b'), valuesByColor.getOrDefault('s', 0));

            colorScalesToIdentifiers.put(colorScale, identifier);

        }

        return colorScalesToIdentifiers;

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
            currentPowerOf2 >>= 1;
        }

        return total;

    }

    private record ColorScale(int red, int green, int blue, int shine) implements Comparable<ColorScale> {

        @Override
        public int compareTo(@Nonnull ColorScale o) {
            return Comparator
                    .comparingInt(ColorScale::shine).reversed()
                    .thenComparing(ColorScale::getDarkness)
                    .thenComparingInt(ColorScale::red)
                    .thenComparingInt(ColorScale::green)
                    .thenComparingInt(ColorScale::blue)
                    .compare(this, o);
        }

        public boolean isRedDominant() {
            return red > green && red > blue;
        }

        public boolean isGreenDominant() {
            return green > red && green > blue;
        }

        public boolean isBlueDominant() {
            return blue > red && blue > green;
        }

        public int getDarkness() {
            return red + green + blue;
        }

        public Optional<Group> getGroup() {

            if (shine == 31 || shine == 32) {
                return Optional.empty();
            }

            if (!isRedDominant() && !isGreenDominant() && !isBlueDominant()) {
                return Optional.empty();
            }

            char dominantColor;

            if (isRedDominant()) {
                dominantColor = 'r';
            } else if (isGreenDominant()) {
                dominantColor = 'g';
            } else {
                dominantColor = 'b';
            }

            boolean isShiny = shine >= 33;

            return Optional.of(new Group(dominantColor, isShiny));
        }

    }

    private record Group(char dominantColor, boolean isShiny) {}

}
