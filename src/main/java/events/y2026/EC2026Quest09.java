package events.y2026;

import common.AbstractQuest;
import common.support.interfaces.Quest09;
import common.support.interfaces.MainEvent2026;
import common.support.params.ExecutionParameters;

import java.math.BigInteger;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EC2026Quest09 extends AbstractQuest implements MainEvent2026, Quest09 {

    private static final Pattern PATTERN = Pattern.compile("^id=\\d+, growth=(?<growth>\\d+), limit=(?<limit>\\d+)$");

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        LimitingAmountFunction function = (branchingPoints, countsByBranchSize) -> {

            BigInteger total = countsByBranchSize.entrySet()
                    .stream()
                    .map(entry -> entry.getKey().multiply(entry.getValue()))
                    .reduce(BigInteger.ZERO, BigInteger::add);

            return total.add(branchingPoints);

        };
        boolean allowSkips = false;

        return solve(inputLines, function, allowSkips);

    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        LimitingAmountFunction function = (branchingPoints, _) -> branchingPoints.add(BigInteger.ONE);
        boolean allowSkips = true;

        return solve(inputLines, function, allowSkips);

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        LimitingAmountFunction function = (branchingPoints, _) -> branchingPoints.add(BigInteger.ONE);
        boolean allowSkips = true;

        return solve(inputLines, function, allowSkips);

    }

    private String solve(List<String> inputLines, LimitingAmountFunction function, boolean allowSkips) {

        BigInteger total = BigInteger.ZERO;

        for (String line : inputLines) {
            Matcher matcher = PATTERN.matcher(line);
            if (matcher.matches()) {
                BigInteger growth = new BigInteger(matcher.group("growth"));
                BigInteger limit = new BigInteger(matcher.group("limit"));

                BigInteger seconds = secondsUntilLimitIsReached(growth, limit, function, allowSkips);

                total = total.add(seconds);

            }
        }

        return total.toString();

    }

    private BigInteger secondsUntilLimitIsReached(BigInteger growth, BigInteger limit,
                                                  LimitingAmountFunction function, boolean allowSkips) {

        if (limit.equals(BigInteger.ONE)) {
            return BigInteger.ONE;
        }

        BigInteger seconds = BigInteger.ZERO;
        NavigableMap<BigInteger, BigInteger> countsByBranchSize = new TreeMap<>();
        countsByBranchSize.put(BigInteger.ZERO, BigInteger.ONE);

        BigInteger branchingPoints = BigInteger.ZERO;

        BigInteger limitingAmount;
        do {

            BigInteger delta = BigInteger.ONE;

            if (allowSkips) {
                // Compute the delta that we skip
                BigInteger largestBranchSize = countsByBranchSize.lastKey();
                delta = growth.subtract(largestBranchSize);
            }

            seconds = seconds.add(delta);

            // Compute the plant after an amount of seconds equal to delta
            NavigableMap<BigInteger, BigInteger> newCounts = new TreeMap<>();
            for (Map.Entry<BigInteger, BigInteger> entry: countsByBranchSize.entrySet()) {
                BigInteger branchSize = entry.getKey();
                BigInteger count = entry.getValue();

                BigInteger newBranchSize = branchSize.add(delta);

                if (newBranchSize.equals(growth)) {

                    BigInteger halfBranchSize = growth.divide(BigInteger.valueOf(2)); // this rounds the value down

                    newCounts.merge(halfBranchSize, count.multiply(BigInteger.valueOf(2)), BigInteger::add); // the two halves
                    newCounts.merge(BigInteger.ONE, count, BigInteger::add); // one new branch of size 1, for each count
                    branchingPoints = branchingPoints.add(count);

                } else {
                    newCounts.merge(newBranchSize, count, BigInteger::add);
                }

            }

            countsByBranchSize = newCounts;

            limitingAmount = function.apply(branchingPoints, countsByBranchSize);

        } while (limitingAmount.compareTo(limit) < 0);

        return seconds;

    }

    @FunctionalInterface
    private interface LimitingAmountFunction extends BiFunction<BigInteger, Map<BigInteger, BigInteger>, BigInteger> {

        BigInteger computeLimitingAmount(BigInteger branchingPoints, Map<BigInteger, BigInteger> countsByBranchSize);

        @Override
        default BigInteger apply(BigInteger branchingPoints, Map<BigInteger, BigInteger> countsByBranchSize) {
            return computeLimitingAmount(branchingPoints, countsByBranchSize);
        }
    }

}
