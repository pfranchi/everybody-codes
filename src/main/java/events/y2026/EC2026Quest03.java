package events.y2026;

import common.AbstractQuest;
import common.support.interfaces.MainEvent2026;
import common.support.interfaces.Quest03;
import common.support.params.ExecutionParameters;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class EC2026Quest03 extends AbstractQuest implements MainEvent2026, Quest03 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(inputLines, ISO_PATTERN, false, (_, _) -> true);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(inputLines, ISO_PATTERN, false, (_, _) -> true);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        BiPredicate<BigInteger, ParseResult> condition = (n, parseResult) -> isSmallestPossibleRepresentation(n, parseResult.numberUsed(), parseResult.bases());
        return solve(inputLines, ISO2_PATTERN, true, condition);
    }

    private String solve(List<String> inputLines, Pattern pattern, boolean parseCoefficientsAsInt, BiPredicate<BigInteger, ParseResult> condition) {
        BigInteger sum = BigInteger.ZERO;

        for (String line: inputLines) {
            ParseResult parseResult = ParseResult.from(line, pattern, parseCoefficientsAsInt);
            char[] bases = parseResult.bases();
            char[] coefficients = parseResult.coefficients();

            BigInteger n = compute(bases, coefficients);
            if (condition.test(n, parseResult)) {
                sum = sum.add(n);
            }

        }

        return sum.toString();
    }

    private static final Pattern ISO_PATTERN = Pattern.compile("^ISO-(?<bases>\\d+) (?<coeffs>\\d+)$");
    private static final Pattern ISO2_PATTERN = Pattern.compile("^ISO2-(?<bases>\\d+) (?<coeffs>\\d+)$");

    private record ParseResult(char[] bases, char[] coefficients, int numberUsed) {
        public static ParseResult from(String line, Pattern pattern, boolean parseCoefficientsAsInt) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                char[] bases = StringUtils.reverse(matcher.group("bases")).toCharArray();
                char[] coefficients = StringUtils.reverse(matcher.group("coeffs")).toCharArray();
                int numberUsed = 0;
                if (parseCoefficientsAsInt) {
                    numberUsed = Integer.parseInt(matcher.group("coeffs"));
                }
                return new ParseResult(bases, coefficients, numberUsed);
            }
            throw new IllegalArgumentException();
        }
    }

    private boolean isSmallestPossibleRepresentation(BigInteger n, int numberUsed, char[] bases) {

        return IntStream.range(0, numberUsed)
                .mapToObj(smallerCandidate -> StringUtils.reverse(Integer.toString(smallerCandidate)).toCharArray())
                .map(coefficients -> compute(bases, coefficients))
                .noneMatch(n::equals);

    }

    private BigInteger compute(char[] bases, char[] coefficients) {
        BigInteger n = BigInteger.ZERO;
        for (int i = 0; i < coefficients.length; i++) {
            BigInteger base = new BigInteger(String.valueOf(bases[i % (bases.length)]));
            n = n.add(base.pow(i).multiply(new BigInteger(String.valueOf(coefficients[i]))));
        }
        return n;
    }

}
