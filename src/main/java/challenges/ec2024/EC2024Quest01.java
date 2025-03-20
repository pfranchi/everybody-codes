package challenges.ec2024;

import challenges.AbstractQuest;
import challenges.interfaces.ECEvent2024;
import challenges.interfaces.Quest01;
import challenges.params.ExecutionParameters;

import java.util.List;

public class EC2024Quest01 extends AbstractQuest implements ECEvent2024, Quest01 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int total = 0;
        for (char c: input.toCharArray()) {
            total += switch (c) {
                case 'A' -> 0;
                case 'B' -> 1;
                case 'C' -> 3;
                default -> throw new IllegalArgumentException();
            };
        }

        return Integer.toString(total);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        char[] chars = input.toCharArray();
        int length = chars.length;

        if (length % 2 != 0) {
            throw new IllegalArgumentException("Input has an odd number of characters");
        }

        int total = 0;

        for (int currentIndex = 0; currentIndex < length - 1; currentIndex += 2) {

            char c1 = chars[currentIndex];
            char c2 = chars[currentIndex + 1];

            int pairTotal = getValuePart2(c1) + getValuePart2(c2);

            if (c1 != 'x' && c2 != 'x') {
                pairTotal += 2;
            }

            total += pairTotal;

        }

        return Integer.toString(total);

    }

    private static int getValuePart2(char c) {
        return switch (c) {
            case 'A', 'x' -> 0;
            case 'B' -> 1;
            case 'C' -> 3;
            case 'D' -> 5;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        char[] chars = input.toCharArray();
        int length = chars.length;

        if (length % 3 != 0) {
            throw new IllegalArgumentException("Input must have a number of characters multiple of 3");
        }

        int total = 0;

        for (int currentIndex = 0; currentIndex < length - 2; currentIndex += 3) {

            char c1 = chars[currentIndex];
            char c2 = chars[currentIndex + 1];
            char c3 = chars[currentIndex + 2];

            int numberOfX = 0;
            if (c1 == 'x') numberOfX++;
            if (c2 == 'x') numberOfX++;
            if (c3 == 'x') numberOfX++;

            total += getValuePart2(c1) + getValuePart2(c2) + getValuePart2(c3);
            if (numberOfX == 1) {
                // It's a pair
                total += 2; // 1 extra potion per creature
            } else if (numberOfX == 0) {
                // It's a group of three
                total += 6; // 2 extra potions per creature
            }

        }

        return Integer.toString(total);
    }
}
