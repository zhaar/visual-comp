package ch.epfl.visualComputing.Transformations;


import ch.epfl.visualComputing.Transformations.CopeOut.Pair;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface TransformationFunction<A, B> extends Function<A, B> {

    default <C> Function<A, Pair<B, C>> merge(Function<A, C> that) {
        Objects.requireNonNull(that);
        return (A a) -> new Pair<>(this.apply(a), that.apply(a));
    }

    default <C, D> Function<A, D> mergeWith(Function<A, C> that, BiFunction<B, C, D> fn) {
        Objects.requireNonNull(that);
        Objects.requireNonNull(fn);
        return merge(that).andThen((Pair<B, C> p) -> fn.apply(p._1(), p._2()));
    }

    default <C> Function<A, C> $(Function<B, C> a) {
        return andThen(a);
    }
}
