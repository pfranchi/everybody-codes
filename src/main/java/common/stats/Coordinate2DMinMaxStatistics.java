package common.stats;

import common.geo.Coordinate2D;

import java.util.function.Consumer;

public class Coordinate2DMinMaxStatistics implements Consumer<Coordinate2D> {

    private final IntMinMaxStatistics xStatistics;
    private final IntMinMaxStatistics yStatistics;

    public Coordinate2DMinMaxStatistics() {
        this.xStatistics = new IntMinMaxStatistics();
        this.yStatistics = new IntMinMaxStatistics();
    }

    @Override
    public void accept(Coordinate2D coordinate2D) {
        this.accept(coordinate2D.getX(), coordinate2D.getY());
    }

    public void accept(int x, int y) {
        this.xStatistics.accept(x);
        this.yStatistics.accept(y);
    }

    public void combine(Coordinate2DMinMaxStatistics other) {
        this.xStatistics.combine(other.getxStatistics());
        this.yStatistics.combine(other.getyStatistics());
    }

    public IntMinMaxStatistics getxStatistics() {
        return xStatistics;
    }

    public IntMinMaxStatistics getyStatistics() {
        return yStatistics;
    }
}
