package ch.epfl.visualComputing;

import ch.epfl.visualComputing.Transformations.Convolution;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ImageTransformation<Pixel, Transformed> {

    @FunctionalInterface
    public interface PixelTransformation<T, S> {
        S apply(T pixel);
        default GenericTransformation<T, S> toGeneric() {
            return (index, pixel, image) -> PixelTransformation.this.apply(pixel);
        }
    }

    @FunctionalInterface
    public interface GenericTransformation<T, S> {
        S apply(int index, T pixel, List<T> image);
    }

    private final GenericTransformation<Pixel, Transformed> transform;

    public ImageTransformation(PixelTransformation<Pixel, Transformed> px) {
        this.transform = px.toGeneric();
    }

    public ImageTransformation(GenericTransformation<Pixel, Transformed> trs) {
        this.transform = trs;
    }


    public static ImageTransformation<Float, Float> convolutionTransformation(int[][] matrix, int size, int w, int h) {
        return new ImageTransformation<>(new Convolution(matrix, size, 1).toGeneric(w, h));
    }

//    public <T> ImageTransformation<Pixel, T>composeWith(ImageTransformation<Transformed, T> ts) {
//        return new ImageTransformation<Pixel, T>((i, p, ls) -> );
//    }

    public <T> ImageTransformation<Pixel, T> mergeTransforms(ImageTransformation<Pixel, Transformed> that, BiFunction<Transformed, Transformed, T> merger) {
        return new ImageTransformation<Pixel, T>((i, p, ls) -> merger.apply(this.transform.apply(i, p, ls), that.transform.apply(i, p, ls)));
    }

    public List<Transformed> transform(List<Pixel> source) {
        return IntStream.range(0, source.size()).parallel()
                .mapToObj((i) -> transform.apply(i, source.get(i), source))
                .collect(Collectors.toList());
    }

}
