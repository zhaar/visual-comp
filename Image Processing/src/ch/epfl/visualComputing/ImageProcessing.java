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

    PImage img1;
    Capture cam;
    PImage buffer1;


    private static final boolean webcam = false;

    public void settings() {
        size(800 * 3, 600);
    }

    public void setup() {
        if (webcam) {
            String[] cameras = Capture.list();
            if (cameras.length == 0) {
                println("There are no cameras available for capture.");
                exit();
            } else {
                println("Available cameras:");
                for (String camera : cameras) {
                    println(camera);
                }
                cam = new Capture(this, cameras[0]);
                cam.start();
            }
        } else {
            img1 = loadImage("board1.jpg");

            buffer1 = createImage(img1.width, img1.height, RGB);

            noLoop();
        }
    }

    public void draw() {
        if (webcam) {
            if (cam.available()) {
                cam.read();
            }
            img1 = cam.get();
            image(img1, 0, 0);
        } else {
            image(img1, 0, 0);
            computeAndDraw(img1, buffer1, this);
        }
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
                .andThen(HoughClusters.mapToClusters(200, 10))
                .andThen(HoughClusters.selectBestLines(6))
                .andThen(DrawEffects.drawLineArray(ctx, 0, img.width))
                .andThen(ls -> ls.stream().map(p -> new PVector(p._1(), p._2())).collect(Collectors.toList()))
                .andThen(new QuadTransform(img.width, img.height))
                .andThen(DrawEffects.drawQuads(ctx))
                .apply(sourcePixels);
    }
}
