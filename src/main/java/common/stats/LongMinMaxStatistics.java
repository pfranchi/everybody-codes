package common.stats;

import java.util.function.LongConsumer;

public class LongMinMaxStatistics implements LongConsumer {

    private long min = Integer.MAX_VALUE;
    private long max = Integer.MIN_VALUE;

    public LongMinMaxStatistics() {
    }

    public LongMinMaxStatistics(long min, long max) {

        if (min > max) throw new IllegalArgumentException("Minimum greater than maximum");

        this.min = min;
        this.max = max;
    }

    @Override
    public void accept(long value) {
        min = Math.min(min, value);
        max = Math.max(max, value);
    }

    public void combine(LongMinMaxStatistics other) {
        min = Math.min(min, other.min);
        max = Math.max(max, other.max);
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

}
