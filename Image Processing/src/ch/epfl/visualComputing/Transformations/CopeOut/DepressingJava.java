package ch.epfl.visualComputing.Transformations.CopeOut;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class DepressingJava {

    public static List<Integer> toIntList(int[] arr) {
        List<Integer> intList = new ArrayList<Integer>(arr.length);
        for (int elem : arr) {
            intList.add(elem);
        }
        return intList;
    }

    public static <T> T getOrDefault(int i, List<T> ls, T v) {
        return (i < 0 || i >= ls.size()) ? v : ls.get(i);
    }

    public static <T> T get2D(int x, int y, List<T> ls, int w, int h, T d) {
        return (x < 0 || x >= w || y < 0 || y >= h) ? d : ls.get(y * w + x);
    }

    public static Function<List<Pair<Float, Float>>, List<PVector>> toVectors() {
        return ls -> ls.stream().map(p -> new PVector(p._1(), p._2())).collect(Collectors.toList());
    }
}
