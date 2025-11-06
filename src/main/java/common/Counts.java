package common;

import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import common.count.CharCount;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class Counts {

    private Counts() {
    }

    public enum CountSort {
        FIRST_APPEARANCE,
        HIGHEST_COUNT,
        LOWEST_COUNT;

        private Comparator<CharCount> getCharCountCmp() {
            return switch (this) {
                case FIRST_APPEARANCE -> (a, b) -> 0; // no swap
                case HIGHEST_COUNT -> Comparator.comparingInt(CharCount::count).reversed();
                case LOWEST_COUNT -> Comparator.comparingInt(CharCount::count);
            };
        }

    }

    public static List<CharCount> countChars(String s) {
        return countChars(s.toCharArray());
    }

    public static List<CharCount> countChars(char[] chars) {
        return countChars(chars, CountSort.FIRST_APPEARANCE);
    }

    public static List<CharCount> countChars(char[] chars, CountSort sort) {
        Multiset<Character> counts = LinkedHashMultiset.create();

        for (char c : chars) {
            counts.add(c);
        }

        return counts.entrySet()
                .stream()
                .map(entry -> new CharCount(entry.getElement(), entry.getCount()))
                .sorted(sort.getCharCountCmp())
                .toList();
    }

    public static <T> Collector<T, ?, Map<T, Long>> byFrequencyCount() {
        return Collectors.groupingBy(Function.identity(), Collectors.counting());
    }

}
