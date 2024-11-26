package common.stats;

import common.geo.Cell2D;
import common.geo.Coordinate2D;
import common.geo.Coordinate3D;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

public final class Statistics {

    private Statistics() {
        throw new AssertionError("Cannot instantiate");
    }

    public static Cell2DMinMaxStatistics computeCellStatistics(Iterable<? extends Cell2D> cells) {
        Cell2DMinMaxStatistics statistics = new Cell2DMinMaxStatistics();
        for (Cell2D cell : cells) {
            statistics.accept(cell);
        }
        return statistics;
    }

    public static <T> Collector<T, Cell2DMinMaxStatistics, Cell2DMinMaxStatistics> computingCellStatistics(Function<? super T, Cell2D> mapper) {
        return Collector.of(
                Cell2DMinMaxStatistics::new,
                (r, t) -> r.accept(mapper.apply(t)),
                (l, r) -> {
                    l.combine(r);
                    return l;
                },
                Collector.Characteristics.IDENTITY_FINISH
        );
    }

    public static <T> Cell2DMinMaxStatistics computeCellStatistics(Collection<? extends T> elements, Function<? super T, Cell2D> mapper) {
        return elements.stream().collect(computingCellStatistics(mapper));
    }

    public static Coordinate2DMinMaxStatistics computeCoordinate2DStatistics(Iterable<? extends Coordinate2D> coordinates) {
        Coordinate2DMinMaxStatistics statistics = new Coordinate2DMinMaxStatistics();
        for (Coordinate2D coordinate : coordinates) {
            statistics.accept(coordinate);
        }
        return statistics;
    }

    public static <T>
    Collector<T, Coordinate2DMinMaxStatistics, Coordinate2DMinMaxStatistics>
    computingCoordinate2DStatistics(Function<? super T, Coordinate2D> mapper) {
        return Collector.of(
                Coordinate2DMinMaxStatistics::new,
                (r, t) -> r.accept(mapper.apply(t)),
                (l, r) -> {
                    l.combine(r);
                    return l;
                },
                Collector.Characteristics.IDENTITY_FINISH
        );
    }

    public static <T> Coordinate2DMinMaxStatistics computeCoordinate2DStatistics(Collection<? extends T> elements, Function<? super T, Coordinate2D> mapper) {
        return elements.stream().collect(computingCoordinate2DStatistics(mapper));
    }

    public static Coordinate3DMinMaxStatistics computeCoordinate3DStatistics(Iterable<? extends Coordinate3D> coordinates) {
        Coordinate3DMinMaxStatistics statistics = new Coordinate3DMinMaxStatistics();
        for (Coordinate3D coordinate : coordinates) {
            statistics.accept(coordinate);
        }
        return statistics;
    }

    public static <T> Collector<T, Coordinate3DMinMaxStatistics, Coordinate3DMinMaxStatistics> computingCoordinate3DStatistics(Function<? super T, Coordinate3D> mapper) {
        return Collector.of(
                Coordinate3DMinMaxStatistics::new,
                (r, t) -> r.accept(mapper.apply(t)),
                (l, r) -> {
                    l.combine(r);
                    return l;
                },
                Collector.Characteristics.IDENTITY_FINISH
        );
    }

    public static <T> Coordinate3DMinMaxStatistics computeCoordinate3DStatistics(Collection<? extends T> elements, Function<? super T, Coordinate3D> mapper) {
        return elements.stream().collect(computingCoordinate3DStatistics(mapper));
    }

    public static IntMinMaxStatistics computeIntStatistics(Iterable<Integer> ints) {
        IntMinMaxStatistics statistics = new IntMinMaxStatistics();
        for (int i : ints) {
            statistics.accept(i);
        }
        return statistics;
    }

    public static <T> Collector<T, IntMinMaxStatistics, IntMinMaxStatistics> computingIntStats(ToIntFunction<? super T> mapper) {
        return Collector.of(
                IntMinMaxStatistics::new,
                (r, t) -> r.accept(mapper.applyAsInt(t)),
                (l, r) -> {
                    l.combine(r);
                    return l;
                },
                Collector.Characteristics.IDENTITY_FINISH
        );
    }

    public static <T> IntMinMaxStatistics computeIntStatistics(Collection<? extends T> elements, ToIntFunction<? super T> mapper) {
        return elements.stream().collect(computingIntStats(mapper));
    }

    public static <K, V> IntMinMaxStatistics computeIntStatistics(Map<K, V> elements, ToIntFunction<? super Map.Entry<K, V>> mapper) {
        return elements.entrySet().stream().collect(computingIntStats(mapper));
    }

}
