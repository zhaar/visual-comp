package ch.epfl.visualComputing.Transformations;

import java.util.function.Predicate;

public final class Threshold {

    private Threshold() {}

    //If the predicate is satisfied the maximum value is returned, otherwise, the minimum value is returned
    public static <T, S> PixelTransformer<S, T> genericBinary(Predicate<S> p, T max, T min) {
        return new PixelTransformer<>(px -> p.test(px) ? max : min);
    }

    //If the predicate is satified, the value passes through. Otherwise it become the default value
    public static <T> PixelTransformer<T, T> passThrough(Predicate<T> p, T val) {
        return new PixelTransformer<>(px -> p.test(px) ? px : val);
    }

    public static <T> PixelTransformer<Float, T> Binary(float threshold, T max, T min) {
        return new PixelTransformer<>((pixel) -> pixel > threshold ? max : min);
    }

    public static <T> PixelTransformer<Float, T> InvertBinary(float threshold, T max, T min) {
        return new PixelTransformer<>((px) -> px < threshold ? min : max);
    }
}
