package ch.epfl.visualComputing.Transformations;

public final class Threshold {

    private Threshold() {}

    public static PixelTransformer<Float, Float> Binary(float threshold, float max) {
        return new PixelTransformer<>((pixel) -> pixel > threshold ? max : 0);
    }

    public static PixelTransformer<Float, Float> InvertBinary(float threshold, float max) {
        return new PixelTransformer<>((px) -> px < threshold ? 0 : max);
    }
}
