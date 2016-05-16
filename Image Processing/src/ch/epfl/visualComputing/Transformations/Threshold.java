package ch.epfl.visualComputing.Transformations;

import java.util.function.Predicate;

public final class Threshold {

    private Threshold() {}

    public static PixelTransformer<Float, Float> genericBinary(Predicate<Float> p, float max, float min) {
        return new PixelTransformer<>(px -> p.test(px) ? max : min);
    }

    public static PixelTransformer<Float, Float> Binary(float threshold, float max) {
        return new PixelTransformer<>((pixel) -> pixel > threshold ? max : 0);
    }

    public static PixelTransformer<Float, Float> InvertBinary(float threshold, float max) {
        return new PixelTransformer<>((px) -> px < threshold ? 0 : max);
    }
}
