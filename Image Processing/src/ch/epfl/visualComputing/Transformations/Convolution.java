package ch.epfl.visualComputing.Transformations;

import ch.epfl.visualComputing.DepressingJava;
import ch.epfl.visualComputing.ImageTransformation;

import java.util.stream.IntStream;

public class Convolution {

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
        System.out.println("weight: " + w);
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