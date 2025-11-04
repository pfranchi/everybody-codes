package events.y2025;

import common.AbstractQuest;
import common.support.interfaces.Quest02;
import common.support.interfaces.MainEvent2025;
import common.support.params.ExecutionParameters;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EC2025Quest02 extends AbstractQuest implements MainEvent2025, Quest02 {

    private record Complex(long x, long y) {

        public static final Complex ZERO = new Complex(0L, 0L);

        public Complex add(Complex c) {
            long sx = x + c.x;
            long sy = y + c.y;
            return new Complex(sx, sy);
        }

        public Complex multiply(Complex c) {

            long px = x * c.x - y * c.y;
            long py = x * c.y + y * c.x;

            return new Complex(px, py);

        }

        public Complex divide(Complex c) {
            long dx = x / c.x;
            long dy = y / c.y;
            return new Complex(dx, dy);
        }

        @Override
        @Nonnull
        public String toString() {
            return "[" + x + "," + y + "]";
        }

    }

    private static final Pattern PATTERN = Pattern.compile("^A=\\[(?<x>-?\\d+),(?<y>-?\\d+)]$");

    private Complex parse(String s) {
        Matcher matcher = PATTERN.matcher(s);
        if (!matcher.matches()) {
            throw new IllegalArgumentException();
        }
        int x = Integer.parseInt(matcher.group("x"));
        int y = Integer.parseInt(matcher.group("y"));
        return new Complex(x, y);
    }

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Complex a = parse(inputLines.getFirst());

        Complex result = Complex.ZERO;
        Complex complexTen = new Complex(10, 10);

        for (int i = 1; i <= 3; i++) {
            result = result.multiply(result);
            result = result.divide(complexTen);
            result = result.add(a);
        }

        return result.toString();
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Complex divisor = new Complex(100000,100000);

        Complex a = parse(inputLines.getFirst());

        int count = 0;

        for (int xDelta = 0; xDelta <= 100; xDelta++) {
            for (int yDelta = 0; yDelta <= 100; yDelta++) {

                Complex c = new Complex(a.x + xDelta * 10, a.y + yDelta * 10);

                if (isEngraved(c, divisor)) {
                    count++;
                }

            }
        }

        return Integer.toString(count);
    }

    private boolean exceedsLimit(Complex c) {
        return c.x > 1000000 || c.x < -1000000 || c.y > 1000000 || c.y < -1000000;
    }

    private boolean isEngraved(Complex c, Complex divisor) {

        Complex result = Complex.ZERO;
        for (int i = 1; i <= 100; i++) {

            result = result.multiply(result);
            result = result.divide(divisor);
            result = result.add(c);

            if (exceedsLimit(result)) {
                return false;
            }

        }

        return true;

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Complex divisor = new Complex(100000,100000);

        Complex a = parse(inputLines.getFirst());

        int count = 0;

        for (int xDelta = 0; xDelta <= 1000; xDelta++) {
            for (int yDelta = 0; yDelta <= 1000; yDelta++) {

                Complex c = new Complex(a.x + xDelta, a.y + yDelta);

                if (isEngraved(c, divisor)) {
                    count++;
                }

            }
        }

        return Integer.toString(count);
    }

}
