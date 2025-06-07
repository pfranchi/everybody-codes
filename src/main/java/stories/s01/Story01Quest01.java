package stories.s01;

import common.stats.AbstractQuest;
import common.support.interfaces.Quest01;
import common.support.interfaces.Story01;
import common.support.params.ExecutionParameters;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Story01Quest01 extends AbstractQuest implements Story01, Quest01 {

    private static final Pattern PATTERN = Pattern.compile("^A=(?<a>\\d+) B=(?<b>\\d+) C=(?<c>\\d+) X=(?<x>\\d+) Y=(?<y>\\d+) Z=(?<z>\\d+) M=(?<m>\\d+)$");

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        BigInteger max = BigInteger.ZERO;

        for (String line: inputLines) {

            Matcher matcher = PATTERN.matcher(line);
            if (matcher.matches()) {

                int a = Integer.parseInt(matcher.group("a"));
                int b = Integer.parseInt(matcher.group("b"));
                int c = Integer.parseInt(matcher.group("c"));
                int x = Integer.parseInt(matcher.group("x"));
                int y = Integer.parseInt(matcher.group("y"));
                int z = Integer.parseInt(matcher.group("z"));
                int m = Integer.parseInt(matcher.group("m"));

                BigInteger result = eni(a, x, m).add(eni(b, y, m)).add(eni(c, z, m));

                max = max.max(result);

            }

        }

        return max.toString();
    }

    private BigInteger eni(int n, int exp, int mod) {

        List<Integer> remainders = new ArrayList<>();

        int currentScore = 1;

        for (int step = 1; step <= exp; step++) {

            currentScore *= n;
            currentScore %= mod;
            remainders.addFirst(currentScore);

        }

        String result = remainders.stream().map(i -> Integer.toString(i)).collect(Collectors.joining());

        return new BigInteger(result);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        BigInteger max = BigInteger.ZERO;

        for (String line: inputLines) {


            Matcher matcher = PATTERN.matcher(line);
            if (matcher.matches()) {

                long a = Long.parseLong(matcher.group("a"));
                long b = Long.parseLong(matcher.group("b"));
                long c = Long.parseLong(matcher.group("c"));
                long x = Long.parseLong(matcher.group("x"));
                long y = Long.parseLong(matcher.group("y"));
                long z = Long.parseLong(matcher.group("z"));
                long m = Long.parseLong(matcher.group("m"));

                BigInteger add1 = eniPart2(a, x, m);
                BigInteger add2 = eniPart2(b, y, m);
                BigInteger add3 = eniPart2(c, z, m);

                BigInteger result = add1.add(add2).add(add3);

                max = max.max(result);

            }

        }

        return max.toString();

    }

    private BigInteger eniPart2(long n, long exp, long mod) {

        long startingExponent = Long.max(exp - 5, 0);
        long numberOfRemainders = exp - startingExponent;

        BigInteger bigIntegerN = BigInteger.valueOf(n);
        BigInteger bigIntegerExp = BigInteger.valueOf(startingExponent);
        BigInteger bigIntegerMod = BigInteger.valueOf(mod);

        BigInteger currentScore = bigIntegerN.modPow(bigIntegerExp, bigIntegerMod);

        List<BigInteger> remainders = new ArrayList<>();

        for (int step = 1; step <= numberOfRemainders; step++) {

            currentScore = currentScore.multiply(bigIntegerN);
            currentScore = currentScore.mod(bigIntegerMod);
            remainders.addFirst(currentScore);

        }

        String result = remainders.stream()
                .map(BigInteger::toString)
                .collect(Collectors.joining());

        return new BigInteger(result);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        long max = Long.MIN_VALUE;

        for (String line: inputLines) {

            Matcher matcher = PATTERN.matcher(line);
            if (matcher.matches()) {

                long a = Long.parseLong(matcher.group("a"));
                long b = Long.parseLong(matcher.group("b"));
                long c = Long.parseLong(matcher.group("c"));
                long x = Long.parseLong(matcher.group("x"));
                long y = Long.parseLong(matcher.group("y"));
                long z = Long.parseLong(matcher.group("z"));
                long m = Long.parseLong(matcher.group("m"));

                long res = eniPart3(a, x, m) + eniPart3(b, y, m) + eniPart3(c, z, m);

                max = Long.max(max, res);

            }

        }

        return Long.toString(max);

    }

    private long eniPart3(long n, long exp, long mod) {

        Map<Long, Integer> seenRemainders = new LinkedHashMap<>();

        long currentRemainder = 1;
        //seenRemainders.put(currentRemainder, 0);

        int step = 1;
        while (step <= exp) {

            currentRemainder *= n;
            currentRemainder %= mod;

            if (seenRemainders.containsKey(currentRemainder)) {

                int previousStep = seenRemainders.get(currentRemainder);
                int delta = step - previousStep;

                long numberOfBlocks = (exp - previousStep) / delta;

                //emptyLine();
                //log("Remainder {} seen at step {} was already seen at step {}. Delta = {}", currentRemainder, step, previousStep, delta);
                //log("Will have {} block(s)", numberOfBlocks);

                long sumOfBlock = 0;
                for (Map.Entry<Long, Integer> entry: seenRemainders.entrySet()) {
                    int blockStep = entry.getValue();
                    if (blockStep >= previousStep) {
                        sumOfBlock += entry.getKey();
                    }
                }

                //long sumOfBlock = seenRemainders.keySet().stream().mapToLong(l -> l).sum();

                long total = numberOfBlocks * sumOfBlock;

                // Add the remainders that appear before the step that is the first to appears twice (this step is probably not required)
                for (Map.Entry<Long, Integer> entry: seenRemainders.entrySet()) {
                    if (entry.getValue() < previousStep) {
                        total += entry.getKey();
                    }
                }

                //log("Remainders so far: {}. Sum of block = {}. Total so far = {}", seenRemainders, sumOfBlock, total);

                total += currentRemainder;

                long start = previousStep + numberOfBlocks * delta;

                for (long finalStep = start + 1; finalStep <= exp; finalStep++ ) {
                    currentRemainder *= n;
                    currentRemainder %= mod;
                    total += currentRemainder;
                }

                //log("Total is {}", total);

                return total;

            }

            seenRemainders.put(currentRemainder, step);

            step++;

        }

        //log("Remainders are: {}", seenRemainders);

        return seenRemainders.keySet().stream().mapToLong(l -> l).sum();

    }


}
