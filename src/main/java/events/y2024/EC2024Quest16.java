package events.y2024;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import common.AbstractQuest;
import common.support.interfaces.MainEvent2024;
import common.support.interfaces.Quest16;
import common.support.params.ExecutionParameters;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EC2024Quest16 extends AbstractQuest implements MainEvent2024, Quest16 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfInputLines = inputLines.size();

        String[] firstLine = inputLines.getFirst().split(",");

        int numberOfCylinders = firstLine.length;

        List<Integer> numberOfPositionsTurned = Arrays.stream(firstLine).mapToInt(Integer::parseInt).boxed().toList();

        List<List<String>> cylinders = new ArrayList<>();
        for (int cylinderIndex = 0; cylinderIndex < numberOfCylinders; cylinderIndex++) {

            List<String> cylinder = new ArrayList<>();

            int columnInTheInput = 4 * cylinderIndex;

            for (int inputLineIndex = 2; inputLineIndex < numberOfInputLines; inputLineIndex++) {

                String inputLine = inputLines.get(inputLineIndex);
                if (inputLine.length() > columnInTheInput && inputLine.charAt(columnInTheInput) != ' ') {
                    String content = inputLine.substring(columnInTheInput, columnInTheInput + 3);
                    cylinder.add(content);
                }

            }

            cylinders.add(cylinder);

        }

        StringBuilder sb = new StringBuilder();
        for (int cylinderIndex = 0; cylinderIndex < numberOfCylinders; cylinderIndex++) {
            List<String> cylinder = cylinders.get(cylinderIndex);
            int length = cylinder.size();
            sb.append( cylinder.get(numberOfPositionsTurned.get(cylinderIndex) * 100 % length) );

            if (cylinderIndex != numberOfCylinders - 1) {
                sb.append(' ');
            }
        }

        return sb.toString();
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        long numberOfLeverPulls = 202420242024L;

        int numberOfInputLines = inputLines.size();

        String[] firstLine = inputLines.getFirst().split(",");

        int numberOfCylinders = firstLine.length;

        List<Integer> numberOfPositionsTurned = Arrays.stream(firstLine).mapToInt(Integer::parseInt).boxed().toList();

        List<List<String>> cylinders = new ArrayList<>();
        for (int cylinderIndex = 0; cylinderIndex < numberOfCylinders; cylinderIndex++) {

            List<String> cylinder = new ArrayList<>();

            int columnInTheInput = 4 * cylinderIndex;

            for (int inputLineIndex = 2; inputLineIndex < numberOfInputLines; inputLineIndex++) {

                String inputLine = inputLines.get(inputLineIndex);
                if (inputLine.length() > columnInTheInput && inputLine.charAt(columnInTheInput) != ' ') {
                    String content = inputLine.substring(columnInTheInput, columnInTheInput + 3);
                    cylinder.add(content);
                }

            }

            cylinders.add(cylinder);

        }

        log("Cylinder lengths are: {}", cylinders.stream().map(List::size).map(i -> Integer.toString(i)).collect(Collectors.joining(", ")) );

        BigInteger lcmOfCylinderLengths = cylinders.stream().mapToInt(List::size).mapToObj(BigInteger::valueOf).reduce(BigInteger.ONE, EC2024Quest16::lcm);

        log("lcmOfCylinderLengths is {}", lcmOfCylinderLengths);

        long quotient = numberOfLeverPulls / lcmOfCylinderLengths.longValue();

        long remainder = numberOfLeverPulls % lcmOfCylinderLengths.longValue();

        log("Quotient {} * lcm {} + remainder {} = number of lever pulls {}", quotient, lcmOfCylinderLengths, remainder, numberOfLeverPulls);


        long byteCoins = 0L;
        for (int pull = 1; pull <= lcmOfCylinderLengths.intValue(); pull++) {

            Multiset<Character> characters = HashMultiset.create();

            for (int cylinderIndex = 0; cylinderIndex < numberOfCylinders; cylinderIndex++) {
                List<String> cylinder = cylinders.get(cylinderIndex);
                int length = cylinder.size();

                String catFace = cylinder.get( numberOfPositionsTurned.get(cylinderIndex) * pull % length );

                characters.add(catFace.charAt(0)); // First character (eye on the left)
                characters.add(catFace.charAt(2)); // Third character (eye on the right)
            }

            for (Multiset.Entry<Character> entry: characters.entrySet()) {
                int value = entry.getCount();
                byteCoins += Integer.max(value - 2, 0);
            }

        }

        byteCoins *= quotient;

        for (int pull = 1; pull <= remainder; pull++) {

            Multiset<Character> characters = HashMultiset.create();

            for (int cylinderIndex = 0; cylinderIndex < numberOfCylinders; cylinderIndex++) {
                List<String> cylinder = cylinders.get(cylinderIndex);
                int length = cylinder.size();

                String catFace = cylinder.get( numberOfPositionsTurned.get(cylinderIndex) * pull % length );

                characters.add(catFace.charAt(0)); // First character (eye on the left)
                characters.add(catFace.charAt(2)); // Third character (eye on the right)
            }

            for (Multiset.Entry<Character> entry: characters.entrySet()) {
                int value = entry.getCount();
                byteCoins += Integer.max(value - 2, 0);
            }

        }

        return Long.toString(byteCoins);
    }

    private static BigInteger lcm(BigInteger val1, BigInteger val2) {
        if (val1.equals(BigInteger.ZERO) || val2.equals(BigInteger.ZERO)) {
            return BigInteger.ZERO;
        }
        return val1.divide( val1.gcd(val2) ).multiply(val2);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return NOT_IMPLEMENTED;
    }
}
