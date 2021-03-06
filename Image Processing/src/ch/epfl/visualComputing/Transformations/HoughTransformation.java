package ch.epfl.visualComputing.Transformations;

import ch.epfl.visualComputing.Transformations.CopeOut.DepressingJava;
import ch.epfl.visualComputing.Transformations.CopeOut.Pair;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.IntStream.range;

public class HoughTransformation implements Function<List<Float>, HoughTransformation.HoughAccumulator> {

    private final Float phiStep, rStep;
    private final Integer width, height;
    private final int phiDim;
    private final int rDim;
    private final float[] sinTable;
    private final float[] cosTable;

    public HoughTransformation(float phiStep, float rStep, int width, int height) {
        this.phiStep = phiStep;
        this.rStep = rStep;
        this.width = width;
        this.height = height;
        this.phiDim = (int) (Math.PI / phiStep);
        this.rDim = (int) (((width + height) * 2 + 1) / rStep);
        sinTable = new float[phiDim];
        cosTable = new float[phiDim];
        float a = 0;
        for (int accPhi = 0; accPhi < phiDim; accPhi++) {
            sinTable[accPhi] = (PApplet.sin(a) / rStep);
            cosTable[accPhi] = (PApplet.cos(a) / rStep);
            a += phiStep;
        }
    }

    private HoughAccumulator transform(List<Float> source) {

        HoughAccumulator acc = new HoughAccumulator(rDim, phiDim, rStep, phiStep);
        range(0, width).forEach(x -> range(0, height).filter(y -> source.get(y * width + x) != 0).forEach(y -> range(0, phiDim).forEach(angle -> {
            int radius = (int) (x * cosTable[angle] + y * sinTable[angle]);
            int rNormalized = radius + (rDim - 1) / 2;
            acc.accumulate(rNormalized, angle, 1);
        })));

        return acc;
    }

    @Override
    public HoughAccumulator apply(List<Float> source) {
        return this.transform(source);
    }


    public interface HoughAccInterface {

        int getRadius();
        int getAngle();
        List<Integer> getDataArray();
        Pair<Integer, Integer> convertIndex(int i);
        int get(int r, int phi);
        int getDefault(int r, int phi, int def);
        void set(int r, int phi, int value);
        void accumulate(int r, int phi, int delta);
        Pair<Float, Float> convertToActualValues(int idx);
    }
    //Accumulator of size radius x angle
    public static class HoughAccumulator implements HoughAccInterface {
        public final List<Integer> dataArray;
        public final int radius;
        public final int angle;
        public final float phiStep;
        public final float rStep;

        public int getRadius() { return radius; }
        public int getAngle() { return  angle; }
        public List<Integer> getDataArray() { return dataArray; }

        public HoughAccumulator(int radius, int angle, float rStep, float phiStep) {
            this.dataArray = new ArrayList<>(Collections.nCopies((radius + 2) * (angle + 2), 0));
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
            int idx = (phi + 1) * (radius + 2) + r + 1;
            dataArray.set(idx, dataArray.get(idx) + delta);
        }

        public Pair<Float, Float> convertToActualValues(int idx) {
            int accPhi = (idx / (radius + 2)) - 1;
            int accR = idx % (radius + 2);
            float r = (accR - (radius - 1) * 0.5f) * rStep;
            float phi = accPhi * phiStep;
            return new Pair<>(r, phi);
        }
    }
}
