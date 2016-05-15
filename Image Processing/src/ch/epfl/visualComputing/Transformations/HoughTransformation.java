package ch.epfl.visualComputing.Transformations;

import ch.epfl.visualComputing.CopeOut.DepressingJava;
import ch.epfl.visualComputing.CopeOut.Triple;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.IntStream.range;

public class HoughTransformation implements Function<List<Float>, HoughTransformation.HoughAccumulator> {

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

    private HoughAccumulator transform(List<Float> source) {

        float[] sinTable = new float[phiDim];
        float[] cosTable = new float[phiDim];
        for (int angle = 0; angle < phiDim; ++angle) {
            sinTable[angle] = (float) Math.sin(PConstants.PI * angle / phiDim);
            cosTable[angle] = (float) Math.cos(PConstants.PI * angle / phiDim);
        }

        HoughAccumulator acc = new HoughAccumulator(rDim, phiDim);
        range(0, height).boxed().forEach(y -> range(0, width).forEach(x -> {
            if (DepressingJava.get2D(x, y, source, width, height, 0f) != 0) {
                range(0, phiDim).forEach(phi -> {
                    float r = x * cosTable[phi] + y * sinTable[phi];
                    int normalized = Math.round((r + acc.radius) / 2);
                    acc.accumulate(normalized, phi, 1);
                });
            }
        }));
        return acc;
    }

    @Override
    public HoughAccumulator apply(List<Float> source) {
        return this.transform(source);
    }

    public static class HoughAccumulator {
        public final List<Integer> dataArray;
        public final int radius;
        public final int angle;

        public HoughAccumulator(int radius, int angle) {
            this.dataArray = new ArrayList<>(Collections.nCopies(radius * angle, 0));
            this.radius = radius;
            this.angle = angle;
        }

//        private HoughAccumulator(int[] dataArray, int radius, int angle) {
//            this.dataArray = dataArray;
//            this.radius = radius;
//            this.angle = angle;
//        }

        public int get(int r, int phi) {
            return dataArray.get(phi * radius + r);
        }

        public void set(int r, int phi, int value) {
            dataArray.set(phi * radius + r, value);
        }

        public void accumulate(int r, int phi, int delta) {
            int index = phi * radius + r;
            dataArray.set(index, dataArray.get(index) + delta);
        }
    }
}
