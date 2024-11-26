package common.stats;

import java.util.Optional;
import java.util.function.Consumer;

public class MinMaxStatistics<T extends Comparable<T>> implements Consumer<T> {

    private T min = null;
    private T max = null;

    public MinMaxStatistics() {
    }

    public MinMaxStatistics(T first) {
        this.min = first;
        this.max = first;
    }

    @Override
    public void accept(T t) {
        if (min == null) {
            min = t;
            max = t;
        } else {

            if (t.compareTo(min) < 0) {
                // New min found
                min = t;
            }

            if (t.compareTo(max) > 0) {
                // New max found
                max = t;
            }

        }
    }

    public Optional<T> getMin() {
        return Optional.ofNullable(min);
    }

    public Optional<T> getMax() {
        return Optional.ofNullable(max);
    }

}
