package common.geo;

import java.util.Comparator;
import java.util.StringJoiner;

public record ImmutableCell2D(int row, int column) implements Cell2D, Comparable<ImmutableCell2D> {

    public static ImmutableCell2D of(int row, int column) {
        return new ImmutableCell2D(row, column);
    }

    public static ImmutableCell2D copyOf(Cell2D cell2D) {
        if (cell2D instanceof ImmutableCell2D immutableCell2D) {
            return immutableCell2D;
        }
        return of(cell2D.getRow(), cell2D.getColumn());
    }

    public static ImmutableCell2D nTimes(Cell2D coordinate, int times) {
        return ImmutableCell2D.of(coordinate.getRow() * times, coordinate.getColumn() * times);
    }

    public static ImmutableCell2D nTimes(Direction2D direction, int times) {
        return nTimes(direction.getUnitVectorCell(), times);
    }

    @Override
    public int getRow() {
        return row();
    }

    @Override
    public int getColumn() {
        return column();
    }

    @Override
    public ImmutableCell2D add(Cell2D other) {
        return ImmutableCell2D.of(this.getRow() + other.getRow(), this.getColumn() + other.getColumn());
    }

    @Override
    public ImmutableCell2D add(Direction2D other) {
        return add(other.getUnitVectorCell());
    }

    @Override
    public int compareTo(ImmutableCell2D o) {
        return Comparator.comparingInt(ImmutableCell2D::getRow)
                .thenComparingInt(ImmutableCell2D::getColumn)
                .compare(this, o);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ",  "(", ")")
                .add("r=" + row)
                .add("c=" + column)
                .toString();
    }
}
