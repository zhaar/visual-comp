package ch.epfl.visualComputing.Transformations;

import ch.epfl.visualComputing.CopeOut.DepressingJava;
import ch.epfl.visualComputing.CopeOut.Pair;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.IntStream.range;

public class HoughTransformation implements Function<List<Float>, HoughTransformation.HoughData> {

    private final Float phi, r;
    private final Integer width, height;
    private final int phiDim;
    private final int rDim;
    private final float[] sinTable;
    private final float[] cosTable;

    public HoughTransformation(float phiStep, float rStep, int width, int height) {
        this.phi = phiStep;
        this.r = rStep;
        this.width = width;
        this.height = height;
        this.phiDim = (int) (Math.PI / phi) + 2;
        this.rDim = (int) (((width + height) * 2) + 1 / r) + 2;
        sinTable = new float[phiDim];
        cosTable = new float[phiDim];
        for (int angle = 0; angle < phiDim; ++angle) {
            sinTable[angle] = (float) Math.sin(PConstants.PI * angle / phiDim);
            cosTable[angle] = (float) Math.cos(PConstants.PI * angle / phiDim);
        }
    }

    private HoughData transform(List<Float> source) {

        List<Integer> accumulator = new ArrayList<>(Collections.nCopies(rDim * phiDim, 0));

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (source.get(y * width + x) != 0) {
                    for (int angle = 0; angle < phiDim; angle++) {
                        int radius = (int) (x * cosTable[angle] + y * sinTable[angle]);
                        int normalized = radius + rDim / 2;
                        int idx = angle * rDim + normalized;
                        accumulator.set(idx,  accumulator.get(idx) + 1);
                    }
                }
            }
        }
        return new HoughData(accumulator, rDim, phiDim);
    }

    @Override
    public HoughData apply(List<Float> source) {
        return this.transform(source);
    }

    public static class HoughData {

        public final int phiDim;
        public final int rDim;
        public final List<Integer> acc;

        public HoughData(List<Integer> ls, int rDim, int phiDim) {
            this.acc = ls;
            this.rDim = rDim;
            this.phiDim = phiDim;
        }
    }

    //Accumulator of size radius x angle
    public static class HoughAccumulator {
        public final List<Integer> dataArray;
        public final int radius;
        public final int angle;
        public final float phiStep;
        public final float rStep;

        public HoughAccumulator(int radius, int angle, float phiStep, float rStep) {
            this.dataArray = new ArrayList<>(Collections.nCopies(radius * angle, 0));
            this.radius = radius;
            this.angle = angle;
            this.phiStep = phiStep;
            this.rStep = rStep;
        }

        //(radius, angle)
        public Pair<Integer, Integer> convertIndex(int i) {
            return new Pair<>(i % radius, i / radius);
        }

        public int get(int r, int phi) {
            return dataArray.get(phi * radius + r);
        }

        public int getDefault(int r, int phi, int def) {
            return DepressingJava.get2D(r, phi, dataArray, radius, angle, def);
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
