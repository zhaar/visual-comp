package ch.epfl.visualComputing.Transformations.CopeOut;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MyList {

    private MyList() {
    }

    public static <S, T> List<Pair<S, T>> zip(List<S> xs, List<T> ys) {
        int length = Math.min(xs.size(), ys.size());
        return IntStream.range(0, length).mapToObj(i -> new Pair<>(xs.get(i), ys.get(i))).collect(Collectors.toList());
    }

    public static <T> List<Pair<T, Integer>> zipWithIndex(List<T> xs) {
        return zip(xs, IntStream.range(0, xs.size()).boxed().collect(Collectors.toList()));
    }

    public static <T> List<T> take(List<T> xs, int n) {
        List<T> ys = new ArrayList<>(n);
        for (int i = 0; i < Math.min(n, xs.size()); i++) ys.add(xs.get(i));
        return ys;
    }
}
