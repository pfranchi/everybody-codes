package common.stats;

import common.geo.Coordinate3D;

import java.util.function.Consumer;

public class Coordinate3DMinMaxStatistics implements Consumer<Coordinate3D> {

    private final IntMinMaxStatistics xStatistics;
    private final IntMinMaxStatistics yStatistics;
    private final IntMinMaxStatistics zStatistics;

    public Coordinate3DMinMaxStatistics() {
        this.xStatistics = new IntMinMaxStatistics();
        this.yStatistics = new IntMinMaxStatistics();
        this.zStatistics = new IntMinMaxStatistics();
    }

    public void accept(int x, int y, int z) {
        this.xStatistics.accept(x);
        this.yStatistics.accept(y);
        this.zStatistics.accept(z);
    }

    @Override
    public void accept(Coordinate3D coordinate3D) {
        accept(coordinate3D.getX(), coordinate3D.getY(), coordinate3D.getZ());
    }

    public void combine(Coordinate3DMinMaxStatistics other) {
        this.xStatistics.combine(other.getxStatistics());
        this.yStatistics.combine(other.getyStatistics());
        this.zStatistics.combine(other.getzStatistics());
    }

    public IntMinMaxStatistics getxStatistics() {
        return xStatistics;
    }

    public IntMinMaxStatistics getyStatistics() {
        return yStatistics;
    }

    public IntMinMaxStatistics getzStatistics() {
        return zStatistics;
    }
}
