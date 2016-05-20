
import ch.epfl.visualComputing.Transformations.CopeOut.DepressingJava;
import ch.epfl.visualComputing.Transformations.CopeOut.Pair;
import ch.epfl.visualComputing.Transformations.HoughTransformation.HoughAccInterface;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class HoughTransformation_ implements Function<List<Float>, HoughAccumulator_> {

    private final Float phiStep, rStep;
    private final Integer width, height;
    private final int phiDim;
    private final int rDim;
    private final float[] sinTable;
    private final float[] cosTable;

    public HoughTransformation_(float phiStep, float rStep, int width, int height) {
        println("HOUGH");
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

    private HoughAccumulator_ transform(List<Float> source) {

        HoughAccumulator_ acc = new HoughAccumulator_(rDim, phiDim, rStep, phiStep);
        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            if (source.get(y * width + x) != 0) {
              for (int phi = 0; phi < phiDim; phi++) {
                int radius = (int) (x * cosTable[phi] + y * sinTable[phi]);
                int rNormalized = radius + (rDim - 1) / 2;
                acc.accumulate(rNormalized, phi, 1);
              }
            }
          }
        }
        return acc;
    }

    @Override
    public HoughAccumulator_ apply(List<Float> source) {
        return this.transform(source);
    }

    //Accumulator of size radius x angle
   
}
public class HoughAccumulator_ implements HoughAccInterface{
  public final List<Integer> dataArray;
  public final int radius;
  public final int angle;
  public final float phiStep;
  public final float rStep;
  
  public int getRadius() { return radius; }
  public int getAngle() { return  angle; }
  public List<Integer> getDataArray() { return dataArray; }

  public HoughAccumulator_(int radius, int angle, float rStep, float phiStep) {
      this.dataArray = new ArrayList<Integer>(Collections.nCopies((radius + 2) * (angle + 2), 0));
      this.radius = radius;
      this.angle = angle;
      this.phiStep = phiStep;
      this.rStep = rStep;
  }

  //(radius, angle)
  public Pair<Integer, Integer> convertIndex(int i) {
      return new Pair<Integer, Integer>(i % radius, i / radius);
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
      return new Pair<Float, Float>(r, phi);
  }
}