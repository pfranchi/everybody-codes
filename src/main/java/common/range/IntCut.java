package common.range;

import com.google.common.collect.BoundType;
import com.google.common.primitives.Booleans;

import java.io.Serial;
import java.io.Serializable;

abstract sealed class IntCut 
        implements Comparable<IntCut>, Serializable {

    @Serial
    private static final long serialVersionUID = 0;

    final int endpoint;

    IntCut(int endpoint) {
        this.endpoint = endpoint;
    }

    abstract boolean isLessThan(int value);

    abstract BoundType typeAsLowerBound();

    abstract BoundType typeAsUpperBound();

    abstract IntCut withLowerBoundType(BoundType boundType);

    abstract IntCut withUpperBoundType(BoundType boundType);

    abstract void describeAsLowerBound(StringBuilder sb);

    abstract void describeAsUpperBound(StringBuilder sb);

    abstract int leastValueAbove();

    abstract int greatestValueBelow();

    IntCut canonical() {
        return this;
    }

    @Override
    public int compareTo(IntCut o) {
        
        if (o == belowMinValue()) {
            return 1;
        }
        
        if (o == aboveMaxValue()) {
            return -1;
        }
        
        int result = Integer.compare(endpoint, o.endpoint);

        if (result != 0) {
            return result;
        }

        return Booleans.compare(this instanceof AboveValue, o instanceof AboveValue);
    }

    int endpoint() {
        return endpoint;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof IntCut ic) {
            return compareTo(ic) == 0;
        }

        return false;
    }

    @Override
    public abstract int hashCode();

    static IntCut belowMinValue() {
        return BelowMinValue.INSTANCE;
    }

    private static final class BelowMinValue extends IntCut {

        private static final BelowMinValue INSTANCE = new BelowMinValue();

        @Override
        int endpoint() {
            throw new IllegalStateException("range unbounded on this side");
        }

        public BelowMinValue() {
            super(0);
        }

        @Override
        boolean isLessThan(int value) {
            return true;
        }

        @Override
        BoundType typeAsLowerBound() {
            throw new IllegalStateException();
        }

        @Override
        BoundType typeAsUpperBound() {
            throw new AssertionError("this statement should be unreachable");
        }

        @Override
        IntCut withLowerBoundType(BoundType boundType) {
            throw new IllegalStateException();
        }

        @Override
        IntCut withUpperBoundType(BoundType boundType) {
            throw new AssertionError("this statement should be unreachable");
        }

        @Override
        void describeAsLowerBound(StringBuilder sb) {
            sb.append("(-∞");
        }

        @Override
        void describeAsUpperBound(StringBuilder sb) {
            throw new AssertionError();
        }

        @Override
        int leastValueAbove() {
            return Integer.MIN_VALUE;
        }

        @Override
        int greatestValueBelow() {
            throw new AssertionError();
        }

        @Override
        IntCut canonical() {
            return belowValue(Integer.MIN_VALUE);
        }

        @Override
        public int compareTo(IntCut o) {
            return o == this ? 0 : -1;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }

        @Override
        public String toString() {
            return "-∞";
        }
    }

    static IntCut aboveMaxValue() {
        return AboveMaxValue.INSTANCE;
    }

    private static final class AboveMaxValue extends IntCut {

        private static final AboveMaxValue INSTANCE = new AboveMaxValue();

        public AboveMaxValue() {
            super(0);
        }

        @Override
        int endpoint() {
            throw new IllegalStateException("range unbounded on this side");
        }

        @Override
        boolean isLessThan(int value) {
            return false;
        }

        @Override
        BoundType typeAsLowerBound() {
            throw new AssertionError("this statement should be unreachable");
        }

        @Override
        BoundType typeAsUpperBound() {
            throw new IllegalStateException();
        }

        @Override
        IntCut withLowerBoundType(BoundType boundType) {
            throw new AssertionError("this statement should be unreachable");
        }

        @Override
        IntCut withUpperBoundType(BoundType boundType) {
            throw new IllegalStateException();
        }

        @Override
        void describeAsLowerBound(StringBuilder sb) {
            throw new AssertionError();
        }

        @Override
        void describeAsUpperBound(StringBuilder sb) {
            sb.append("+∞)");
        }

        @Override
        int leastValueAbove() {
            throw new AssertionError();
        }

        @Override
        int greatestValueBelow() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int compareTo(IntCut o) {
            return (o == this) ? 0 : 1;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }

        @Override
        public String toString() {
            return "+∞";
        }

    }

    static IntCut belowValue(int endpoint) {
        return new BelowValue(endpoint);
    }

    private static final class BelowValue extends IntCut {

        public BelowValue(int endpoint) {
            super(endpoint);
        }

        @Override
        boolean isLessThan(int value) {
            return endpoint <= value;
        }

        @Override
        BoundType typeAsLowerBound() {
            return BoundType.CLOSED;
        }

        @Override
        BoundType typeAsUpperBound() {
            return BoundType.OPEN;
        }

        @Override
        IntCut withLowerBoundType(BoundType boundType) {

            return switch (boundType) {
                case CLOSED -> this;
                case OPEN -> endpoint == Integer.MIN_VALUE ? belowMinValue() : new AboveValue(endpoint - 1);
            };

        }

        @Override
        IntCut withUpperBoundType(BoundType boundType) {
            return switch (boundType) {
                case CLOSED -> endpoint == Integer.MIN_VALUE ? aboveMaxValue() : new AboveValue(endpoint - 1);
                case OPEN -> this;
            };
        }

        @Override
        void describeAsLowerBound(StringBuilder sb) {
            sb.append('[').append(endpoint);
        }

        @Override
        void describeAsUpperBound(StringBuilder sb) {
            sb.append(endpoint).append(')');
        }

        @Override
        int leastValueAbove() {
            return endpoint;
        }

        @Override
        int greatestValueBelow() {
            if (endpoint == Integer.MIN_VALUE) {
                throw new AssertionError();
            }
            return endpoint - 1;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(endpoint);
        }

        @Override
        public String toString() {
            return "\\" + endpoint + "/";
        }

    }

    static IntCut aboveValue(int endpoint) {
        return new AboveValue(endpoint);
    }

    private static final class AboveValue extends IntCut {

        public AboveValue(int endpoint) {
            super(endpoint);
        }

        @Override
        boolean isLessThan(int value) {
            return endpoint < value;
        }

        @Override
        BoundType typeAsLowerBound() {
            return BoundType.OPEN;
        }

        @Override
        BoundType typeAsUpperBound() {
            return BoundType.CLOSED;
        }

        @Override
        IntCut withLowerBoundType(BoundType boundType) {
            return switch (boundType) {
                case OPEN -> this;
                case CLOSED -> endpoint == Integer.MAX_VALUE ? belowMinValue() : belowValue(endpoint + 1);
            };
        }

        @Override
        IntCut withUpperBoundType(BoundType boundType) {
            return switch (boundType) {
                case OPEN -> endpoint == Integer.MAX_VALUE ? aboveMaxValue() : belowValue(endpoint + 1);
                case CLOSED -> this;
            };
        }

        @Override
        void describeAsLowerBound(StringBuilder sb) {
            sb.append('(').append(endpoint);
        }

        @Override
        void describeAsUpperBound(StringBuilder sb) {
            sb.append(endpoint).append(']');
        }

        @Override
        int leastValueAbove() {
            if (endpoint == Integer.MAX_VALUE) {
                throw new AssertionError();
            }
            return endpoint + 1;
        }

        @Override
        int greatestValueBelow() {
            return endpoint;
        }

        @Override
        IntCut canonical() {

            if (endpoint == Integer.MAX_VALUE) {
                return aboveMaxValue();
            }

            return belowValue(endpoint + 1);
        }

        @Override
        public int hashCode() {
            return ~Integer.hashCode(endpoint);
        }

        @Override
        public String toString() {
            return "/" + endpoint + "\\";
        }

    }

}
