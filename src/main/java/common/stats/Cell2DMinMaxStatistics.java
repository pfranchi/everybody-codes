package common.stats;

import common.geo.Cell2D;

import java.util.function.Consumer;

public class Cell2DMinMaxStatistics implements Consumer<Cell2D> {

    private final IntMinMaxStatistics rowStatistics;
    private final IntMinMaxStatistics columnStatistics;

    public Cell2DMinMaxStatistics() {
        this.rowStatistics = new IntMinMaxStatistics();
        this.columnStatistics = new IntMinMaxStatistics();
    }

    @Override
    public void accept(Cell2D cell2D) {
        this.rowStatistics.accept(cell2D.getRow());
        this.columnStatistics.accept(cell2D.getColumn());
    }

    public void combine(Cell2DMinMaxStatistics other) {
        this.rowStatistics.combine(other.getRowStatistics());
        this.columnStatistics.combine(other.getColumnStatistics());
    }

    public IntMinMaxStatistics getRowStatistics() {
        return rowStatistics;
    }

    public IntMinMaxStatistics getColumnStatistics() {
        return columnStatistics;
    }
}
