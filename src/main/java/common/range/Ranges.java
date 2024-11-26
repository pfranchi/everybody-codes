package common.range;

public final class Ranges {

    private Ranges() {}

    static String toString(IntCut lowerBound, IntCut upperBound) {
        StringBuilder sb = new StringBuilder(16);
        lowerBound.describeAsLowerBound(sb);
        sb.append("..");
        upperBound.describeAsUpperBound(sb);
        return sb.toString();
    }

}
