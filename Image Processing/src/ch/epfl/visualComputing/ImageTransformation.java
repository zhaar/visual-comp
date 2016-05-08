package ch.epfl.visualComputing;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ImageTransformation<Pixel, Target> {
    @FunctionalInterface
    public interface PixelTransformation<T> {
        T apply(T pixel);
        default GenericTransformation<T> toGeneric() {
            return (index, pixel, image) -> PixelTransformation.this.apply(pixel);
        }
    }

    public class Convolution<T> {

        private final int size;
        private final int[][] kernel;
        private final float weight;

        public Convolution(int[][] kernel, int mSize, float weight) {
            this.kernel = kernel;
            this.size = mSize;
            this.weight = weight;
        }

        public Convolution(int[][] kernel, int mSize) {
            this(kernel, mSize, 1f);
        }

        public GenericTransformation<Integer> toGeneric(int imageWidth, int imageHeight) {
            return (index, pixel, image) -> {
                int x = index % imageWidth;
                int y = index / imageWidth;
                for (int ix = x - size/2; ix < x + size/2; ix++) {
                    for (int iy = y - size/2; iy < y + size/2; iy++) {
                        
                    }
                }
                return index;
            }
        }
    }

    @FunctionalInterface
    public interface GenericTransformation<T> {
        T apply(int index, T pixel, List<T> image);
    }

//    public static int get(int x, int y, List<Integer> image, int width, int height) {
//        if (x < 0 || x > width) {
//            return 0;
//        } else if (y < 0 || y > height) {
//            return 0;
//        } else {
//            return image.get()
//        }
//    }

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
