package common.stats;

import java.util.function.IntConsumer;

public class IntMinMaxStatistics implements IntConsumer {

    private int min = Integer.MAX_VALUE;
    private int max = Integer.MIN_VALUE;

    public IntMinMaxStatistics() {
    }

    public IntMinMaxStatistics(int min, int max) {

        if (min > max) throw new IllegalArgumentException("Minimum greater than maximum");

        this.min = min;
        this.max = max;
    }

    @Override
    public void accept(int value) {
        min = Math.min(min, value);
        max = Math.max(max, value);
    }

    public void combine(IntMinMaxStatistics other) {
        min = Math.min(min, other.min);
        max = Math.max(max, other.max);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    static final class NoOp extends IntMinMaxStatistics {

        @Override
        public void accept(int value) {
            // No need to execute anything
        }
    }

    public static IntMinMaxStatistics noOp() {
        return new NoOp();
    }

}
