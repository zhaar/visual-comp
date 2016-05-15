package ch.epfl.visualComputing.Transformations.Effects;

import ch.epfl.visualComputing.CopeOut.DepressingJava;
import ch.epfl.visualComputing.CopeOut.MyList;
import ch.epfl.visualComputing.CopeOut.Pair;
import ch.epfl.visualComputing.CopeOut.Triple;
import ch.epfl.visualComputing.ImageProcessing;
import ch.epfl.visualComputing.Transformations.HoughTransformation;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.util.List;

public class DrawEffects {

    private DrawEffects() {}

    public static EffectFunction<HoughTransformation.HoughAccumulator> drawHough(PApplet ctx) {
        return new EffectFunction<>(t -> {
            PImage houghed = ctx.createImage(t.radius, t.angle, PConstants.RGB);
            PImage result = ImageProcessing.applyImage(t.dataArray, houghed, ctx);
            result.resize(400, 400);
            result.updatePixels();
            ctx.image(result, 400,0);
        });
    }

    public static EffectFunction<HoughTransformation.HoughAccumulator> drawLines(PApplet ctx, int width, int height) {
        return new EffectFunction<>(hough -> {
            for (Pair<Integer, Integer> p : MyList.zipWithIndex(hough.dataArray)) {

                if (p._1() > 200) {
                    int idx = p._2();
                    int rDim = hough.radius;
                    float discretizationStepsR = hough.rStep;
                    float discretizationStepsPhi = hough.phiStep;

                    int accPhi = (int) (idx / rDim) - 1;
                    int accR = idx - (accPhi + 1) * rDim - 1;
                    float r = (accR - (rDim - 1) * 0.5f) * discretizationStepsR;
                    float phi = accPhi * discretizationStepsPhi;

                    int x0 = 0;
                    int y0 = (int) (r / PApplet.sin(phi));
                    int x1 = (int) (r / PApplet.cos(phi));
                    int y1 = 0;
                    int x2 = width;
                    int y2 = (int) (-PApplet.cos(phi) / PApplet.sin(phi) * x2 + r / PApplet.sin(phi));
                    int y3 = width;
                    int x3 = (int) (-(y3 - r / PApplet.sin(phi)) * (PApplet.sin(phi) / PApplet.cos(phi)));

                    ctx.stroke(204, 102, 0);
                    if (y0 > 0) {
                        if (x1 > 0)
                            ctx.line(x0, y0, x1, y1);
                        else if (y2 > 0)
                            ctx.line(x0, y0, x2, y2);
                        else
                            ctx.line(x0, y0, x3, y3);
                    } else {
                        if (x1 > 0) {
                            if (y2 > 0)
                                ctx.line(x1, y1, x2, y2);
                            else
                                ctx.line(x1, y1, x3, y3);
                        } else {
                            ctx.line(x2, y2, x3, y3);
                        }
                    }
                }

            }
        });
    }
}
