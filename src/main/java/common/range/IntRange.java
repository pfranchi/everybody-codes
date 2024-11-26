package common.range;

import com.google.common.collect.BoundType;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

import java.util.Comparator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

public final class IntRange {

    static class LowerBoundFn implements Function<IntRange, IntCut> {

        static final LowerBoundFn INSTANCE = new LowerBoundFn();

        @Override
        public IntCut apply(IntRange intRange) {
            return intRange.lowerBound;
        }
    }

    static class UpperBoundFn implements Function<IntRange, IntCut> {

        static final UpperBoundFn INSTANCE = new UpperBoundFn();

        @Override
        public IntCut apply(IntRange intRange) {
            return intRange.upperBound;
        }
    }

    static Function<IntRange, IntCut> lowerBoundFn() {
        return LowerBoundFn.INSTANCE;
    }

    static Function<IntRange, IntCut> upperBoundFn() {
        return UpperBoundFn.INSTANCE;
    }

    static Ordering<IntRange> rangeLexOrdering() {
        throw new UnsupportedOperationException();
    }

    static IntRange create(IntCut lowerBound, IntCut upperBound) {
        return new IntRange(lowerBound, upperBound);
    }

    public static IntRange open(int lower, int upper) {
        return create(IntCut.aboveValue(lower), IntCut.belowValue(upper));
    }

    public static IntRange closed(int lower, int upper) {
        return create(IntCut.belowValue(lower), IntCut.aboveValue(upper));
    }

    public static IntRange closedOpen(int lower, int upper) {
        return create(IntCut.belowValue(lower), IntCut.belowValue(upper));
    }

    public static IntRange openClosed(int lower, int upper) {
        return create(IntCut.aboveValue(lower), IntCut.aboveValue(upper));
    }

    public static IntRange range(int lower, BoundType lowerType, int upper, BoundType upperType) {
        Objects.requireNonNull(lowerType);
        Objects.requireNonNull(upperType);

        IntCut lowerBound = lowerType == BoundType.OPEN ? IntCut.aboveValue(lower) : IntCut.belowValue(lower);
        IntCut upperBound = upperType == BoundType.OPEN ? IntCut.belowValue(upper) : IntCut.aboveValue(upper);

        return create(lowerBound, upperBound);
    }

    public static IntRange lessThanExclusive(int endpoint) {
        return create(IntCut.belowMinValue(), IntCut.belowValue(endpoint));
    }

    public static IntRange lessThanInclusive(int endpoint) {
        return create(IntCut.belowMinValue(), IntCut.aboveValue(endpoint));
    }

    public static IntRange lessThan(int endpoint, BoundType boundType) {
        return switch (boundType) {
            case OPEN -> lessThanExclusive(endpoint);
            case CLOSED -> lessThanInclusive(endpoint);
        };
    }

    public static IntRange greaterThanExclusive(int endpoint) {
        return create(IntCut.aboveValue(endpoint), IntCut.aboveMaxValue());
    }

    public static IntRange greaterThanInclusive(int endpoint) {
        return create(IntCut.belowValue(endpoint), IntCut.aboveMaxValue());
    }

    public static IntRange greaterThan(int endpoint, BoundType boundType) {
        return switch (boundType) {
            case OPEN -> greaterThanExclusive(endpoint);
            case CLOSED -> greaterThanInclusive(endpoint);
        };
    }

    private static final IntRange ALL = new IntRange(IntCut.belowMinValue(), IntCut.aboveMaxValue());

    public static IntRange all() {
        return ALL;
    }

    public static IntRange singleton(int value) {
        return closed(value, value);
    }

    public static IntRange encloseAll(Iterable<Integer> values) {
        Objects.requireNonNull(values);

        if (values instanceof SortedSet<Integer> sortedSet) {
            Comparator<?> comparator = sortedSet.comparator();
            if (comparator == null) { // Natural order
                return closed(sortedSet.getFirst(), sortedSet.getLast());
            }
        }

        SortedSet<Integer> sortedValues = new TreeSet<>();
        for (int value: values) {
            sortedValues.add(value);
        }

        return closed(sortedValues.getFirst(), sortedValues.getLast());

    }

    final IntCut lowerBound;
    final IntCut upperBound;

    private IntRange(IntCut lowerBound, IntCut upperBound) {
        this.lowerBound = Objects.requireNonNull(lowerBound);
        this.upperBound = Objects.requireNonNull(upperBound);
        if (lowerBound.compareTo(upperBound) > 0 || lowerBound == IntCut.aboveMaxValue() || upperBound == IntCut.belowMinValue()) {
            throw new IllegalArgumentException("Invalid range: " + Ranges.toString(lowerBound, upperBound));
        }
    }

    public boolean hasLowerBound() {
        return lowerBound != IntCut.belowMinValue();
    }

    public int lowerEndpoint() {
        return lowerBound.endpoint();
    }

    public BoundType lowerBoundType() {
        return lowerBound.typeAsLowerBound();
    }

    public boolean hasUpperBound() {
        return upperBound != IntCut.aboveMaxValue();
    }

    public int upperEndpoint() {
        return upperBound.endpoint();
    }

    public BoundType upperBoundType() {
        return upperBound.typeAsUpperBound();
    }

    public boolean isEmpty() {
        return lowerBound.equals(upperBound);
    }

    public boolean contains(int value) {
        return lowerBound.isLessThan(value) && !upperBound.isLessThan(value);
    }

    public boolean containsAll(Iterable<Integer> values) {
        if (Iterables.isEmpty(values)) {
            return true;
        }

        if (values instanceof SortedSet<Integer> sortedSet) {
            Comparator<?> comparator = sortedSet.comparator();
            if (comparator == null) { // Natural order
                return contains(sortedSet.getFirst()) && contains(sortedSet.getLast());
            }
        }

        for (int value: values) {
            if (!contains(value)) {
                return false;
            }
        }

        return true;

    }

    public boolean encloses(IntRange other) {
        return lowerBound.compareTo(other.lowerBound) <= 0 && upperBound.compareTo(other.upperBound) >= 0;
    }

    public boolean isConnected(IntRange other) {
        return lowerBound.compareTo(other.upperBound) <= 0 && other.lowerBound.compareTo(upperBound) <= 0;
    }

    public IntRange intersection(IntRange connectedRange) {
        int lowerCmp = lowerBound.compareTo(connectedRange.lowerBound);
        int upperCmp = upperBound.compareTo(connectedRange.upperBound);

        if (lowerCmp >= 0 && upperCmp <= 0) {
            return this;
        }

        if (lowerCmp <= 0 && upperCmp >= 0) {
            return connectedRange;
        }

        IntCut newLower = (lowerCmp >= 0) ? lowerBound : connectedRange.lowerBound;
        IntCut newUpper = (upperCmp <= 0) ? upperBound : connectedRange.upperBound;

        if (newLower.compareTo(newUpper) <= 0) {
            throw new IllegalArgumentException("intersection is undefined for disconnected ranges "
                    + this + " and " + connectedRange);
        }

        return create(newLower, newUpper);

    }

    public IntRange canonical() {
        IntCut lower = lowerBound.canonical();
        IntCut upper = upperBound.canonical();
        return (lower == lowerBound && upper == upperBound) ? this : create(lower, upper);
    }

}
