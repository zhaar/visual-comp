package ch.epfl.visualComputing;

public final class Threshold {

    public static  ImageTransformation<Float> Binary(float threshold, float max) {
        return new ImageTransformation<>((pixel) -> pixel > threshold ? max : 0);
    }

    public static ImageTransformation<Float> InvertBinary(float threshold, float max) {
        return new ImageTransformation<>((px) -> px < threshold ? 0 : max);
    }
}
