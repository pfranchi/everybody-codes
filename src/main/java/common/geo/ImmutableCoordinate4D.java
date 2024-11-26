package common.geo;

import java.util.Comparator;
import java.util.StringJoiner;

public record ImmutableCoordinate4D(int x, int y, int z, int w)
        implements Coordinate4D, Comparable<ImmutableCoordinate4D> {

    public static ImmutableCoordinate4D of(int x, int y, int z, int w) {
        return new ImmutableCoordinate4D(x, y, z, w);
    }

    public static ImmutableCoordinate4D atOrigin() {
        return of(0, 0, 0, 0);
    }

    public static ImmutableCoordinate4D copyOf(Coordinate4D coordinate4D) {
        if (coordinate4D instanceof ImmutableCoordinate4D immutableCoordinate4D) {
            return immutableCoordinate4D;
        }
        return ImmutableCoordinate4D.of(coordinate4D.getX(), coordinate4D.getY(),
                coordinate4D.getZ(), coordinate4D.getW());
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
    public int getZ() {
        return z();
    }

    @Override
    public int getW() {
        return w();
    }

    @Override
    public Coordinate4D add(Coordinate4D other) {
        return of(getX() + other.getX(), getY() + other.getY(),
                getZ() + other.getZ(), getW() + other.getW());
    }

    @Override
    public Coordinate4D minus(Coordinate4D other) {
        return of(getX() - other.getX(), getY() - other.getY(),
                getZ() - other.getZ(), getW() - other.getW());
    }

    @Override
    public int compareTo(ImmutableCoordinate4D o) {
        return Comparator
                .comparingInt(ImmutableCoordinate4D::getX)
                .thenComparingInt(ImmutableCoordinate4D::getY)
                .thenComparingInt(ImmutableCoordinate4D::getZ)
                .thenComparingInt(ImmutableCoordinate4D::getW)
                .compare(this, o);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "(", ")")
                .add("x=" + x)
                .add("y=" + y)
                .add("z=" + z)
                .add("w=" + w)
                .toString();
    }

}
