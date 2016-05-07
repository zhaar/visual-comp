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
//
//    @FunctionalInterface
//    public static interface MatrixTransformation<T> {
//        public T apply(int[] matrix);
//        default GenericTransformation<T> toGeneric() {
//            return (index, pixel, image) -> {
//
//            }
//        }
//    }

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
