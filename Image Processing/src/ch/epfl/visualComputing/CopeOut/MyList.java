package ch.epfl.visualComputing.CopeOut;

import ch.epfl.visualComputing.CopeOut.Pair;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MyList {

    private MyList() {}

    public static <S, T> List<Pair<S, T>> zip(List<S> xs, List<T> ys) {
        return IntStream.range(0, Math.min(xs.size(), ys.size())).mapToObj(i -> new Pair<>(xs.get(i), ys.get(i))).collect(Collectors.toList());
    }

    public static <T> List<Pair<T, Integer>> zipWithIndex(List<T> xs) {
        return zip(xs, IntStream.range(0, xs.size()).boxed().collect(Collectors.toList()));
    }
}
