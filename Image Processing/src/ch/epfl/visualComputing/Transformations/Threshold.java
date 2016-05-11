package ch.epfl.visualComputing.Transformations;

import ch.epfl.visualComputing.ImageTransformation;

public final class Threshold {

    private Threshold() {}

    public static ImageTransformation<Float, Float> Binary(float threshold, float max) {
        return new ImageTransformation<>((pixel) -> pixel > threshold ? max : 0);
    }

    public static ImageTransformation<Float, Float> InvertBinary(float threshold, float max) {
        return new ImageTransformation<>((px) -> px < threshold ? 0 : max);
    }
}
