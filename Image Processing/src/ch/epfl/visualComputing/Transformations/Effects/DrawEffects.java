package ch.epfl.visualComputing.Transformations.Effects;

import ch.epfl.visualComputing.CopeOut.DepressingJava;
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

    public static EffectFunction<Triple<List<Integer>, Integer, Integer>> drawLines(PApplet ctx) {
        return new EffectFunction<>(t -> {

        });
    }
}
