package ch.epfl.visualComputing.Transformations.Effects;

import ch.epfl.visualComputing.CopeOut.MyList;
import ch.epfl.visualComputing.CopeOut.Pair;
import ch.epfl.visualComputing.ImageProcessing;
import ch.epfl.visualComputing.Transformations.HoughTransformation;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.util.List;

public class DrawEffects {

    private DrawEffects() {}


    public static PImage applyImage(List<Float> pixels, PImage buffer, PApplet ctx) {
        for (int i = 0; i < buffer.pixels.length; i++) {
            buffer.pixels[i] = ctx.color(pixels.get(i));
        }
        buffer.updatePixels();
        return buffer;
    }

    public static PImage applyImageInt(List<Integer> pixels, PImage buffer, PApplet ctx) {
        for (int i = 0; i < buffer.pixels.length; i++) {
            buffer.pixels[i] = ctx.color(pixels.get(i));
        }
        buffer.updatePixels();
        return buffer;
    }

    public static EffectFunction<List<Float>> drawPixels(PApplet ctx, PImage buffer, int x, int y) {
        return new EffectFunction<>(ls -> {
            applyImage(ls, buffer, ctx);
            ctx.image(buffer, x, y);
            }
        );
    }

    public static EffectFunction<HoughTransformation.HoughAccumulator> drawHough(PApplet ctx, int x, int y) {
        return new EffectFunction<>(hough -> {
            PImage houghImg = ctx.createImage(hough.radius + 2, hough.angle + 2, PConstants.ALPHA);
            for (int i = 0; i < hough.dataArray.size(); i++) {
                houghImg.pixels[i] = ctx.color(PApplet.min(255, hough.dataArray.get(i)));
            }
            houghImg.resize(400, 400);
            houghImg.updatePixels();
            ctx.image(houghImg, x, y);
        });
    }

    public static EffectFunction<HoughTransformation.HoughAccumulator> drawLines(PApplet ctx, int left,  int width, int minVotes) {
        return new EffectFunction<>(hough -> {
            MyList.zipWithIndex(hough.dataArray).stream().filter(p -> p._1() > minVotes).forEach(p -> {
                int idx = p._2();
                System.out.println("drawing index : " + idx);
                int accPhi = (int) (idx / (hough.radius + 2)) - 1;
                int accR = idx % (hough.radius + 2);
                float r = (accR - (hough.radius - 1) * 0.5f) * hough.rStep;
                float phi = accPhi * hough.phiStep;
                drawLine(ctx, left, width,r , phi);
            });
        });
    }

    public static EffectFunction<List<Pair<Float, Float>>> drawLineArray(PApplet ctx, int left, int width) {
        return new EffectFunction<>(ls -> {
           ls.forEach(p -> drawLine(ctx, left, width, p._1(), p._2()));
        });
    }

    // copy pasted code from class
    public static void drawLine(PApplet ctx, int left, int width, float r, float phi) {
            int x0 = 0;
            int y0 = (int) (r / PApplet.sin(phi));
            int x1 = (int) (r / PApplet.cos(phi));
            int y1 = 0;
            int x2 = width;
            int y2 = (int) (-PApplet.cos(phi) / PApplet.sin(phi) * x2 + r / PApplet.sin(phi));
            int y3 = width;
            int x3 = (int) (-(y3 - r / PApplet.sin(phi)) * (PApplet.sin(phi) / PApplet.cos(phi)));
            // Finally, plot the lines
            ctx.stroke(204,0,200);
            if (y0 > 0) {
                if (x1 > 0)
                    ctx.line(x0, y0, x1, y1);
                else if (y2 > 0)
                    ctx.line(x0, y0, x2, y2);
                else
                    ctx.line(x0, y0, x3, y3);
            }
            else {
                if (x1 > 0) {
                    if (y2 > 0)
                        ctx.line(x1, y1, x2, y2);
                    else
                        ctx.line(x1, y1, x3, y3);
                }
                else
                    ctx.line(x2, y2, x3, y3);
            }
    }
}
