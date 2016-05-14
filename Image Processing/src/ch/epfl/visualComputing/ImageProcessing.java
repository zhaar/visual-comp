package ch.epfl.visualComputing;

import ch.epfl.visualComputing.CopeOut.DepressingJava;
import ch.epfl.visualComputing.CopeOut.Triple;
import ch.epfl.visualComputing.Transformations.Convolution;
import ch.epfl.visualComputing.Transformations.Effects.DrawHoughAccumulator;
import ch.epfl.visualComputing.Transformations.Effects.EffectFunction;
import ch.epfl.visualComputing.Transformations.HoughTransformation;
import ch.epfl.visualComputing.Transformations.PixelTransformer;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.List;
import java.util.stream.Collectors;


public class ImageProcessing extends PApplet {

    PImage img;

    HScrollbar upperThreshold = new HScrollbar(this, 0, 0, 800, 12);
    HScrollbar lowerThreshold = new HScrollbar(this, 0, 15, 800, 12);

    public void settings() {
        size(1200, 400);
    }

    public void setup() {
        img = loadImage("board1.jpg");
        noLoop();
    }

    public void draw() {
        computeImage(img);
        upperThreshold.update();
        upperThreshold.display();
    }

    public void computeImage(PImage img) {

        image(img,0, 0, 400, 400);
        List<Float> sourceBrightness = DepressingJava.toIntList(img.pixels)
                .stream().map(this::brightness).collect(Collectors.toList());
        Triple<List<Integer>, Integer, Integer> processed = Convolution.gaussianBlur(img.width, img.height)
                .andThen(new PixelTransformer<>(p -> Float.compare(p, 128) < 0 ? 250f : 0f))
                .andThen(Convolution.sobelDoubleConvolution(img.width, img.height))
                .andThen(new HoughTransformation(0.06f, 2.5f, img.width, img.height))
                .andThen(DrawHoughAccumulator.drawHough(this))
                .apply(sourceBrightness);
    }

    public static PImage applyImage(List<Integer> pixels, PImage buffer, PApplet ctx) {
        for (int i = 0; i < buffer.pixels.length; i++) {
            buffer.pixels[i] = ctx.color(pixels.get(i));
        }
        return buffer;
    }

}
