package ch.epfl.visualComputing;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ImageTransformation<Pixel> {

    @FunctionalInterface
    public interface PixelTransformation<T> {
        T apply(T pixel);
        default GenericTransformation<T> toGeneric() {
            return (index, pixel, image) -> PixelTransformation.this.apply(pixel);
        }
    }

    public static class Convolution {

        private final int size;
        private final int[][] kernel;
        private final float weight;

        public Convolution(int[][] kernel, int mSize) {
            int w = IntStream.range(0,mSize)
                    .map(i -> IntStream.range(0, mSize)
                            .reduce(0, (a, j) -> a + kernel[i][j]))
                    .reduce(0, (l, r) -> l + r);
            this.kernel = kernel;
            this.size = mSize;
            this.weight = w;
        }

        public GenericTransformation<Float> toGeneric(int imageWidth, int imageHeight) {
            return (index, pixel, image) -> {
                int x = index % imageWidth;
                int y = index / imageWidth;

                int sum = 0;

                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        int ix = x + i - size/2;
                        int iy = y + j - size/2;
                        float arrayValue = DepressingJava.get2D(ix, iy, image, imageWidth, imageHeight, 0f);
                        float prod = kernel[i][j] * arrayValue;
                        sum += prod;
                    }
                }
                return sum/weight;
            };
        }
    }

    @FunctionalInterface
    public interface GenericTransformation<T> {
        T apply(int index, T pixel, List<T> image);
    }

    private final GenericTransformation<Pixel> transform;

    public ImageTransformation(PixelTransformation<Pixel> px) {
        this.transform = px.toGeneric();
    }

    public ImageTransformation(GenericTransformation<Pixel> trs) {
        this.transform = trs;
    }

    public List<Pixel> transform(List<Pixel> source) {
        return IntStream.range(0, source.size())
                .mapToObj((i) -> transform.apply(i, source.get(i), source))
                .collect(Collectors.toList());
    }
}
