package ch.epfl.visualComputing.Transformations;

import ch.epfl.visualComputing.CopeOut.DepressingJava;
import ch.epfl.visualComputing.CopeOut.Triple;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.IntStream.range;

public class HoughTransformation implements Function<List<Float>, Triple<List<Integer>, Integer, Integer>> {

    private final Float phi, r;
    private final Integer width, height;
    private final int phiDim;
    private final int rDim;

    public HoughTransformation(float phiStep, float rStep, int width, int height) {
        this.phi = phiStep;
        this.r = rStep;
        this.width = width;
        this.height = height;
        this.phiDim = (int) (Math.PI / phi);
        this.rDim = (int) (((width + height) * 2 + 1) / r);
    }

    private List<Integer> transform(List<Float> source) {

        float[] sinTable = new float[phiDim];
        float[] cosTable = new float[phiDim];
        for (int angle = 0; angle < phiDim; ++angle) {
            sinTable[angle] = (float) Math.sin(PConstants.PI * angle / phiDim);
            cosTable[angle] = (float) Math.cos(PConstants.PI * angle / phiDim);
        }

        ArrayData acc = new ArrayData(rDim + 2, phiDim + 2);
        List<Integer> accumulator = new ArrayList<>((phiDim) * (rDim));
        range(0, height).boxed().forEach(y -> range(0, width).forEach(x -> {
            if (DepressingJava.get2D(x, y, source, width, height, 0f) != 0) {
                range(0, phiDim).forEach(phi -> {
                    float r = x * cosTable[phi] + y * sinTable[phi];
                    int normalized = Math.round((r + acc.radius) / 2);
                    acc.accumulate(normalized, phi, 1);

                });
            }
        }));
        return DepressingJava.toIntList(acc.dataArray);
    }

    @Override
    public Triple<List<Integer>, Integer, Integer> apply(List<Float> source) {
        List<Integer> transformed = this.transform(source);
        return new Triple<>(transformed, phiDim, rDim);
    }

    public static class ArrayData {
        public final int[] dataArray;
        public final int radius;
        public final int angle;

        public ArrayData(int radius, int angle) {
            this(new int[radius * angle], radius, angle);
        }

        private ArrayData(int[] dataArray, int radius, int angle) {
            this.dataArray = dataArray;
            this.radius = radius;
            this.angle = angle;
        }

        public int get(int r, int phi) {
            return dataArray[phi * radius + r];
        }

        public void set(int r, int phi, int value) {
            dataArray[phi * radius + r] = value;
        }

        public void accumulate(int r, int phi, int delta) {
            dataArray[phi * radius + r] += delta;
        }
    }
}
