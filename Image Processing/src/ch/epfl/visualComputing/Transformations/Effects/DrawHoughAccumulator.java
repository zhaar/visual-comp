package ch.epfl.visualComputing.Transformations.Effects;

import ch.epfl.visualComputing.CopeOut.Pair;
import ch.epfl.visualComputing.CopeOut.Triple;
import ch.epfl.visualComputing.ImageProcessing;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.util.List;

public class DrawHoughAccumulator {
    public static EffectFunction<Triple<List<Integer>, Integer, Integer>> drawHough(PApplet ctx) {
        return new EffectFunction<>(t -> {
            PImage houghed = ctx.createImage(t._3(), t._2(), PConstants.RGB);
            PImage result = ImageProcessing.applyImage(t._1(), houghed, ctx);
            result.resize(400, 400);
            result.updatePixels();
            ctx.image(result, 400,0);
        });
    }

    public static EffectFunction<Triple<List<Integer>, Integer, Integer>> drawLines(PApplet ctx) {
        return new EffectFunction<>(t -> {

        });
    }
}
