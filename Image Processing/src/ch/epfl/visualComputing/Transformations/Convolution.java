package ch.epfl.visualComputing.Transformations;

import ch.epfl.visualComputing.Transformations.CopeOut.DepressingJava;
import ch.epfl.visualComputing.Transformations.CopeOut.MyList;
import ch.epfl.visualComputing.Transformations.CopeOut.Pair;
import processing.core.PApplet;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Convolution {

    private final int size;
    private final int[][] kernel;
    private final float weight;

    public Convolution(int[][] kernel, int mSize) {
        int w = IntStream.range(0, mSize)
                .map(i -> IntStream.range(0, mSize)
                        .reduce(0, (a, j) -> a + kernel[i][j]))
                .reduce(0, (l, r) -> l + r);
        this.kernel = kernel;
        this.size = mSize;
        this.weight = w;
    }

    public Convolution(int[][] kernel, int mSize, int weight) {
        this.kernel = kernel;
        this.size = mSize;
        this.weight = weight;
    }

    public ImageTransformation.GenericTransformation<Float, Float> toGeneric(int imageWidth, int imageHeight) {
        return (index, pixel, image) -> {
            int x = index % imageWidth;
            int y = index / imageWidth;

            int sum = 0;

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int ix = x + i - size / 2;
                    int iy = y + j - size / 2;
                    float arrayValue = DepressingJava.get2D(ix, iy, image, imageWidth, imageHeight, 0f);
                    float prod = kernel[i][j] * arrayValue;
                    sum += prod;
                }
            }
            return sum / weight;
        };
    }

    private static final int[][] defaultGaussianMatrix = {{9, 12, 9}, {12, 15, 12}, {9, 12, 9}};

    public static ImageTransformation<Float, Float> gaussianBlur(int width, int height) {
        return new ImageTransformation<>(new Convolution(defaultGaussianMatrix, 3, 99).toGeneric(width, height));
    }

    public static Function<List<Float>, List<Float>> sobelDoubleConvolution(int width, int height) {

        int[][] hSobel = {{0, 1, 0}, {0, 0, 0}, {0, -1, 0}};
        int[][] vSobel = {{0, 0, 0}, {1, 0, -1}, {0, 0, 0}};
        ImageTransformation<Float, Float> vertical = ImageTransformation.convolutionTransformation(vSobel, 3, width, height);
        ImageTransformation<Float, Float> horizontal = ImageTransformation.convolutionTransformation(hSobel, 3, width, height);
        return vertical.mergeWith(horizontal, MyList::zip)
                .andThen(ls -> ls.parallelStream().map(Convolution::distance).collect(Collectors.toList()));
    }

    private static float distance(Pair<Float, Float> p) {
        return PApplet.sqrt(PApplet.pow(p._1(), 2) + PApplet.pow(p._2(), 2));
    }


}