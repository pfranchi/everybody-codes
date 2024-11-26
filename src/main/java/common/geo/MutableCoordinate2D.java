package common.geo;

import java.util.Objects;
import java.util.StringJoiner;

public class MutableCoordinate2D implements Coordinate2D {

    private int x;
    private int y;

    private MutableCoordinate2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static MutableCoordinate2D of(int x, int y) {
        return new MutableCoordinate2D(x, y);
    }

    public static MutableCoordinate2D atOrigin() {
        return of(0, 0);
    }

    public static MutableCoordinate2D copyOf(Coordinate2D coordinate2D) {
        return of(coordinate2D.getX(), coordinate2D.getY());
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableCoordinate2D that = (MutableCoordinate2D) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MutableCoordinate2D.class.getSimpleName() + "[", "]")
                .add("x=" + x)
                .add("y=" + y)
                .toString();
    }

    @Override
    public Coordinate2D add(Coordinate2D other) {
        return MutableCoordinate2D.of(getX() + other.getX(), getY() + other.getY());
    }

    public void move(Coordinate2D movement) {
        move(movement.getX(), movement.getY());
    }

    public void move(Direction2D movement) {
        move(movement.getUnitVectorCoordinate());
    }

    public void move(Direction2D movement, int steps) {
        Coordinate2D unitVectorCoordinate = movement.getUnitVectorCoordinate();
        move(unitVectorCoordinate.getX() * steps, unitVectorCoordinate.getY() * steps);
    }

    private void move(int deltaX, int deltaY) {
        this.x += deltaX;
        this.y += deltaY;
    }

    public void rotateRight() {
        /*
            |  0  1 |  *  | x |  =  |  y |
            | -1  0 |     | y |     | -x |
         */
        @SuppressWarnings("SuspiciousNameCombination") int newX = y;
        int newY = -x;
        this.x = newX;
        this.y = newY;
    }

    public void rotateLeft() {
        /*
            |  0 -1 |  *  | x |  =  | -y |
            |  1  0 |     | y |     |  x |
         */
        int newX = -y;
        @SuppressWarnings("SuspiciousNameCombination") int newY = x;
        this.x = newX;
        this.y = newY;
    }

}
