package common.geo;

import java.util.Comparator;
import java.util.StringJoiner;

public record ImmutableCoordinate3D(int x, int y, int z) implements Coordinate3D, Comparable<ImmutableCoordinate3D> {

    public static ImmutableCoordinate3D of(int x, int y, int z) {
        return new ImmutableCoordinate3D(x, y, z);
    }

    public static ImmutableCoordinate3D atOrigin() {
        return of(0, 0, 0);
    }

    public static ImmutableCoordinate3D copyOf(Coordinate3D coordinate3D) {
        if (coordinate3D instanceof ImmutableCoordinate3D immutableCoordinate3D) {
            return immutableCoordinate3D;
        }
        return ImmutableCoordinate3D.of(coordinate3D.getX(), coordinate3D.getY(), coordinate3D.getZ());
    }

    public static ImmutableCoordinate3D nTimes(Coordinate3D coordinate, int times) {
        return ImmutableCoordinate3D.of(
                coordinate.getX() * times,
                coordinate.getY() * times,
                coordinate.getZ() * times
        );
    }

    public static ImmutableCoordinate3D nTimes(Direction3D direction, int times) {
        return nTimes(direction.getUnitVectorCoordinate(), times);
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
    public ImmutableCoordinate3D add(Coordinate3D other) {
        return of(getX() + other.getX(), getY() + other.getY(), getZ() + other.getZ());
    }

    @Override
    public ImmutableCoordinate3D add(Direction3D direction) {
        return add(direction.getUnitVectorCoordinate());
    }

    @Override
    public ImmutableCoordinate3D minus(Coordinate3D other) {
        return of(getX() - other.getX(), getY() - other.getY(), getZ() - other.getZ());
    }

    @Override
    public int compareTo(ImmutableCoordinate3D o) {
        return Comparator
                .comparingInt(ImmutableCoordinate3D::getX)
                .thenComparingInt(ImmutableCoordinate3D::getY)
                .thenComparingInt(ImmutableCoordinate3D::getZ)
                .compare(this, o);
    }

    public static ImmutableCoordinate3D min(ImmutableCoordinate3D c1, ImmutableCoordinate3D c2) {
        return c1.compareTo(c2) <= 0 ? c1 : c2;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "(", ")")
                .add("x=" + x)
                .add("y=" + y)
                .add("z=" + z)
                .toString();
    }
}
