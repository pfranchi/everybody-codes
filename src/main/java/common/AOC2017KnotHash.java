package common;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public final class AOC2017KnotHash {

    private AOC2017KnotHash() {}

    private static final List<Integer> LIST_OF_LENGTHS_TO_BE_APPENDED = List.of(17, 31, 73, 47, 23);

    public static String knotHash(String input) {
        List<Integer> sequenceOfLengths = new ArrayList<>();
        for (char c: input.toCharArray()) {
            sequenceOfLengths.add((int) c);
        }

        sequenceOfLengths.addAll(LIST_OF_LENGTHS_TO_BE_APPENDED);

        int size = 256;
        int[] numbers = IntStream.range(0, size).toArray();


        int currentIndex = 0;
        int currentSkipSize = 0;

        for (int round = 1; round <= 64; round++) {

            for (int length : sequenceOfLengths) {
                int[] newNumbers = new int[size];

                for (int increment = 0; increment < size; increment++) {

                    int actualIndex = currentIndex + increment;
                    if (increment < length) {
                        actualIndex = currentIndex + length - increment - 1;
                    }
                    actualIndex = actualIndex % size;

                    newNumbers[(currentIndex + increment) % size] = numbers[actualIndex];

                }

                currentIndex += length + currentSkipSize;
                currentSkipSize++;

                numbers = newNumbers;

            }

        }

        StringBuilder sb = new StringBuilder();

        for (int group = 0; group < 16; group++) {

            int start = numbers[16 * group];
            for (int increment = 1; increment < 16; increment++) {
                start ^= numbers[16 * group + increment];
            }

            sb.append(StringUtils.leftPad(Integer.toHexString(start), 2, '0'));

        }

        return sb.toString();
    }

}
