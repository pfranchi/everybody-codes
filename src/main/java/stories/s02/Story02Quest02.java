package stories.s02;

import common.AbstractQuest;
import common.Strings;
import common.support.interfaces.Quest02;
import common.support.interfaces.Story02;
import common.support.params.ExecutionIntParameter;
import common.support.params.ExecutionParameters;

import java.util.ArrayList;
import java.util.List;

public class Story02Quest02 extends AbstractQuest implements Story02, Quest02 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        List<Character> balloons = new ArrayList<>();
        for (char c: Strings.firstRow(input).toCharArray()) {
            balloons.add(c);
        }

        int currentColorIndex = 0;
        int fluffboltShot = 0;

        while (!balloons.isEmpty()) {

            fluffboltShot++;

            char currentColor = switch (currentColorIndex) {
                case 0 -> 'R';
                case 1 -> 'G';
                case 2 -> 'B';
                default -> throw new IllegalArgumentException();
            };

            boolean fluffboltStops = false;

            do {

                char currentBalloon = balloons.removeFirst();

                if (currentBalloon != currentColor) {
                    fluffboltStops = true;
                }

            } while (!fluffboltStops && !balloons.isEmpty());

            currentColorIndex++;
            currentColorIndex %= 3;

        }

        return Integer.toString(fluffboltShot);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int sequenceRepetitions = 100;

        if (executionParameters instanceof ExecutionIntParameter(int value)) {
            sequenceRepetitions = value;
        }

        List<Character> baseBalloons = new ArrayList<>();
        for (char c: Strings.firstRow(input).toCharArray()) {
            baseBalloons.add(c);
        }

        List<Character> balloons = new ArrayList<>();
        for (int repetition = 1; repetition <= sequenceRepetitions; repetition++) {
            balloons.addAll(baseBalloons);
        }

        int currentColorIndex = 0;
        int fluffboltShot = 0;

        while (!balloons.isEmpty()) {

            fluffboltShot++;

            char currentColor = switch (currentColorIndex) {
                case 0 -> 'R';
                case 1 -> 'G';
                case 2 -> 'B';
                default -> throw new IllegalArgumentException();
            };

            char currentBalloon = balloons.removeFirst();

            if (currentBalloon == currentColor) {

                // The shot passes through

                if (balloons.size() % 2 == 1) {
                    // It has now odd size, which means that it had even size before the shot
                    balloons.remove(balloons.size() / 2);
                }
            }

            currentColorIndex++;
            currentColorIndex %= 3;

        }

        return Integer.toString(fluffboltShot);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        String baseSequence = Strings.firstRow(input);

        int baseSequenceLength = baseSequence.length();

        int sequenceRepetitions = 100000;

        int currentNumberOfBalloons = baseSequenceLength * sequenceRepetitions;
        int currentColorIndex = 0;
        int fluffboltShot = 0;

        int firstPassMax = (baseSequenceLength * sequenceRepetitions) / 2;

        StringBuilder sb = new StringBuilder();

        int secondHalfIndex = 0;

        for (int i = 0; i < firstPassMax; i++) {
            char currentColor = switch (i % 3) {
                case 0 -> 'R';
                case 1 -> 'G';
                case 2 -> 'B';
                default -> throw new IllegalArgumentException();
            };

            char currentBalloon = baseSequence.charAt( i % baseSequenceLength );

            if (currentNumberOfBalloons % 2 == 0) {

                if (currentBalloon == currentColor) {
                    // Passes through and hits the balloon on the other side

                    currentNumberOfBalloons--;

                } else {

                    // The balloon on the other side "survives" and will not be hit in this pass
                    sb.append(baseSequence.charAt(secondHalfIndex % baseSequenceLength));

                }

                secondHalfIndex++;

            }

            currentNumberOfBalloons--;

        }

        currentColorIndex += firstPassMax;

        fluffboltShot += firstPassMax;

        for (int i = secondHalfIndex; i < firstPassMax; i++) {
            sb.append(baseSequence.charAt(i % baseSequenceLength));
        }

        while (!sb.isEmpty()) {

            String fullSequence = sb.toString();
            int fullSequenceLength = fullSequence.length();
            int lengthOfThisPass = (fullSequenceLength + 1) / 2; // if even, this is half of fullSequenceLength. if odd, this is half rounded up.

            sb = new StringBuilder();

            secondHalfIndex = lengthOfThisPass;

            for (int i = 0; i < lengthOfThisPass; i++) {
                char currentColor = switch ((currentColorIndex + i) % 3) {
                    case 0 -> 'R';
                    case 1 -> 'G';
                    case 2 -> 'B';
                    default -> throw new IllegalArgumentException();
                };

                char currentBalloon = fullSequence.charAt(i);

                if (currentNumberOfBalloons % 2 == 0) {
                    if (currentBalloon == currentColor) {
                        // Pass through and hit at opposite side
                        currentNumberOfBalloons--;
                    } else {
                        sb.append(fullSequence.charAt(secondHalfIndex));
                    }

                    secondHalfIndex++;

                }

                currentNumberOfBalloons--;

            }

            currentColorIndex += lengthOfThisPass;
            fluffboltShot += lengthOfThisPass;

            sb.append(fullSequence.substring(secondHalfIndex));

        }

        return Integer.toString(fluffboltShot);

    }

}
