package common.geo;

import java.util.Objects;

public class MutableCell2D implements Cell2D {

    private int row;
    private int column;

    public MutableCell2D(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public static MutableCell2D of(int row, int column) {
        return new MutableCell2D(row, column);
    }

    public static MutableCell2D copyOf(Cell2D cell2D) {
        return new MutableCell2D(cell2D.getRow(), cell2D.getColumn());
    }

    @Override
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    @Override
    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableCell2D that = (MutableCell2D) o;
        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public Cell2D add(Cell2D other) {
        return of(getRow() + other.getRow(), getColumn() + other.getColumn());
    }

    public void move(Cell2D movement) {
        move(movement.getRow(), movement.getColumn());
    }

    public void move(Direction2D movement) {
        move(movement.getUnitVectorCell());
    }

    private void move(int deltaRow, int deltaColumn) {
        this.row += deltaRow;
        this.column += deltaColumn;
    }


}

