package ch.epfl.visualComputing;

import ch.epfl.visualComputing.Transformations.*;
import ch.epfl.visualComputing.Transformations.CopeOut.DepressingJava;
import ch.epfl.visualComputing.Transformations.Effects.DrawEffects;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.video.Capture;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ImageProcessing extends PApplet {

    PImage img;
    PImage buffer;


    public void settings() {
        size(800 * 3, 600);
    }

    public void setup() {
        img = loadImage("board3.jpg");

        buffer = createImage(img.width, img.height, RGB);

        noLoop();
    }

    public void draw() {
        image(img, 0, 0);
        computeAndDraw(img, buffer, this);
    }



    public static void computeAndDraw(PImage img, PImage buffer, PApplet ctx) {

        List<Integer> sourcePixels = DepressingJava.toIntList(img.pixels);
        ImageTransformation<Float, Float> blur = Convolution.gaussianBlur(img.width, img.height);

        //hue ranges: 112 - 133 or 70 - 144
        //brightness ranges: 0 - 153

        Function<List<Integer>, List<Float>> filtered = new PixelTransformer<>((Function<Integer, Float>) p -> {
            float b = ctx.brightness(p);
            float h = ctx.hue(p);
            float s = ctx.saturation(p);
            return s > 86
                    && 70 < h && h < 144
                    && 0 < b && b < 153
                    ? 255f : 0f;
        });

        ctx.image(img, 0, 0);
        filtered.andThen(blur)
                .andThen(Convolution.sobelDoubleConvolution(img.width, img.height))
                .andThen(DrawEffects.drawPixels(ctx, buffer, 1600, 0))
                .andThen(new HoughTransformation(0.06f, 2.5f, img.width, img.height))
                .andThen(DrawEffects.drawHough(ctx, 800, 0, 800, 600))
                .andThen(HoughClusters.mapToClusters(300, 20))
                .andThen(HoughClusters.selectBestLines(6))
                .andThen(DrawEffects.drawLineArray(ctx, 0, img.width))
                .andThen(ls -> ls.stream().map(p -> new PVector(p._1(), p._2())).collect(Collectors.toList()))
                .andThen(new QuadTransform(img.width, img.height))
                .andThen(DrawEffects.drawQuads(ctx))
                .apply(sourcePixels);
    }
}
