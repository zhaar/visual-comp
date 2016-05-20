package ch.epfl.visualComputing.Transformations;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


public class PixelTransformer<P, T> implements TransformationFunction<List<P>, List<T>> {

    private final Function<P, T> transfomer;

    public PixelTransformer(Function<P, T> t) {
        this.transfomer = t;
    }

    @Override
    public List<T> apply(List<P> source) {
        return source.stream().map(transfomer::apply).collect(Collectors.toList());
    }
}
