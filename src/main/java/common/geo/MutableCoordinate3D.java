package common.geo;

import java.util.Objects;

public class MutableCoordinate3D implements Coordinate3D {

    private int x;
    private int y;
    private int z;

    private MutableCoordinate3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static MutableCoordinate3D of(int x, int y, int z) {
        return new MutableCoordinate3D(x, y, z);
    }

    public static MutableCoordinate3D atOrigin() {
        return of(0, 0, 0);
    }

    public static MutableCoordinate3D copyOf(Coordinate3D coordinate3D) {
        return of(coordinate3D.getX(), coordinate3D.getY(), coordinate3D.getZ());
    }

    @Override
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableCoordinate3D that = (MutableCoordinate3D) o;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public Coordinate3D add(Coordinate3D other) {
        return of(this.getX() + other.getX(), this.getY() + other.getY(), this.getZ() + other.getZ());
    }

    @Override
    public Coordinate3D minus(Coordinate3D other) {
        return of(getX() - other.getX(), getY() - other.getY(), getZ() - other.getZ());
    }

    public void move(Coordinate3D movement) {
        move(movement.getX(), movement.getY(), movement.getZ());
    }

    public void move(Direction3D movement) {
        move(movement.getUnitVectorCoordinate());
    }

    private void move(int deltaX, int deltaY, int deltaZ) {
        this.x += deltaX;
        this.y += deltaY;
        this.z += deltaZ;
    }

}
