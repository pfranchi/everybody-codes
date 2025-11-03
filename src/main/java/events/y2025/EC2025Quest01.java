package events.y2025;

import common.AbstractQuest;
import common.support.interfaces.MainEvent2025;
import common.support.interfaces.Quest01;
import common.support.params.ExecutionParameters;

import java.util.List;

public class EC2025Quest01 extends AbstractQuest implements MainEvent2025, Quest01 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        log("Welcome to EC 2025");

        String firstLine = inputLines.getFirst();
        String[] words = firstLine.split(",");
        int numberOfWords = words.length;

        String[] instructions = inputLines.getLast().split(",");

        int currentIndex = 0;
        for (String instruction: instructions) {

            int dir = instruction.charAt(0) == 'R' ? 1 : -1;
            int steps = Integer.parseInt(instruction.substring(1));

            currentIndex += steps * dir;
            currentIndex = Integer.min(Integer.max(currentIndex, 0), numberOfWords - 1);

        }

        return words[currentIndex];
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        String firstLine = inputLines.getFirst();
        String[] words = firstLine.split(",");
        int numberOfWords = words.length;

        String[] instructions = inputLines.getLast().split(",");

        int currentIndex = 0;
        for (String instruction: instructions) {

            int dir = instruction.charAt(0) == 'R' ? 1 : -1;
            int steps = Integer.parseInt(instruction.substring(1));

            currentIndex += steps * dir;

        }

        return words[currentIndex % numberOfWords];
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        String firstLine = inputLines.getFirst();
        String[] words = firstLine.split(",");
        int numberOfWords = words.length;

        String[] instructions = inputLines.getLast().split(",");

        for (String instruction: instructions) {

            int dir = instruction.charAt(0) == 'R' ? 1 : -1;
            int steps = Integer.parseInt(instruction.substring(1));
            int swapIndex = modInRange(dir * steps, numberOfWords);
            swap(words, swapIndex);

        }

        return words[0];
    }

    private void swap(String[] arr, int j) {

        String temp = arr[0];
        arr[0] = arr[j];
        arr[j] = temp;

    }

    private int modInRange(int x, int mod) {
        return (x % mod + mod) % mod;
    }

}
