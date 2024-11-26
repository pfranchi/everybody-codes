package common.geo;

import java.util.Comparator;
import java.util.StringJoiner;

public record ImmutableCoordinate2D(int x, int y) implements Coordinate2D, Comparable<ImmutableCoordinate2D> {

    private static final ImmutableCoordinate2D origin = ImmutableCoordinate2D.of(0, 0);

    public static ImmutableCoordinate2D of(int x, int y) {
        return new ImmutableCoordinate2D(x, y);
    }

    public static ImmutableCoordinate2D atOrigin() {
        return origin;
    }

    public static ImmutableCoordinate2D copyOf(Coordinate2D coordinate2D) {
        if (coordinate2D instanceof ImmutableCoordinate2D immutableCoordinate2D) {
            return immutableCoordinate2D;
        }
        return new ImmutableCoordinate2D(coordinate2D.getX(), coordinate2D.getY());
    }

    public static ImmutableCoordinate2D nTimes(Coordinate2D coordinate, int times) {
        return ImmutableCoordinate2D.of(coordinate.getX() * times, coordinate.getY() * times);
    }

    public static ImmutableCoordinate2D nTimes(Direction2D direction, int times) {
        return ImmutableCoordinate2D.nTimes(direction.getUnitVectorCoordinate(), times);
    }

    @Override
    public int getX() {
        return x();
    }

    @Override
    public int getY() {
        return y();
    }

    @Override
    public ImmutableCoordinate2D add(Coordinate2D other) {
        return ImmutableCoordinate2D.of(getX() + other.getX(), getY() + other.getY());
    }

    @Override
    public int compareTo(ImmutableCoordinate2D o) {

        Comparator<ImmutableCoordinate2D> yComp = Comparator.comparingInt(ImmutableCoordinate2D::getY).reversed();
        Comparator<ImmutableCoordinate2D> xComp = Comparator.comparingInt(ImmutableCoordinate2D::getX);

        return yComp.thenComparing(xComp).compare(this, o);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "(", ")")
                .add("x=" + x)
                .add("y=" + y)
                .toString();
    }
}
