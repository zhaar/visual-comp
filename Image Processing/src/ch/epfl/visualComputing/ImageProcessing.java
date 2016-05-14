package ch.epfl.visualComputing;

import ch.epfl.visualComputing.CopeOut.DepressingJava;
import ch.epfl.visualComputing.CopeOut.Pair;
import ch.epfl.visualComputing.CopeOut.Triple;
import ch.epfl.visualComputing.Transformations.*;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class ImageProcessing extends PApplet {

    PImage img;

    HScrollbar upperThreshold = new HScrollbar(this, 0, 0, 800, 12);
    HScrollbar lowerThreshold = new HScrollbar(this, 0, 15, 800, 12);

    public void settings() {
        size(800, 800);
    }

    public void setup() {
        img = loadImage("board1.jpg");
        noLoop();
    }

    public void draw() {
        image(computeImage(img), 0, 0);
        upperThreshold.update();
        lowerThreshold.update();
        upperThreshold.display();
        lowerThreshold.display();
    }

    public PImage computeImage(PImage img) {
        PImage buffer = createImage(img.width, img.height, RGB);

        float low = lowerThreshold.getPos() * 255;
        float high = upperThreshold.getPos() * 255;
        System.out.println("low: "+low + " high:" + high);

        List<Float> sourceBrightness = DepressingJava.toIntList(img.pixels)
                .stream().map(this::brightness).collect(Collectors.toList());
        List<Float> sobeled = Convolution.gaussianBlur(img.width, img.height)
                .andThen(new PixelTransformer<>(p -> Float.compare(p, 128) == -1 ? 250f : 0f))
                .andThen(Convolution.sobelDoubleConvolution(img.width, img.height))
                .apply(sourceBrightness);
//        sourceBrightness = new PixelTransformer<Float, Float>(p -> low < p && p < high ? 250f : 0).transform(sourceBrightness);

//        List<Float> sobeled = Convolution.sobelDoubleConvolution(img.width, img.height).transform(blurred);
        float max = sobeled.parallelStream().max((l, r) -> l < r ? -1 : 1).get();
        System.out.println("ma value: " + max);
        sobeled = sobeled.parallelStream().map(v -> v > max * 0.25f ? 255f : 0f).collect(Collectors.toList());

        Triple<List<Integer>, Integer, Integer> hough = new HoughTransformation(0.06f, 2.5f, img.width, img.height).apply(sobeled);
        PImage houghed = createImage(hough._3(), hough._2(), RGB);

        // You may want to resize the accumulator to make it easier to see:
        // houghImg.resize(400, 400);

        PImage resultImage = applyImage(hough._1(), houghed);
        resultImage.resize(400, 400);
        resultImage.updatePixels();

        return resultImage;
    }

    public PImage applyImage(List<Integer> pixels, PImage buffer) {
        for (int i = 0; i < buffer.pixels.length; i++) {
            buffer.pixels[i] = color(pixels.get(i));
        }
        return buffer;
    }

}
